package com.pojiang.porpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.pojiang.porpc.RpcApplication;
import com.pojiang.porpc.model.RpcRequest;
import com.pojiang.porpc.model.RpcResponse;
import com.pojiang.porpc.model.ServiceMetaInfo;
import com.pojiang.porpc.protoco.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * TCP 请求客户端
 */
public class VertxTcpClient {

    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo selectedServiceMetaInfo) throws InterruptedException, ExecutionException {
        // 改为TCP请求
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();

        // 放在这里，是为了result里面可用
        // 将异步改为同步
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();

        netClient.connect(
                selectedServiceMetaInfo.getServicePort(),
                selectedServiceMetaInfo.getServiceHost(),
                result -> {
                    if (result.succeeded()) {
                        try {
                            System.out.println("TCP 连接已经完成建立！！！");
                            io.vertx.core.net.NetSocket socket = result.result();
                            // 构造消息
                            ProtocolMessage protocolMessage = new ProtocolMessage();
                            ProtocolMessage.Header header = new ProtocolMessage.Header();
                            header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                            header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                            header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                            header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                            header.setRequestId(IdUtil.getSnowflakeNextId());
                            protocolMessage.setHeader(header);
                            protocolMessage.setBody(rpcRequest);

                            // 发送消息
                            try {
                                Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                                socket.write(encodeBuffer);
                            } catch (IOException e) {
                                responseFuture.completeExceptionally(new RuntimeException("发送消息出现异常", e));
                            }

                            // 接收响应
                            socket.handler(buffer -> {
                                try {
                                    ProtocolMessage<RpcResponse> rpcResponseProtocolMessage =
                                            (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decoder(buffer);
                                    responseFuture.complete(rpcResponseProtocolMessage.getBody()); // 标记为完成，返回结果
                                } catch (IOException e) {
                                    responseFuture.completeExceptionally(new RuntimeException("协议消息解码错误", e));
                                }
                            });
                        } catch (Exception e) {
                            responseFuture.completeExceptionally(e); // 异常传递
                        }
                    } else {
                        responseFuture.completeExceptionally(result.cause()); // 连接失败的异常
                    }
                }
        );

        // 在主线程捕获结果或异常
        try {
            // 一定要关闭连接
            netClient.close();
            RpcResponse response = responseFuture.get(); // 阻塞等待结果（或使用 join()）
            System.out.println("接收到响应：" + response);
            return response;
        } catch (Exception e) {
            System.err.println("主线程捕获异常：" + e.getMessage());
        }

        return null;
    }
}
