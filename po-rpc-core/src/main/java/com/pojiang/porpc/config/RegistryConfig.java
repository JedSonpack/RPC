package com.pojiang.porpc.config;

import com.pojiang.porpc.registry.RegistryKeys;
import lombok.Data;

/**
 * RPC框架的注册中心配置
 */
@Data
public class RegistryConfig {
    /**
     * 注册中⼼类别
     */
    private String registry = RegistryKeys.ETCD;
    /**
     * 注册中⼼地址
     */
    private String address = "http://localhost:2380";
    /**
     * ⽤户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 超时时间（单位毫秒）
     */
    private Long timeout = 10000L;
}
