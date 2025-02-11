package com.pojiang.porpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.pojiang.porpc.fault.retry.RetryStrategy;
import com.pojiang.porpc.fault.tolerant.TolerantStrategy;
import com.pojiang.porpc.loadblancer.LoadBalancer;
import com.pojiang.porpc.registry.Registry;
import com.pojiang.porpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SPI的加载器
 * 提供了读取配置并加载实现类的⽅法。
 * 自定义实现，支持键值对映射
 * <p>
 * <p>
 * 关键实现如下：
 * 完整代码如下：
 * 1. ⽤ Map 来存储已加载的配置信息 键名 => 实现类 。
 * 2. 扫描指定路径，读取每个配置⽂件，获取到 键名 => 实现类 信息并存储在 Map 中。
 * 3. 定义获取实例⽅法，根据⽤户传⼊的接⼝和键名，从 Map 中找到对应的实现类，然后通过反射获取到实现类对
 * 象。可以维护⼀个对象实例缓存，创建过⼀次的对象从缓存中读取即可。
 * <p>
 * <p>
 * 虽然提供了 loadAll ⽅法，扫描所有路径下的⽂件进⾏加载，但其实没必要使⽤。更推荐使⽤ load ⽅法，按需加载指定的类。
 * 上述代码中获取配置⽂件是使⽤了 ResourceUtil.getResources ，⽽不是通过⽂件路径获取。因为如果框架作为依
 * 赖被引⼊，是⽆法得到正确⽂件路径的。
 */
@Slf4j
public class SpiLoader {
    /**
     * 存储已加载的类：接⼝名 =>（key => 实现类）
     */
    private static Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();
    /**
     * 对象实例缓存（避免重复 new），类路径 => 对象实例，单例模式
     */
    private static Map<String, Object> instanceCache = new ConcurrentHashMap<>();


    /**
     * 系统 SPI ⽬录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    /**
     * ⽤户⾃定义 SPI ⽬录
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";

    /**
     * 扫描路径
     */
    private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};

    /**
     * 动态加载的类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(RetryStrategy.class, TolerantStrategy.class, LoadBalancer.class, Serializer.class, Registry.class);

    /**
     * 加载单个类型
     *
     * @param loadClass
     * @return
     */
    public static Map<String, Class<?>> load(Class<?> loadClass) { //类型的类
        log.info("加载类型为 {} 的 SPI", loadClass.getName());
        // 扫描路径 用户自定义的优先级高于系统优先级（覆盖）
        Map<String, Class<?>> keyClassMap = new HashMap<>();
        for (String scanDir : SCAN_DIRS) {
            System.out.println(scanDir + loadClass.getName());
            List<URL> resources = ResourceUtil.getResources(scanDir + loadClass.getName()); //定位到每一个文件
            //读取每个文件
            for (URL resource : resources) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] split = line.split("=");
                        if (split.length > 1) { // 有效的
                            String key = split[0];
                            String className = split[1];
                            keyClassMap.put(key, Class.forName(className));
                        }
                    }
                } catch (Exception e) {
                    log.error("spi resource load error", e);
                }
            }
        }
        // 放到loaderMap中存储
        loaderMap.put(loadClass.getName(), keyClassMap);
        return keyClassMap;
    }

    /**
     * 加载所有的类型
     */
    public static void loadAll() {
        log.info("加载所有的SPI");
        for (Class<?> tClass : LOAD_CLASS_LIST) {
            load(tClass);
        }
    }

    /**
     * 获取实例
     * eg: tClass = Serializer key = jdk
     * return jdkSerializer
     */
    public static <T> T getInstance(Class<?> tClass, String key) {
        String tClassName = tClass.getName();
        Map<String, Class<?>> keyClassMap = loaderMap.get(tClassName);
        if (keyClassMap == null) {
            throw new RuntimeException(String.format("SpiLoader 未加载 %s 类型",
                    tClassName));
        }
        if (!keyClassMap.containsKey(key)) {
            throw new RuntimeException(String.format("SpiLoader 的 %s 不存在 key=%s 的类型", tClassName, key));
        }
        // 获取要加载的实际类型（已经到了实现类）
        Class<?> aClass = keyClassMap.get(key);
        String implClassName = aClass.getName();
        if (!instanceCache.containsKey(implClassName)) {
            try {
                instanceCache.put(implClassName, aClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                String errorMsg = String.format("%s 类实例化失败", implClassName);
                throw new RuntimeException(errorMsg, e);
            }
        }
        return (T) instanceCache.get(implClassName);
    }
}
