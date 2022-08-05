package org.tfoweaver.api;

/**
 * @Description: 模板接口类
 * @title: DsLockTemplate
 * @Author Star_Chen
 * @Date: 2022/7/13 8:33
 * @Version 1.0
 */
public interface DsLockTemplate {

    /**
     * @Author Star_Chen
     * @Description 执行方法
     * @Date 16:35 2022/7/13
     * @Param [lockId, timeout, callback]
     * @return java.lang.Object
     **/
    Object execute(String lockId, long timeout, Callback callback);
}
