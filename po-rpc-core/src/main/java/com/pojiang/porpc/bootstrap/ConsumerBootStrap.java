package com.pojiang.porpc.bootstrap;

import com.pojiang.porpc.RpcApplication;

/**
 * 服务消费者的启动类
 */
public class ConsumerBootStrap {
    public static void init(){

        // 只需要初始化框架就行（配置和注册中⼼）
        RpcApplication.init();
    }
}
