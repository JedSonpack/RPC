package com.pojiang.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.pojiang.example.common.model.User;
import com.pojiang.example.common.service.UserService;
import com.pojiang.porpc.RpcApplication;
import com.pojiang.porpc.model.RpcRequest;
import com.pojiang.porpc.model.RpcResponse;
import com.pojiang.porpc.serializer.JdkSerializer;
import com.pojiang.porpc.serializer.Serializer;
import com.pojiang.porpc.serializer.SerializerFactory;

import java.io.IOException;

/**
 * 静态代理，帮助消费者简化调用流程
 * 为每一个特定的接口或者对象，编写一个代理类
 */
public class UserServiceProxy implements UserService {

    public User getUser(User user) {
        // 指定序列化器
        Serializer serializer = new JdkSerializer();
        // 发请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .ParameterTypes(new Class[]{User.class}) // 参数类型列表
                .args(new Object[]{user}) // 参数列表
                .build();

        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bodyBytes)
                    .execute()) {
                result = httpResponse.bodyBytes();//得到响应后的结果，字节数据
            }
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return (User) rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

