package com.pojiang.porpc.springboot.starter.bootstrap;

import com.pojiang.porpc.RpcApplication;
import com.pojiang.porpc.config.RpcConfig;
import com.pojiang.porpc.server.tcp.VertxTcpServer;
import com.pojiang.porpc.springboot.starter.annotation.EnableRpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Rpc 框架全局启动类
 */
@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {
    /**
     * Spring 初始化时执⾏，初始化 RPC 框架
     *
     * @param importingClassMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 先获取是否需要服务器
        boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName()).get("needServer");
        // 初始化RPC框架
        RpcApplication.init();
        // 全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        if (needServer) {
            // 服务器初始化
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(rpcConfig.getServerPort());
        } else {
            System.out.println("消费者：不需要启动服务器！！！");
//            log.info("消费者：不需要启动服务器！！！");
        }
    }
}
