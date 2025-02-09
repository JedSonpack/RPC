package com.pojiang.example.provider;

import com.pojiang.example.common.model.User;
import com.pojiang.example.common.service.UserService;

/**
 * 用户服务实现类
 */
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("服务提供者开始工作-------");
        System.out.println("用户名是：" + user.getName());
        return user;
    }
}
