package org.tfoweaver.api;

/**
 * @Description: 回调实现
 * @title: Callback
 * @Author Star_Chen
 * @Date: 2022/7/13 16:35
 * @Version 1.0
 */
public interface Callback {

    Object onGetLock() throws InterruptedException;

    Object onTimeout() throws InterruptedException;
}