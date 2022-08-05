package org.tfoweaver.redis;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tfoweaver.api.Callback;
import org.tfoweaver.api.DsLockTemplate;
import org.tfoweaver.utils.SequenceIdGenerator;

import java.util.concurrent.TimeUnit;

/**
 * @Description: 模板类
 * @title: RedisDsLockTemplate
 * @Author Star_Chen
 * @Date: 2022/7/13 8:32
 * @Version 1.0
 */
@Component
public class RedisDsLockTemplate implements DsLockTemplate {

    private static final Logger log = LoggerFactory.getLogger(RedisDsLockTemplate.class);

    @Autowired
    private RedisDsReentrantLock redisDsReentrantLock;

    @Override
    public Object execute(String lockId, long timeout, Callback callback) {

        if (StringUtils.isBlank(lockId)) {
            SequenceIdGenerator sequenceIdGenerator = new SequenceIdGenerator(1, 1);
            lockId = sequenceIdGenerator.nextId() + "";
        }

        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        boolean getLock = false;
        try {
            if (redisDsReentrantLock.tryLock(new Long(timeout), timeUnit, lockId)) {
                getLock = true;
                return callback.onGetLock();
            } else {
                return callback.onTimeout();
            }
        } catch (InterruptedException ex) {
            log.error(ex.getMessage(), ex);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (getLock) {
                redisDsReentrantLock.unlock(lockId);
            }
        }
        return null;
    }
}
