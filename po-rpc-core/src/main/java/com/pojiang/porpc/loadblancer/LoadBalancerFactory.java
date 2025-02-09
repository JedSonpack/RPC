package com.pojiang.porpc.loadblancer;

import com.pojiang.porpc.spi.SpiLoader;

/**
 * 工厂模式，支持SPI加载
 */
public class LoadBalancerFactory {
    /**
     * 判断懒汉模式的时候是否已经加载
     */
    private static boolean isLoaded = false;


    /**
     * 默认负载均衡方式
     */
    private static final LoadBalancer DEFAULT_LoadBalancer = new RoundRobinLoadBalancer();

    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static LoadBalancer getInstance(String key) {
        if (!isLoaded) {
            synchronized (LoadBalancerFactory.class) {
                if (!isLoaded) {
                    SpiLoader.load(LoadBalancer.class); //用到的时候，才加载
                    isLoaded = true;
                }
            }
        }
        return SpiLoader.getInstance(LoadBalancer.class, key);
    }

}
