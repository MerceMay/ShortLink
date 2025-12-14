-- 设置用户访问频率限制参数
local username = KEYS[1]
local timeWindow = tonumber(ARGV[1]) -- 时间窗口，单位为秒

-- 构造Redis键
local redisKey = "shortlink:user:traffic:control:" .. username

-- 原子递增访问计数，并获取递增后的值
local currentCount = redis.call("INCR", redisKey)

-- 设置键的过期时间
if currentCount == 1 then
    redis.call("EXPIRE", redisKey, timeWindow)
end

-- 返回当前访问计数
return currentCount