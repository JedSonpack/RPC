package com.pojiang.porpc.serializer;

import com.pojiang.porpc.spi.SpiLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * 序列化工厂
 * 序列化器对象是可以复⽤的，没必要每次执⾏序列化操作前都创建⼀个新的对象。所以我们可以使⽤设计模式中的
 * 工⼚模式 + 单例模式 来简化创建和获取序列化器对象的操作
 * <p>
 * 再使用SPI解决了HashMap硬编码的问题
 */
public class SerializerFactory {
    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();


    /**
     * 判断懒汉模式的时候是否已经加载
     */
    private static boolean isLoaded = false;

    /**
     * 懒加载获取实例
     *
     * @param key
     * @return
     */
    public static Serializer getInstance(String key) {
        if (!isLoaded) {
            synchronized (SerializerFactory.class) {
                if (!isLoaded) {
                    SpiLoader.load(Serializer.class); //用到的时候，才加载
                    isLoaded = true;
                }
            }
        }
        return SpiLoader.getInstance(Serializer.class, key);
    }


//    private static final Map<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<>() {{
//        put(SerializerKeys.JDK, new JdkSerializer());
//        put(SerializerKeys.JSON, new JsonSerializer());
//        put(SerializerKeys.KRYO, new KryoSerializer());
//        put(SerializerKeys.HESSIAN, new HessianSerializer());
//    }};
//
//    /**
//     * 默认的序列化器
//     */
//    private static final Serializer DEFAULT_SERIALIZER = KEY_SERIALIZER_MAP.get(SerializerKeys.JDK);
//
//
//    /**
//     * 获取实例
//     *
//     * @param key
//     * @return
//     */
//    public static Serializer getInstance(String key) {
//        return KEY_SERIALIZER_MAP.getOrDefault(key, DEFAULT_SERIALIZER);
//    }
}
