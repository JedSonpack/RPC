package com.pojiang.examplespringbootconsumer;

import com.pojiang.example.common.model.User;
import com.pojiang.example.common.service.UserService;
import com.pojiang.porpc.springboot.starter.annotation.RpcReference;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {
    @RpcReference
    UserService userService;

    public void test() {
        User user = new User();
        user.setName("迫降！");
        User newUser = userService.getUser(user);
        if (newUser == null) {
            System.out.println("不能使用RPC框架");
        } else {
            System.out.println(newUser.getName());
        }
    }
}
