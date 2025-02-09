package com.pojiang.porpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * mock的动态代理
 * 只需要提供⼀个根据服务接⼝类型返回固定值的⽅法。
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {

    /**
     * 调用代理，根据返回值不同
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 根据⽅法的返回值类型，⽣成特定的默认值对象
        Class<?> methodReturnType = method.getReturnType();
        log.info("mock invoke {}", method.getName());
        return getDefaultObject(methodReturnType);
    }

    /**
     * ⽣成指定类型的默认值对象
     *
     * @param type
     * @return
     */
    private Object getDefaultObject(Class<?> type) {
        // 基本类型
        if (type.isPrimitive()) {
            if (type == boolean.class) {
                return false;
            } else if (type == short.class) {
                return (short) 0;
            } else if (type == int.class) {
                return 0;
            } else if (type == long.class) {
                return 0L;
            } else if (type == float.class) {
                return 0.0;
            } else if (type == double.class) {
                return 0.0;
            } else if (type == byte.class) {
                return (byte) 0;
            }
        }
        // 对象类型
        return null;
    }
}
