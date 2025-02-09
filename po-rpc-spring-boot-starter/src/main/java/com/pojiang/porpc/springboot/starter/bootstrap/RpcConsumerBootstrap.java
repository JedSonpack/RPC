package com.pojiang.porpc.springboot.starter.bootstrap;

import com.pojiang.porpc.proxy.ServiceProxyFactory;
import com.pojiang.porpc.springboot.starter.annotation.RpcReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * 消费者的启动类
 * 在 Bean 初始化后，通过反射获取到 Bean 的所有属性，如果属性包含@RpcReference 注解，那么就为该属性动态⽣成代理对象并赋值。
 */
public class RpcConsumerBootstrap implements BeanPostProcessor {
    /**
     * Bean 初始化后执⾏，注⼊服务
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     **/
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //得到类ExampleServiceImpl
        Class<?> beanClass = bean.getClass();
        // 遍历对象所有属性（因为注解是加载属性上的）
        // 这个对象所有的属性
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            // 有属性有 注解，将它升级为代理对象
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                // 有注解
                // userService接口
                Class<?> interfaceClass = rpcReference.interfaceClass();
                if (interfaceClass == void.class) {
                    interfaceClass = field.getType();
                }
                // 反射绕过访问限制
                field.setAccessible(true);
                Object proxyObject = ServiceProxyFactory.getProxy(interfaceClass);
                try {
                    field.set(bean, proxyObject);
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("为字段注⼊代理对象失败", e);
                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
