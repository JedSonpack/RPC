package com.pojiang.porpc.fault.tolerant;

import com.pojiang.porpc.spi.SpiLoader;

/**
 * 容错机制工厂
 */
public class TolerantStrategyFactory {

    /**
     * 判断懒汉模式的时候是否已经加载
     */
    private static boolean isLoaded = false;


    /**
     * 默认负载均衡方式
     */
    private static final TolerantStrategy DEFAULT_TolerantStrategy = new FailFastTolerantStrategy();

    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static TolerantStrategy getInstance(String key) {
        if (!isLoaded) {
            synchronized (TolerantStrategyFactory.class) {
                if (!isLoaded) {
                    SpiLoader.load(TolerantStrategy.class); //用到的时候，才加载
                    isLoaded = true;
                }
            }
        }
        return SpiLoader.getInstance(TolerantStrategy.class, key);
    }

}
