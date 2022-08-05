package org.tfoweaver.api;

import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @title: DsReentrantLock
 * @Author Star_Chen
 * @Date: 2022/7/12 11:43
 * @Version 1.0
 */
public interface DsReentrantLock {

    boolean tryLock(long timeout, TimeUnit timeUnit, String lockId);

    void unlock(String lockId);
}
