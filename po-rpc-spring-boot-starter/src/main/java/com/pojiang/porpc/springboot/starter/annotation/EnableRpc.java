package com.pojiang.porpc.springboot.starter.annotation;

import com.pojiang.porpc.springboot.starter.bootstrap.RpcConsumerBootstrap;
import com.pojiang.porpc.springboot.starter.bootstrap.RpcInitBootstrap;
import com.pojiang.porpc.springboot.starter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用RPC注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
// 注册自己的启动类
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {

    /**
     * 是否需要启用服务器：因为consumer和provider不一样
     *
     * @return
     */
    boolean needServer() default true;
}
