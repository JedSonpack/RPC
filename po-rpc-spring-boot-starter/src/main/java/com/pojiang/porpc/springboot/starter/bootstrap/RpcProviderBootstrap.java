package com.pojiang.porpc.springboot.starter.bootstrap;

import com.pojiang.porpc.RpcApplication;
import com.pojiang.porpc.config.RegistryConfig;
import com.pojiang.porpc.config.RpcConfig;
import com.pojiang.porpc.model.ServiceMetaInfo;
import com.pojiang.porpc.registry.LocalRegistry;
import com.pojiang.porpc.registry.Registry;
import com.pojiang.porpc.registry.RegistryFactory;
import com.pojiang.porpc.springboot.starter.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Rpc 服务提供者启动类
 * 获取到所有包含 @RpcService 注解的类，并且通过注解的属性和反射机制，获取到要注册的服务信息，并且完成服务注册。
 */
@Slf4j
public class RpcProviderBootstrap implements BeanPostProcessor {
    //实现 BeanPostProcessor 接⼝的 postProcessAfterInitialization ⽅法，就可以在某个服务提供者Bean初始化后，执⾏注册服务等操作了。

    /**
     * Bean 初始化后执⾏，注册服务
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 获取当前自己加载的bean类
        // userServiceImpl
        Class<?> beanClass = bean.getClass();
        // 获取类的注解
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        if (rpcService != null) {
            // 有这个注解，需要注册服务
            // 获取服务基本信息
            // 接口
            Class<?> interfaceClass = rpcService.interfaceClass();
            // 默认值处理
            if (interfaceClass == void.class) {
                Class<?>[] interfaces = beanClass.getInterfaces();
                // beanClass实现的所有接口,这里默认取第一个
                interfaceClass = beanClass.getInterfaces()[0];
            }
            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();
            // 本地注册服务
            // 注册实现类
            LocalRegistry.register(serviceName, beanClass);
            // 全局配置
            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            // 注册服务到注册中⼼
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            // 填充注册信息元
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(serviceVersion);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + "注册失败！", e);
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
