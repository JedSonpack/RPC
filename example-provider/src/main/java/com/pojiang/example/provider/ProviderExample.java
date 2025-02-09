package com.pojiang.example.provider;

import com.pojiang.example.common.service.UserService;
import com.pojiang.porpc.bootstrap.ProviderBootStrap;
import com.pojiang.porpc.model.ServiceRegisterInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 新增服务提供者示例类，需要初始化 RPC 框架并且将服务⼿动注册到注册中⼼上
 */
public class ProviderExample {
    public static void main(String[] args) {
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo<?> serviceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);
        // 服务提供者初始化
        ProviderBootStrap.init(serviceRegisterInfoList);
    }
}
