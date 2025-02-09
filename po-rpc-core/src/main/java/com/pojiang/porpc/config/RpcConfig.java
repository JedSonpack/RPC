package com.pojiang.porpc.config;

import com.pojiang.porpc.fault.retry.RetryStrategyKeys;
import com.pojiang.porpc.fault.tolerant.TolerantStrategyKeys;
import com.pojiang.porpc.loadblancer.LoadBalancerKeys;
import com.pojiang.porpc.serializer.SerializerKeys;
import lombok.Data;

/**
 * RPC 的配置
 */
@Data
public class RpcConfig {
    /**
     * 名称
     */
    private String name = "po-rpc";
    /**
     * 版本号
     */
    private String version = "1.0";
    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";

    /**
     * 服务器端⼝号
     */
    private Integer serverPort = 8080;


    /**
     * 是否开启mock辅助测试
     */
    private boolean mock = false;


    /**
     * 序列化器
     */
    private String serializer = SerializerKeys.JDK;


    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();


    /**
     * 负载均衡设置
     */
    private String LoadBalancer = LoadBalancerKeys.RANDOM;

    /**
     * 重试策略
     */
    private String retryStrategy = RetryStrategyKeys.FIXED_INTERVAL;

    /**
     *  容错机制
     */
    private String tolerantStrategy = TolerantStrategyKeys.FAIL_OVER;

}
