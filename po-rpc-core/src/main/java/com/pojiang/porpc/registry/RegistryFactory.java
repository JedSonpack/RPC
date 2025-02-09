package com.pojiang.porpc.registry;

import com.pojiang.porpc.spi.SpiLoader;

/**
 * 注册中心工厂
 */
public class RegistryFactory {


    /**
     * 判断懒汉模式的时候是否已经加载
     */
    private static boolean isLoaded = false;


    /**
     * 默认注册中⼼
     */
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static Registry getInstance(String key) {
        if(!isLoaded){
            synchronized (RegistryFactory.class){
                if (!isLoaded) {
                    SpiLoader.load(Registry.class); //用到的时候，才加载
                    isLoaded = true;
                }
            }
        }
        return SpiLoader.getInstance(Registry.class, key);
    }

}
