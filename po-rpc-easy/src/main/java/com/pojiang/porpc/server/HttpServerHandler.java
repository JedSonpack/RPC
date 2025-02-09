package com.pojiang.porpc.server;

import com.pojiang.porpc.model.RpcRequest;
import com.pojiang.porpc.model.RpcResponse;
import com.pojiang.porpc.registry.LocalRegistry;
import com.pojiang.porpc.serializer.JdkSerializer;
import com.pojiang.porpc.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 请求处理器，作用就是根据客户端的请求参数来进行不同的处理，调用不同的服务和方法
 *  简化消费者，直接调用，这里面解耦合
 *   转发
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {

    @Override
    public void handle(HttpServerRequest request) {
        // 1. 反序列化请求为对象，并从请求对象中获取参数
        //制定序列化器
        final Serializer serializer = new JdkSerializer();
        //记录日志 GET
        System.out.println("Received Request" + request.method() + "   " + request.uri());
        //异步处理请求
        request.bodyHandler(buffer -> {
            byte[] bytes = buffer.getBytes(); //RPCRequest封装的字节数组
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 准备回复
            RpcResponse rpcResponse = new RpcResponse();
            if (rpcRequest == null) {
                rpcResponse.setMessage("rpcRequest is null!!!");
                doResponse(request, rpcResponse, serializer);
                return;
            }
            try {
                // 准备调用方法
                // 2. 根据服务名称从本地服务注册器中取得对应的服务实现类
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                // 3. 通过反射机制获取结果
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                // 4. 封装到响应中
                rpcResponse.setData(result);
                rpcResponse.setMessage("OK!!!");
                rpcResponse.setDataType(method.getReturnType());
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            doResponse(request, rpcResponse, serializer);
        });

    }

    /**
     * 响应回复，封装成响应
     *
     * @param httpServerRequest
     * @param response
     * @param serializer
     */
    public void doResponse(HttpServerRequest httpServerRequest, RpcResponse response, Serializer serializer) {
        HttpServerResponse httpServerResponse = httpServerRequest.response().putHeader("content-type", "application/json");
        try {
            // 序列化
            byte[] bytes = serializer.serialize(response);
            httpServerResponse.end(Buffer.buffer(bytes));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
