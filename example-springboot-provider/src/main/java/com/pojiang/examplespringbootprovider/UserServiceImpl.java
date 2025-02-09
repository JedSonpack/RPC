package com.pojiang.examplespringbootprovider;

import com.pojiang.example.common.model.User;
import com.pojiang.example.common.service.UserService;
import com.pojiang.porpc.springboot.starter.annotation.RpcService;
import org.springframework.stereotype.Service;

@RpcService
@Service
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}