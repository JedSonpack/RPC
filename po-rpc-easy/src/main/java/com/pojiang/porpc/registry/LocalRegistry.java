package com.pojiang.porpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地的注册中心 负责分发服务，其实就是管理一个Map（实现了类名 -> 实现类的映射）
 * <p>
 * 需要注意：本地服务注册器和注册中心的作用是有区别的。
 * 注册中心测中与管理注册的服务，提供服务信息给消费者
 * 本地服务注册器只需要根据服务名称，来获取对应的实现类，是调用过程中不可缺少的模块
 */
public class LocalRegistry {

    // 名称，实现类 对应哈希表
    // 注册信息的存储
    private static final Map<String, Class<?>> mp = new ConcurrentHashMap<>();

    /**
     * 注册服务
     *
     * @param serviceName
     * @param implClass
     */
    public static void register(String serviceName, Class<?> implClass) {
        mp.put(serviceName, implClass);
    }

    /**
     * 获取服务
     *
     * @param serviceName
     * @return
     */
    public static Class<?> get(String serviceName) {
        return mp.get(serviceName);
    }

    /**
     * 删除服务
     *
     * @param serviceName
     */
    public static void remove(String serviceName) {
        mp.remove(serviceName);
    }

}
