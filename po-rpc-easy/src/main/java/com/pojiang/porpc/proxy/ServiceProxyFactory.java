package com.pojiang.porpc.proxy;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂
 */
public class ServiceProxyFactory {
    /**
     * 得到代理对象
     *
     * @param serviceClass
     * @param <T>
     * @return
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        /*
          1. 获取服务接口类的类加载器，用于加载代理类
          2. 指定代理对象需要实现的接口列表，这里只有一个接口，即 serviceClass
          3. 实现了InvocationHandler 接口的实例，用于处理代理对象上的方法调用
        */
        Object proxyInstance = Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, new ServiceProxy());
        return (T) proxyInstance;
    }
}