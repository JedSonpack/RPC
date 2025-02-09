package com.pojiang.example.consumer;

import com.pojiang.example.common.model.User;
import com.pojiang.example.common.service.UserService;
import com.pojiang.porpc.proxy.ServiceProxyFactory;

import java.net.UnknownHostException;

public class EasyConsumerExample {

    public static void main(String[] args) throws UnknownHostException {
        // todo 需要获取 UserService 的实现类对象
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("pojiang");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}

