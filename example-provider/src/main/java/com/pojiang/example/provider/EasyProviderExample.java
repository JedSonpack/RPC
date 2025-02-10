package com.pojiang.example.provider;


import com.pojiang.example.common.service.UserService;
import com.pojiang.porpc.RpcApplication;
import com.pojiang.porpc.registry.LocalRegistry;
import com.pojiang.porpc.server.HttpServer;
import com.pojiang.porpc.server.VertxHttpServer;

/**
 * 简易服务提供者
 */
public class EasyProviderExample {
    public static void main(String[] args) {

        // 配置初始化，初始化后，后边可以直接用
        RpcApplication.init();

        //先本地注册服务
        LocalRegistry.register(UserService.class.getName(),UserServiceImpl.class);
        // 创建服务器示例
        HttpServer server = new VertxHttpServer();
        // 启动
        server.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
