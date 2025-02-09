package com.pojiang.example.consumer;

import com.pojiang.example.common.model.User;
import com.pojiang.example.common.service.UserService;
import com.pojiang.porpc.bootstrap.ConsumerBootStrap;
import com.pojiang.porpc.proxy.ServiceProxyFactory;

/**
 * 消费者的启动类
 */
public class ConsumerExample {
    public static void main(String[] args) {
        // 启动类先初始化
        ConsumerBootStrap.init();

        // todo 需要获取 UserService 的实现类对象
        UserService userService =
                ServiceProxyFactory.getProxy(UserService.class);
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
