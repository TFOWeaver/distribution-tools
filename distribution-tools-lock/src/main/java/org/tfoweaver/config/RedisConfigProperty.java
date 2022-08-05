/**
 * fshows.com
 * Copyright (C) 2013-2019 All Rights Reserved.
 */
package org.tfoweaver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * redis 配置属性类
 *
 * @author liuyuan
 * @version RedisConfigProperty.java, v 0.1 2019-11-06 16:17
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class RedisConfigProperty {

    /**
     * Redis服务器地址
     */
    private String host;
    /**
     * Redis服务器连接密码（默认为空）
     */
    private String password;
    /**
     * Redis数据库索引（默认为0）
     */
    private int database;
    /**
     * Redis服务器连接端口
     */
    private int port;
    /**
     * 连接超时时间（毫秒）
     */
    private int timeout;
}