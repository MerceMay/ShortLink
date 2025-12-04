package com.mercemay.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mercemay.shortlink.admin.common.constant.RedisCacheConstant;
import com.mercemay.shortlink.admin.common.convention.exception.ClientException;
import com.mercemay.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.mercemay.shortlink.admin.dao.entity.UserDO;
import com.mercemay.shortlink.admin.dao.mapper.UserMapper;
import com.mercemay.shortlink.admin.dto.req.UserLoginReqDTO;
import com.mercemay.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.mercemay.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.mercemay.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.mercemay.shortlink.admin.dto.resp.UserRespDTO;
import com.mercemay.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 用户接口实现层
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public UserRespDTO getUserByName(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new ClientException(UserErrorCodeEnum.USER_NULL);
        }
        UserRespDTO result = new UserRespDTO();
        BeanUtils.copyProperties(userDO, result);
        return result;
    }

    @Override
    public Boolean hasUserName(String username) {
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void register(UserRegisterReqDTO requestParam) {
        if (hasUserName(requestParam.getUsername())) {
            throw new ClientException(UserErrorCodeEnum.USER_NAME_EXIST);
        }
        RLock lock = redissonClient.getLock(RedisCacheConstant.LOCK_USER_REGISTER_KEY + requestParam.getUsername());
        try {
            if (lock.tryLock()) {
                int inserted = baseMapper.insert(BeanUtil.toBean(requestParam, UserDO.class));
                if (inserted < 1) {
                    throw new ClientException(UserErrorCodeEnum.USER_SAVE_ERROR);
                }
                userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
                return;
            }
            throw new ClientException(UserErrorCodeEnum.USER_NAME_EXIST);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void updateByUsername(UserUpdateReqDTO requestParam) {
        // TODO 验证当前用户是否为登录用户
        LambdaUpdateWrapper<UserDO> updateWrapper =
                Wrappers.lambdaUpdate(UserDO.class)
                        .eq(UserDO::getUsername, requestParam.getUsername());
        baseMapper.update(BeanUtil.toBean(requestParam, UserDO.class), updateWrapper);
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername())
                .eq(UserDO::getPassword, requestParam.getPassword())
                .eq(UserDO::getDelFlag, 0);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new ClientException(UserErrorCodeEnum.USER_NOT_EXIST);
        }
        Boolean isLogin = stringRedisTemplate.hasKey("login:" + requestParam.getUsername());
        if (isLogin) {
            throw new ClientException(UserErrorCodeEnum.USER_HAVE_LOGIN);
        }
        String uuid = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put("login:" + requestParam.getUsername(), "token", uuid);
        stringRedisTemplate.expire("login:" + requestParam.getUsername(), 30L, TimeUnit.MINUTES);
        return new UserLoginRespDTO(uuid);
    }

    @Override
    public Boolean checkLogin(String username, String token) {
        return stringRedisTemplate.opsForHash().get("login:" + username, "token") != null;
    }

    @Override
    public void logout(String username, String token) {
        if (checkLogin(username, token)) {
            stringRedisTemplate.delete("login:" + username);
            return;
        }
        throw new ClientException(UserErrorCodeEnum.USER_NOT_LOGIN);
    }
}
