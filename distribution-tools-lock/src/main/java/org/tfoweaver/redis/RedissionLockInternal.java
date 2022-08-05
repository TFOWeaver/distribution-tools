package org.tfoweaver.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @Description: redis分布式锁原生实现
 * @title: RedissionLockInternal
 * @Author Star_Chen
 * @Date: 2022/7/12 13:07
 * @Version 1.0
 */
@Component
@Scope(value = "prototype")
public class RedissionLockInternal {

    private static final Logger log = LoggerFactory.getLogger(RedissionLockInternal.class);

    private final static char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
            'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z'};

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    /**
     * 重试等待时间 ms
     */
    private int retryAwait = 300;

    private int lockTimeout = 2000;

    /**
     * @return java.lang.String
     * @Author Star_Chen
     * @Description 尝试获取redis的锁
     * @Date 15:27 2022/7/13
     * @Param [lockId, timeout, timeUnit]
     **/
    public String tryRedisLock(String lockId, long timeout, TimeUnit timeUnit) {

        long start = System.currentTimeMillis();

        Long out = timeUnit.toMillis(timeout);

        String lockValue = null;

        while (lockValue == null) {
            lockValue = createRedisKey(lockId);
            if (!Objects.nonNull(lockValue)) {
                break;
            }
            //如果 当前时间 去掉等待时间 大于超时时间300ms，就退出，否则进入队列等待休眠时间
            long now = System.currentTimeMillis();

            if (now - start - retryAwait > out) {
                log.info("current thread : {}", Thread.currentThread().getName());
                break;
            }
            //线程进入阻塞队列休眠后唤醒 timed_waiting
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(retryAwait));
        }
        return lockValue;
    }

    private String createRedisKey(String lockId) {
        try {
            String value = lockId + randomId(1);
            String luaScript = ""
                    + "\nlocal r = tonumber(redis.call('SETNX', KEYS[1], ARGV[1]));"
                    + "\nredis.call('PEXPIRE', KEYS[1], ARGV[2]);"
                    + "\nreturn r";
            List<String> keys = new ArrayList<>();
            keys.add(String.valueOf(lockId));


            List<String> args = new ArrayList<>();
            args.add(value);
            args.add(lockTimeout + "");

            Long result = (redisTemplate.execute((RedisCallback<Long>) redisConnection -> {
                Object nativeConnection = redisConnection.getNativeConnection();
                // 集群
                if (nativeConnection instanceof JedisCluster) {
                    return (Long) ((JedisCluster) nativeConnection).eval(luaScript, keys, args);
                }
                // 单点
                else if (nativeConnection instanceof Jedis) {
                    return (Long) ((Jedis) nativeConnection).eval(luaScript, keys, args);
                }
                return null;
            }));
            log.info("current thread :{}, execute luaScript: {}, keys: {}, args: {}, 加锁的结果是：{}", Thread.currentThread().getName(), luaScript, keys, args, result);

            if (new Long(1).equals(result)) {
                return value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param key :
     * @Date: 解锁原子操作
     * @Description:
     * @Return: * @return : void
     */
    public void unlockRedisLock(String key, String value) {
        try {
            String luaScript = ""
                    + "\nlocal v = redis.call('GET', KEYS[1]);"
                    + "\nlocal r= 0;"
                    + "\nif v == ARGV[1] then"
                    + "\nr =redis.call('DEL', KEYS[1]);"
                    + "\nend"
                    + "\nreturn r";
            List<String> keys = new ArrayList<String>();
            keys.add(key);
            List<String> args = new ArrayList<String>();
            args.add(value);
            Long ret = redisTemplate.execute((RedisCallback<Long>) redisConnection -> {
                Object nativeConnection = redisConnection.getNativeConnection();
                // 集群
                if (nativeConnection instanceof JedisCluster) {
                    return (Long) ((JedisCluster) nativeConnection).eval(luaScript, keys, args);
                }
                // 单点
                else if (nativeConnection instanceof Jedis) {
                    return (Long) ((Jedis) nativeConnection).eval(luaScript, keys, args);
                }
                return null;
            });
            log.info("current thread :{}, unlock redis lock : {}", Thread.currentThread().getName(), key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String randomId(int size) {
        char[] cs = new char[size];
        for (int i = 0; i < cs.length; i++) {
            cs[i] = digits[ThreadLocalRandom.current().nextInt(digits.length)];
        }
        return new String(cs);
    }
}
