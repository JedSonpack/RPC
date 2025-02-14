package com.pojiang.porpc.server.tcp;

import com.pojiang.porpc.model.RpcRequest;
import com.pojiang.porpc.model.RpcResponse;
import com.pojiang.porpc.protoco.ProtocolMessage;
import com.pojiang.porpc.protoco.ProtocolMessageDecoder;
import com.pojiang.porpc.protoco.ProtocolMessageEncoder;
import com.pojiang.porpc.protoco.ProtocolMessageTypeEnum;
import com.pojiang.porpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 服务提供者
 */
public class TcpServerHandler implements Handler<NetSocket> {

    @Override
    public void handle(NetSocket netSocket) {
        // 有新的客户端连接到服务器时
        // 处理链接
        // 用装饰者模式，对里面的代码（就是构造函数的一个参数）进行增强
        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            // 已得到不存在沾包半包的数据
            ProtocolMessage<RpcRequest> protocolMessage;
            try {
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decoder(buffer);
            } catch (IOException e) {
                throw new RuntimeException("解析消息的时候出错！！！");
            }
            RpcRequest rpcRequest = protocolMessage.getBody();
            RpcResponse rpcResponse = new RpcResponse();
            try {
                // 获取要调⽤的服务实现类，通过反射调⽤
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("OK!!!");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
                throw new RuntimeException(e);
            }

            // 发送响应，编码
            ProtocolMessage.Header header = protocolMessage.getHeader(); //两个消息头是相同的
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());//只是type不一样
            ProtocolMessage<RpcResponse> responseProtocolMessage = new
                    ProtocolMessage<>(header, rpcResponse);
            try {
                Buffer encode = ProtocolMessageEncoder.encode(responseProtocolMessage);
                netSocket.write(encode);
            } catch (IOException e) {
                throw new RuntimeException("协议消息编码错误");
            }

        });

        //每当 NetSocket 收到新的数据（即从客户端发送过来的数据包）时，
        //bufferHandlerWrapper 的 handle(Buffer buffer) 方法会被自动调用。
        netSocket.handler(bufferHandlerWrapper);
    }
}
