# ShortLink 项目技术亮点总结

> 本文档基于项目全部源码的深度阅读，总结出值得写进简历的核心技术亮点，面向 Java 后端开发岗位（中国互联网公司校招/社招）。

---

## 亮点 1：基于 MurmurHash + Base62 的短链接生成算法与冲突解决机制

**涉及文件**：
- `project/src/main/java/com/mercemay/shortlink/project/util/HashUtil.java`
- `project/src/main/java/com/mercemay/shortlink/project/service/impl/ShortLinkServiceImpl.java`（`generateSuffix` 方法，第 455-471 行）

**问题背景**：
短链接系统的核心挑战之一是如何将一个长 URL 压缩成一个短字符串（6-7 个字符），同时保证全局唯一性。如果直接使用数据库自增 ID 进行编码，存在 ID 可预测、安全性差的问题；如果使用 UUID，长度过长无法满足"短"的要求。

**实现细节**：
- `HashUtil.hashToBase62(String str)` 使用 Hutool 提供的 **MurmurHash（32 位版本）** 对原始 URL 进行哈希计算，得到一个 int 值，再将其转为无符号 long，最后通过 `convertDecToBase62` 方法进行 **Base62 编码**（字符集为 `0-9A-Za-z`，共 62 个字符），生成 6 位左右的短链接后缀。
- MurmurHash 的特点是：非加密型哈希，计算速度极快，分布均匀，非常适合短链接场景。
- **哈希冲突解决**（`generateSuffix` 方法）：在原始 URL 后拼接 `UUID.randomUUID()`，使得每次哈希的输入不同，然后通过 **布隆过滤器**（Redisson `RBloomFilter`）快速判断生成的短链接是否已被占用。如果冲突则重试，最多重试 10 次，从概率上几乎消除了冲突。

```java
private String generateSuffix(ShortLinkCreateReqDTO requestParam) {
    int retryCount = 0;
    String shortUri;
    while (true) {
        if (retryCount > 10) {
            throw new ServiceException("短链接生成失败，请稍后重试");
        }
        String originUrl = requestParam.getOriginUrl();
        originUrl += UUID.randomUUID().toString(); // 每次拼接随机串，避免相同URL生成相同哈希
        shortUri = HashUtil.hashToBase62(originUrl);
        if (!shortUriCreateCachePenetrationBloomFilter.contains(createShortLinkDefaultDomain + "/" + shortUri)) {
            break; // 布隆过滤器判断不存在，跳出
        }
        retryCount++;
    }
    return shortUri;
}
```

- 项目还提供了一个 **基于分布式锁的备选方案** `createShortLinkByLock`（第 131-182 行），通过 Redisson 分布式锁 + 数据库查询来保证唯一性，适用于布隆过滤器不可用时的降级场景。两种方案的对比体现了对"布隆过滤器有误判率"这一缺陷的工程思考。

**技术价值**：
这个设计展示了对哈希算法选型（MurmurHash vs MD5 vs SHA）、编码压缩（Base62）、冲突解决策略（随机盐 + 布隆过滤器 + 重试机制）的综合理解，体现了在高并发场景下兼顾性能与正确性的工程能力。

---

## 亮点 2：路由表 + 详情表的双表分库分表架构设计

**涉及文件**：
- `project/src/main/resources/shardingsphere-config-dev.yml`
- `project/src/main/java/com/mercemay/shortlink/project/dao/entity/ShortLinkDO.java`（`t_link` 表）
- `project/src/main/java/com/mercemay/shortlink/project/dao/entity/ShortLinkRouteDO.java`（`t_link_route` 表）
- `project/src/main/java/com/mercemay/shortlink/project/service/impl/ShortLinkServiceImpl.java`（`redirectUrl` 方法，第 328-397 行）
- `volumes/mysql/init/01_schema.sql`

**问题背景**：
短链接表 `t_link` 的分片键是 `gid`（分组标识），因为管理端需要按分组来查询、分页展示短链接。但在短链接跳转场景中，请求只携带短链接（`full_short_url`），不携带 `gid`，这导致**跳转查询无法命中分片**，必须进行全表扫描（扇出查询），性能极差。

**实现细节**：
- **解决方案**：设计了一个轻量级的 **路由表 `t_link_route`**，以 `full_short_url` 作为分片键，HASH_MOD 分 16 片。路由表只存三个字段：`id`、`gid`、`full_short_url`，通过 `full_short_url` 可以精确定位到哪一个分片。
- **跳转链路**：请求进来 → 先查 `t_link_route`（以 `full_short_url` 精确路由到一个分片）获取 `gid` → 再用 `gid` + `full_short_url` 查 `t_link`（同样精确路由到一个分片），两次查询都是单分片精确查询，无需扇出。

```yaml
# ShardingSphere 分表配置
t_link:
  actualDataNodes: ds_0.t_link_${0..15}
  tableStrategy:
    standard:
      shardingColumn: gid              # 详情表按 gid 分片
      shardingAlgorithmName: link_table_hash_mod
t_link_route:
  actualDataNodes: ds_0.t_link_route_${0..15}
  tableStrategy:
    standard:
      shardingColumn: full_short_url   # 路由表按 full_short_url 分片
      shardingAlgorithmName: link_route_table_hash_mod
```

- 路由表的唯一键是 `full_short_url`，详情表的唯一键是 `(full_short_url, del_time)` 的联合唯一索引，`del_time` 的设计是为了支持逻辑删除后同一短链接可以被重新创建。
- 创建和更新短链接时，两张表在同一个事务中同步写入/更新（`@Transactional`）。

**技术价值**：
"路由表 + 详情表"是分库分表场景中解决"跨维度查询"的经典设计模式。这个亮点体现了对分库分表核心矛盾（分片键只能有一个，但业务查询维度有多个）的深刻理解，以及用空间换时间的工程取舍能力。

---

## 亮点 3：缓存穿透/击穿/雪崩的三层防御体系

**涉及文件**：
- `project/src/main/java/com/mercemay/shortlink/project/service/impl/ShortLinkServiceImpl.java`（`redirectUrl` 方法，第 328-397 行）
- `project/src/main/java/com/mercemay/shortlink/project/config/RBloomFilterConfiguration.java`
- `project/src/main/java/com/mercemay/shortlink/project/common/constant/RedisKeyConstant.java`
- `project/src/main/java/com/mercemay/shortlink/project/util/LinkUtil.java`（`getLinkCacheValidDate` 方法）

**问题背景**：
短链接跳转是一个极高频的读操作。如果大量不存在的短链接请求打到数据库（缓存穿透），或热点短链接缓存失效后大量并发请求同时穿透到数据库（缓存击穿），都会导致数据库压力骤增甚至宕机。

**实现细节**：

**（1）防缓存穿透 — 布隆过滤器 + 空值缓存双保险**
- 使用 Redisson 的 `RBloomFilter`，初始化容量 1 亿、误判率 0.001，在短链接创建时将 `fullShortUrl` 写入布隆过滤器。
- 跳转时首先查布隆过滤器，不存在则直接返回 404，过滤掉绝大多数无效请求。
- 对于布隆过滤器判断"可能存在"但数据库实际不存在的情况（误判），会设置一个 **空值缓存**（key: `shortlink:route:null` + fullShortUrl，value: `-`，TTL: 30 分钟），后续相同请求直接返回 404。

```java
// 第1层：直接查Redis缓存
String originLink = stringRedisTemplate.opsForValue().get(SHORT_LINK_ROUTE_KEY + fullShortUrl);
if (StrUtil.isNotBlank(originLink)) { ... sendRedirect(originLink); return; }

// 第2层：布隆过滤器快速判断
boolean contains = shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl);
if (!contains) { ... sendRedirect("/page/notfound"); return; }

// 第3层：空值缓存判断
String nullShortLink = stringRedisTemplate.opsForValue().get(SHORT_LINK_NULL_ROUTE_KEY + fullShortUrl);
if (StrUtil.isNotBlank(nullShortLink)) { ... sendRedirect("/page/notfound"); return; }
```

**（2）防缓存击穿 — Redisson 分布式锁 + 双重检查**
- 通过 `redissonClient.getLock(SHORT_LINK_ROUTE_LOCK + fullShortUrl)` 获取分布式锁。
- 获取锁后再次检查 Redis 缓存和空值缓存（双重检查），避免重复加载。
- 只有一个线程会穿透到数据库加载数据并回填缓存，其他线程等待锁释放后直接读缓存。

**（3）防缓存雪崩 — 差异化过期时间**
- `LinkUtil.getLinkCacheValidDate()` 方法根据短链接的有效期动态计算缓存 TTL。有有效期的短链接按实际剩余时间设置 TTL；永久有效的短链接设置 30 天缓存，天然实现了过期时间的分散。

**技术价值**：
缓存穿透/击穿/雪崩是后端面试的高频考点。这个实现不是简单的教科书方案，而是将布隆过滤器、空值缓存、分布式锁、双重检查、差异化 TTL 五种策略组合使用，形成了完整的三层防御体系。

---

## 亮点 4：RocketMQ 异步统计 + 消息幂等性保障方案

**涉及文件**：
- `project/src/main/java/com/mercemay/shortlink/project/mq/producer/ShortLinkStatsSaveProducer.java`
- `project/src/main/java/com/mercemay/shortlink/project/mq/consumer/ShortLinkStatsSaveConsumer.java`
- `project/src/main/java/com/mercemay/shortlink/project/mq/idempotent/MessageQueueIdempotentHandler.java`
- `project/src/main/java/com/mercemay/shortlink/project/common/constant/RedisKeyConstant.java`

**问题背景**：
短链接每次被访问时需要记录 PV/UV/IP/地区/浏览器/设备/操作系统/网络等多维度统计数据。如果在跳转请求中同步写入这些统计数据（涉及 8 张表的写入），会严重拖慢跳转响应速度。

**实现细节**：

**（1）异步解耦 — RocketMQ 消息队列**
- 跳转时只做两件事：①从缓存/DB 读取原始 URL 进行 302 重定向；②采集统计数据并发送到 RocketMQ 消息队列。
- 消费者 `ShortLinkStatsSaveConsumer` 异步消费消息，执行 8 张统计表的写入操作。
- 生产者使用**同步发送**（`rocketMQTemplate.syncSend`），超时 2 秒，失败重试 1 次，保证消息可靠投递。

**（2）消息幂等性 — 基于 Redis 的三状态幂等处理器**
- `MessageQueueIdempotentHandler` 通过 Redis 的 `setIfAbsent`（SETNX）原子操作实现消息去重。
- 每条消息有三种状态：
  - **未消费**：key 不存在，`setIfAbsent` 返回 true
  - **消费中**：key 存在且 value="0"，表示消息正在处理中
  - **已完成**：key 存在且 value="1"，表示消息处理完成
- 消费成功后通过 `setAccomplish(keys)` 将值设为 "1"；如果消费异常，通过 `delMessageIdempotentKey(keys)` 删除幂等标识，允许消息重新消费。
- 幂等 key 的 TTL 设置为 2 分钟，避免永久占用 Redis 内存。

```java
public void onMessage(Map<String, String> produceMap) {
    String keys = produceMap.get("keys");
    if (messageQueueIdempotentHandler.isMessageBeingConsumed(keys)) {
        if (messageQueueIdempotentHandler.isAccomplish(keys)) {
            return; // 已处理完成，直接跳过
        }
        throw new ServiceException("消息未完成流程，需要消息队列重试"); // 消费中但未完成，抛异常触发重试
    }
    try {
        processMessage(...); // 处理业务逻辑
    } catch (Throwable ex) {
        messageQueueIdempotentHandler.delMessageIdempotentKey(keys); // 异常时删除幂等标识
        throw ex;
    }
    messageQueueIdempotentHandler.setAccomplish(keys); // 标记完成
}
```

**技术价值**：
这个设计完整展示了"MQ 异步解耦 + 消息幂等性"的工程实践，特别是三状态幂等处理器的设计（未消费/消费中/已完成），解决了消费半途失败导致消息无法重试的问题。这是中高级后端开发的必备技能。

---

## 亮点 5：分布式读写锁解决分组变更与统计写入的并发冲突

**涉及文件**：
- `project/src/main/java/com/mercemay/shortlink/project/service/impl/ShortLinkServiceImpl.java`（`updateShortLink` 方法，第 267-311 行）
- `project/src/main/java/com/mercemay/shortlink/project/mq/consumer/ShortLinkStatsSaveConsumer.java`（`processMessage` 方法，第 78-175 行）

**问题背景**：
当用户修改短链接的分组（gid）时，需要在 `t_link` 表中删除旧分片的记录并在新分片插入新记录，同时更新 `t_link_route` 路由表。但此时如果有统计写入（消费者）正在使用旧的 gid 写入统计数据，就会导致数据不一致。

**实现细节**：
- 使用 Redisson 的 **`RReadWriteLock`**（分布式读写锁），锁 key 为 `shortlink:lock:update:gid:` + fullShortUrl。
- **更新分组操作**（`updateShortLink`）获取 **写锁**（`readWriteLock.writeLock()`），执行删除旧记录 + 插入新记录 + 更新路由表的操作。
- **统计写入操作**（`processMessage`）获取 **读锁**（`readWriteLock.readLock()`），在读锁保护下查询路由表获取最新 gid 并写入统计数据。
- 读写锁的语义：多个统计写入可以并行执行（读锁共享），但分组变更操作会排斥所有统计写入（写锁排他），保证数据一致性。

```java
// 更新操作 - 获取写锁
RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(SHORT_LINK_UPDATE_GID_LOCK + requestParam.getFullShortUrl());
RLock rLock = readWriteLock.writeLock();
rLock.lock();
try {
    // 删除旧分组记录，插入新分组记录，更新路由表
} finally {
    rLock.unlock();
}

// 统计消费者 - 获取读锁
RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(SHORT_LINK_UPDATE_GID_LOCK + fullShortUrl);
RLock rLock = readWriteLock.readLock();
rLock.lock();
try {
    // 查路由表获取 gid，写入统计数据
} finally {
    rLock.unlock();
}
```

**技术价值**：
使用分布式读写锁（而非简单的互斥锁）来解决"低频写操作"与"高频统计写入"之间的并发冲突，既保证了数据一致性，又最大化了统计写入的并发度。这体现了对并发控制粒度精细化设计的能力。

---

## 亮点 6：基于 INSERT ON DUPLICATE KEY UPDATE 的统计数据 Upsert 方案

**涉及文件**：
- `project/src/main/java/com/mercemay/shortlink/project/dao/mapper/LinkAccessStatsMapper.java`
- `project/src/main/java/com/mercemay/shortlink/project/dao/mapper/LinkLocaleStatsMapper.java`
- `project/src/main/java/com/mercemay/shortlink/project/dao/mapper/LinkStatsTodayMapper.java`
- `project/src/main/resources/mapper/LinkMapper.xml`（`incrementStats`）
- `volumes/mysql/init/01_schema.sql`

**问题背景**：
统计数据需要"不存在则插入，存在则累加"的语义。如果先 SELECT 再决定 INSERT 或 UPDATE，在高并发下会有竞态条件（两个线程都 SELECT 不存在，然后都 INSERT 导致主键冲突）。

**实现细节**：
- 所有统计表都利用 MySQL 的 **`INSERT ... ON DUPLICATE KEY UPDATE`** 语法实现原子性的 Upsert 操作。
- 每张统计表都设计了精心选择的**联合唯一键**，作为 Upsert 的判断依据：

| 统计表 | 唯一键组合 | 统计粒度 |
|--------|-----------|---------|
| `t_link_access_stats` | `(full_short_url, date, hour)` | 每小时访问量 |
| `t_link_locale_stats` | `(full_short_url, date, adcode, province)` | 每日地区访问量 |
| `t_link_os_stats` | `(full_short_url, date, os)` | 每日操作系统 |
| `t_link_browser_stats` | `(full_short_url, date, browser)` | 每日浏览器 |
| `t_link_device_stats` | `(full_short_url, date, device)` | 每日设备类型 |
| `t_link_network_stats` | `(full_short_url, date, network)` | 每日网络类型 |
| `t_link_stats_today` | `(full_short_url, date)` | 每日汇总统计 |

```sql
-- 访问统计 Upsert 示例
INSERT INTO t_link_access_stats (full_short_url, date, pv, uv, uip, hour, weekday, create_time, update_time, del_flag)
VALUES(#{...})
ON DUPLICATE KEY UPDATE pv = pv + #{pv}, uv = uv + #{uv}, uip = uip + #{uip};
```

- `t_link` 表本身的 PV/UV/UIP 累加通过自定义 XML Mapper 的 `incrementStats` 方法实现，使用 `SET total_pv = total_pv + #{totalPv}` 的原子自增。

**技术价值**：
这个设计展示了对"写密集型统计场景"的工程处理能力：通过合理的唯一键设计 + MySQL Upsert 语法，用一条 SQL 完成"判断+插入/更新"的原子操作，避免了分布式锁或乐观锁的额外开销。

---

## 亮点 7：基于 Redis Set + Cookie 的 UV/UIP 去重方案

**涉及文件**：
- `project/src/main/java/com/mercemay/shortlink/project/service/impl/ShortLinkServiceImpl.java`（`buildLinkStatsRecordAndSetUser` 方法，第 406-452 行）
- `project/src/main/java/com/mercemay/shortlink/project/common/constant/RedisKeyConstant.java`

**问题背景**：
UV（独立访客）和 UIP（独立 IP）的统计需要去重。PV 每次访问加 1 即可，但 UV 和 UIP 需要判断是否"首次访问"。如果每次都查数据库判断，性能无法接受。

**实现细节**：
- **UV 去重**：利用 Cookie 给每个访客生成唯一标识（UUID），存储在名为 `uv` 的 Cookie 中（有效期 30 天）。将每个访客标识通过 `stringRedisTemplate.opsForSet().add()` 写入 Redis Set（key: `shortlink:stats:uv:` + fullShortUrl），如果 `SADD` 返回 1（新增成功），说明是首次访问，`uvFirstFlag = true`。
- **UIP 去重**：通过请求头获取真实 IP，同样用 Redis Set 的 `SADD` 操作（key: `shortlink:stats:uip:` + fullShortUrl），返回 1 则是首次 IP 访问。
- 这两个 flag 随消息发送给消费者，消费者根据 flag 决定是否将 UV/UIP 计数加 1。

```java
// UV 判断逻辑
Long uvAdded = stringRedisTemplate.opsForSet().add(STATS_UV_KEY + fullShortUrl, uv.get());
uvFirstFlag.set(uvAdded != null && uvAdded > 0L);

// UIP 判断逻辑
Long uipAdded = stringRedisTemplate.opsForSet().add(STATS_UIP_KEY + fullShortUrl, remoteAddr);
boolean uipFirstFlag = uipAdded != null && uipAdded > 0L;
```

**技术价值**：
利用 Redis Set 的天然去重特性和 `SADD` 操作的原子返回值来判断是否首次访问，时间复杂度 O(1)，巧妙地将"去重判断"和"去重记录"合并为一个原子操作。结合 Cookie 方案追踪匿名用户，是 UV 统计的标准工程实践。

---

## 亮点 8：基于 Lua 脚本的用户维度接口限流方案

**涉及文件**：
- `admin/src/main/resources/lua/user_traffic_risk_control.lua`
- `admin/src/main/java/com/mercemay/shortlink/admin/common/biz/user/UserTrafficRiskControlFilter.java`
- `admin/src/main/java/com/mercemay/shortlink/admin/config/UserTrafficRiskControlConfiguration.java`
- `admin/src/main/resources/application.yml`

**问题背景**：
SaaS 平台需要防止单个用户恶意刷接口（如暴力注册、批量创建短链接），需要一个精确到用户维度的限流方案。Sentinel 适合做全局限流，但用户维度的限流需要自定义实现。

**实现细节**：
- 使用 **Redis Lua 脚本**实现滑动窗口限流，保证计数和过期的原子性。

```lua
local username = KEYS[1]
local timeWindow = tonumber(ARGV[1])
local redisKey = "shortlink:user:traffic:control:" .. username
local currentCount = redis.call("INCR", redisKey)
if currentCount == 1 then
    redis.call("EXPIRE", redisKey, timeWindow)
end
return currentCount
```

- `UserTrafficRiskControlFilter` 作为 Servlet Filter，在请求进入 Controller 之前执行 Lua 脚本。
- 配置化参数：`time-window: 1`（1 秒窗口）、`max-requests: 20`（单用户每秒最多 20 次请求）。
- 当 `currentCount > maxRequests` 时，直接返回限流错误响应，不再进入后续业务逻辑。

**技术价值**：
相比简单的 Java 内存计数器（单机限流），Lua 脚本在 Redis 中执行保证了分布式环境下的原子性和一致性。相比 Sentinel（全局维度），这是用户级别的精细化限流。Lua 脚本 + Filter 链的方案也是业界常用的限流实践。

---

## 亮点 9：ShardingSphere 数据加密 — 用户敏感信息透明加密存储

**涉及文件**：
- `admin/src/main/resources/shardingsphere-config-dev.yml`（`ENCRYPT` 规则配置）
- `admin/src/main/java/com/mercemay/shortlink/admin/dao/entity/UserDO.java`

**问题背景**：
用户表中存储了手机号和邮箱等敏感信息，根据《个人信息保护法》和安全最佳实践，这些数据不应以明文形式存储在数据库中。

**实现细节**：
- 利用 ShardingSphere 的 **ENCRYPT（数据加密）** 功能，对 `t_user` 表的 `phone` 和 `mail` 字段配置 AES 加密。
- 写入时自动加密，读取时自动解密，对业务代码完全透明，Java 实体类（`UserDO`）无需任何改动。

```yaml
- !ENCRYPT
  tables:
    t_user:
      columns:
        phone:
          cipherColumn: phone
          encryptorName: common_encryptor
        mail:
          cipherColumn: mail
          encryptorName: common_encryptor
      queryWithCipherColumn: true
  encryptors:
    common_encryptor:
      type: AES
      props:
        aes-key-value: vjEC4vMqQzdN4ocIK4KJ6Jv1RcixZaCA
```

**技术价值**：
利用 ShardingSphere 的声明式加密能力，实现了"零侵入"的敏感数据保护方案。这种基于中间件的透明加密方式，相比在 Service 层手动加解密更优雅、更安全，体现了对数据安全合规和框架能力的深入了解。

---

## 亮点 10：逻辑删除 + del_time 联合唯一索引的软删除方案

**涉及文件**：
- `project/src/main/java/com/mercemay/shortlink/project/dao/entity/ShortLinkDO.java`（`delTime` 字段）
- `project/src/main/java/com/mercemay/shortlink/project/service/impl/RecycleBinServiceImpl.java`
- `project/src/main/java/com/mercemay/shortlink/project/service/impl/ShortLinkServiceImpl.java`（`updateShortLink` 方法）
- `volumes/mysql/init/01_schema.sql`（`t_link` 表唯一索引定义）

**问题背景**：
短链接使用逻辑删除（`del_flag = 0/1`），但如果唯一索引只包含 `full_short_url`，那么被逻辑删除的记录仍然占用唯一索引，导致同一短链接无法被重新创建。

**实现细节**：
- `t_link` 表的唯一索引为 `(full_short_url, del_time)` 联合唯一索引。
- 未删除记录的 `del_time = 0`，逻辑删除时设置 `del_time = System.currentTimeMillis()`。
- 因为时间戳是唯一的，所以即使同一个 `full_short_url` 被多次删除和重建，也不会违反唯一约束。

```java
// 逻辑删除时设置删除时间戳
ShortLinkDO deletedShortLinkDO = ShortLinkDO.builder()
        .delTime(System.currentTimeMillis())
        .build();
deletedShortLinkDO.setDelFlag(1);
baseMapper.update(deletedShortLinkDO, updateWrapper);
```

- 在分组变更场景中（`updateShortLink`），也是先逻辑删除旧分组的记录，再插入新分组的记录，`del_time` 保证了不会冲突。

**技术价值**：
这是一个巧妙的工程设计：用 `del_time` 时间戳代替简单的 `del_flag` 参与唯一索引，既保留了逻辑删除的审计价值，又解决了"逻辑删除后唯一索引冲突"的经典难题。这在实际项目中非常实用。

---

## 亮点 11：Spring Cloud Gateway 网关层 Token 鉴权与用户信息透传

**涉及文件**：
- `gateway/src/main/java/com/mercemay/shortlink/gateway/filter/TokenValidateGatewayFilterFactory.java`
- `gateway/src/main/java/com/mercemay/shortlink/gateway/config/Config.java`
- `gateway/src/main/resources/application-dev.yml`
- `admin/src/main/java/com/mercemay/shortlink/admin/common/biz/user/UserTransmitFilter.java`
- `admin/src/main/java/com/mercemay/shortlink/admin/common/biz/user/UserContext.java`

**问题背景**：
微服务架构下，鉴权不应该分散在每个服务中实现，而应该在网关层统一处理。同时，下游服务需要获取当前登录用户信息。

**实现细节**：
- **网关层**（`TokenValidateGatewayFilterFactory`）：继承 Spring Cloud Gateway 的 `AbstractGatewayFilterFactory`，实现自定义过滤器。从请求头中提取 `username` 和 `token`，通过 Redis Hash 查询用户登录信息（key: `shortlink:login:` + username）。
- 验证通过后，将用户 ID 和真实姓名注入到请求头中（`userId`、`realName`），通过 `exchange.getRequest().mutate()` 修改请求头，传递给下游服务。
- 支持配置化**白名单路径**（如登录、注册接口不需要鉴权），通过 `Config.whitePathList` 灵活配置。
- **下游服务**（`UserTransmitFilter`）：从请求头中提取用户信息，存入 `ThreadLocal`（`UserContext`），业务代码通过 `UserContext.getUserName()` 获取当前用户，`finally` 块中清理 `ThreadLocal` 防止内存泄漏。

```java
// Gateway: 注入用户信息到请求头
ServerHttpRequest.Builder builder = exchange.getRequest().mutate().headers(httpHeaders -> {
    httpHeaders.set("userId", userInfoJsonObject.getString("id"));
    httpHeaders.set("realName", URLEncoder.encode(userInfoJsonObject.getString("realName"), StandardCharsets.UTF_8));
});
return chain.filter(exchange.mutate().request(builder.build()).build());
```

**技术价值**：
完整实现了"网关统一鉴权 → 请求头透传用户信息 → ThreadLocal 接收"的标准微服务鉴权链路。自定义 `GatewayFilterFactory` + 配置化白名单的方式也体现了对 Spring Cloud Gateway 框架的深入理解。

---

## 亮点 12：Sentinel 接口级限流 + 自定义降级处理

**涉及文件**：
- `project/src/main/java/com/mercemay/shortlink/project/config/SentinelRuleConfig.java`
- `project/src/main/java/com/mercemay/shortlink/project/controller/ShortLinkController.java`
- `project/src/main/java/com/mercemay/shortlink/project/handler/CustomBlockHandler.java`

**问题背景**：
短链接创建接口需要防止恶意调用或突发流量压垮服务，需要在接口级别做流量控制。

**实现细节**：
- `SentinelRuleConfig` 实现 `InitializingBean`，在 Bean 初始化时编程式注册 Sentinel 限流规则：对 `create_short_link` 资源设置 **QPS 限流，阈值为 1**。
- 在 Controller 层使用 `@SentinelResource` 注解标记被保护的接口，并指定自定义的降级处理器 `CustomBlockHandler.createShortLinkBlockHandlerMethod`。
- 触发限流时返回友好的错误提示（"当前访问网站人数过多，请稍后再试..."），而不是抛出异常。

```java
@PostMapping("/api/short-link/v1/create")
@SentinelResource(
    value = "create_short_link",
    blockHandler = "createShortLinkBlockHandlerMethod",
    blockHandlerClass = CustomBlockHandler.class
)
public Result<ShortLinkCreateRespDTO> createShortLinkGroup(@RequestBody ShortLinkCreateReqDTO requestParam) {
    return Results.success(shortLinkService.createShortLink(requestParam));
}
```

**技术价值**：
展示了 Sentinel 从规则配置到注解使用到自定义降级处理的完整链路。Sentinel（编程式规则 + 注解式保护 + 自定义 BlockHandler）与上面的 Lua 脚本限流形成了"全局限流 + 用户维度限流"的双层限流架构。

---

## 亮点 13：聚合部署与微服务部署的双模式架构

**涉及文件**：
- `aggregation/src/main/java/com/mercemay/shortlink/aggregation/AggregationApplication.java`
- `aggregation/src/main/resources/application.yml`
- `aggregation/pom.xml`
- `gateway/src/main/resources/application.yml`
- `gateway/src/main/resources/application-dev.yml`
- `gateway/src/main/resources/application-aggregation.yml`

**问题背景**：
微服务架构在生产环境中有很好的弹性和可伸缩性，但在开发/演示/小规模部署时，启动多个服务过于复杂。需要一种方式能在两种模式之间灵活切换。

**实现细节**：
- `aggregation` 模块通过 Maven 依赖将 `admin` 和 `project` 两个模块打包在一起，共享同一个 Spring Boot 进程。
- Gateway 通过 `spring.profiles.active` 切换路由策略：
  - `aggregation` profile：所有请求路由到聚合服务（单节点）
  - `dev` profile：按微服务路由（`lb://short-link-admin` / `lb://short-link-project`）
- 聚合模式还支持 **Demo 模式**，通过配置黑名单拦截写操作接口，方便在线演示。

**技术价值**：
这种"微服务 + 聚合单体"的双模式架构设计，在实际项目中非常实用。它展示了对部署灵活性和工程实践的深入思考。

---

## 可改进的地方

1. **密码明文存储**：`UserServiceImpl.login()` 中密码是明文比较（`eq(UserDO::getPassword, requestParam.getPassword())`），应该使用 BCrypt 等哈希算法。

2. **ShardingSphere 加密密钥硬编码**：AES 密钥直接写在 YAML 配置文件中（`aes-key-value: vjEC4vMqQzdN4ocIK4KJ6Jv1RcixZaCA`），应该从环境变量或密钥管理系统中读取。

3. **Sentinel 限流规则硬编码**：`SentinelRuleConfig` 中的限流规则是硬编码的，阈值固定为 1 QPS，过于严格且不灵活。生产环境应该接入 Sentinel Dashboard 动态配置。

4. **统计消费者缺少事务保证**：`ShortLinkStatsSaveConsumer.processMessage` 中写入 8 张统计表没有用事务包裹，如果中间某张表写入失败，会导致统计数据不一致。

5. **布隆过滤器无法删除元素**：布隆过滤器的固有缺陷是不支持删除。如果短链接被彻底删除，布隆过滤器中仍然存在该元素，可能导致该短链接永远无法被重新创建。可以考虑使用 Counting Bloom Filter 或定期重建布隆过滤器。

6. **缺少单元测试**：项目中没有发现任何测试代码，建议补充核心逻辑的单元测试（如 `HashUtil`、`MessageQueueIdempotentHandler` 等）和集成测试。

7. **数据库连接密码在配置文件中明文存储**：开发环境和生产环境的数据库密码都直接写在了 ShardingSphere 配置文件中，应使用 Jasypt 等加密方案或环境变量注入。
