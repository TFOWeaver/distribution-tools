package org.tfoweaver.redis;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tfoweaver.api.DsReentrantLock;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description: 分布式锁实现
 * @title: RedisDsReentrantLock
 * @Author Star_Chen
 * @Date: 2022/7/12 11:45
 * @Version 1.0
 */
@Component
@Scope(value = "prototype")
public class RedisDsReentrantLock implements DsReentrantLock {

    private final static Logger log = LoggerFactory.getLogger(RedisDsReentrantLock.class);


    public final ConcurrentMap<Thread, LockData> threadLockDataConcurrentMap = Maps.newConcurrentMap();

    @Autowired
    private RedissionLockInternal redissionLockInternal;

    private long lockId;

    public RedisDsReentrantLock(){

    }

    public RedisDsReentrantLock(long lockId) {
        this.lockId = lockId;
        redissionLockInternal = new RedissionLockInternal();
    }

    /**
     * @return boolean
     * @Author Star_Chen
     * @Description 尝试获取锁，可重入
     * @Date 14:33 2022/7/12
     * @Param [timeout, timeUnit]
     **/
    @Override
    public boolean tryLock(long timeout, TimeUnit timeUnit, final String lockId) {
        Thread currentThread = Thread.currentThread();
        LockData lockData = threadLockDataConcurrentMap.get(currentThread);
        if (Objects.nonNull(lockData)) {
            lockData.lockCount.incrementAndGet();
            return true;
        }

        String lockValue = redissionLockInternal.tryRedisLock(lockId, timeout, timeUnit);

        if (Objects.nonNull(lockValue)) {
            LockData newLockData = new LockData(currentThread, lockValue);
            log.info("current thread {} try to lock redis lock :{}....", currentThread.getName(), lockId);
            threadLockDataConcurrentMap.put(currentThread, newLockData);
            return true;
        }
        return false;
    }

    /**
     * @return void
     * @Author Star_Chen
     * @Description 释放锁
     * @Date 16:58 2022/7/12
     * @Param []
     **/
    @Override
    public void unlock(final String lockId) {
        Thread currentThread = Thread.currentThread();
        LockData lockData = threadLockDataConcurrentMap.get(currentThread);
        if (Objects.isNull(lockData)) {
            throw new IllegalMonitorStateException("You do not own this lock: " + lockId);
        }

        int newLockCount = lockData.lockCount.decrementAndGet();
        if (newLockCount > 0) {
            return;
        }
        if (newLockCount < 0) {
            throw new IllegalMonitorStateException("Lock count has gone negative for lock: " + lockId);
        }

        try {
            redissionLockInternal.unlockRedisLock(String.valueOf(lockId), lockData.lockValue);
        } finally {
            threadLockDataConcurrentMap.remove(currentThread);
            log.info("current thread {},  finally release lock :{}....",currentThread.getName(), lockId);
        }

    }

    private static class LockData {
        final Thread ownThread;
        final String lockValue;
        final AtomicInteger lockCount = new AtomicInteger(1);

        private LockData(Thread ownThread, String lockValue) {
            this.ownThread = ownThread;
            this.lockValue = lockValue;
        }
    }
}
