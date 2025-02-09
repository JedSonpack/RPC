package com.pojiang.porpc.bootstrap;

import com.pojiang.porpc.RpcApplication;
import com.pojiang.porpc.config.RegistryConfig;
import com.pojiang.porpc.config.RpcConfig;
import com.pojiang.porpc.model.ServiceMetaInfo;
import com.pojiang.porpc.model.ServiceRegisterInfo;
import com.pojiang.porpc.registry.LocalRegistry;
import com.pojiang.porpc.registry.Registry;
import com.pojiang.porpc.registry.RegistryFactory;
import com.pojiang.porpc.server.tcp.VertxTcpServer;

import java.util.List;

/**
 * 服务提供者初始化
 */
public class ProviderBootStrap {
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        // RPC框架的初始化，注册中心
        RpcApplication.init();

        // 全剧配置
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();
            Class<?> implClass = serviceRegisterInfo.getImplClass();
            // 本地注册服务
            LocalRegistry.register(serviceName, implClass);

            // 注册服务到服务中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(rpcConfig.getVersion());
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());

            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + "注册失败", e);
            }
        }


        // 启动 web 服务
        // HttpServer httpServer = new VertxHttpServer();
        // httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
        // 启动 TCP服务
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
