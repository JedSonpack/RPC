package com.pojiang.porpc.fault.retry;

import com.pojiang.porpc.spi.SpiLoader;

/**
 * 重试策略工厂
 */
public class RetryStrategyFactory {
    /**
     * 判断懒汉模式的时候是否已经加载
     */
    private static boolean isLoaded = false;


    /**
     * 默认重试方式
     */
    private static final RetryStrategy DEFAULT_RetryStrategy = new NoRetryStrategy();

    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static RetryStrategy getInstance(String key) {
        if (!isLoaded) {
            synchronized (RetryStrategyFactory.class) {
                if (!isLoaded) {
                    SpiLoader.load(RetryStrategy.class); //用到的时候，才加载
                    isLoaded = true;
                }
            }
        }
        return SpiLoader.getInstance(RetryStrategy.class, key);
    }
}
