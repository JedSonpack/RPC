package com.pojiang.porpc;


import com.pojiang.porpc.config.RegistryConfig;
import com.pojiang.porpc.config.RpcConfig;
import com.pojiang.porpc.constant.RpcConstant;
import com.pojiang.porpc.registry.Registry;
import com.pojiang.porpc.registry.RegistryFactory;
import com.pojiang.porpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 维护⼀个全局的配置对象。在引⼊ RPC 框架的项⽬启动时，从配置⽂件中读取配置并创建对象实例
 * 之后就可以集中地从这个对象中获取配置信息，⽽不⽤每次加载配置时再重新读取配置、并创建新的对象，减少
 * 了性能开销。
 * <p>
 * <p>
 * 使⽤设计模式中的 单例模式，就能够很轻松地实现这个需求了。
 * <p>
 * <p>
 * * RPC 框架应⽤
 * * 相当于 holder，存放了项⽬全局⽤到的变量。双检锁单例模式实现
 */
@Slf4j
public class RpcApplication {
    public static volatile RpcConfig rpcConfig;

    /**
     * 框架初始化，⽀持传⼊⾃定义配置
     * 输出加载的是什么配置
     *
     * @param newRpcConfig
     */
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", newRpcConfig.toString());
        // 注册中⼼初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, config = {}", registryConfig);
        // 创建并注册 Shutdown Hook，JVM 退出时执⾏操作
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    /**
     * 初始化
     * ⽀持⾃⼰传⼊配置对象；如果不传⼊，则默认调⽤前⾯写好的 ConfigUtils 来加载配置。
     */
    public static void init() {
        RpcConfig newRpcConfig;
        try {//这里自己写的
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class,
                    RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            // 配置加载失败，使⽤默认值，这里是在代码中写死的默认值
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 获取配置
     * 双检锁单例模式的经典实现，⽀持在获取配置时才调⽤  懒加载
     *
     * @return
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }

}
