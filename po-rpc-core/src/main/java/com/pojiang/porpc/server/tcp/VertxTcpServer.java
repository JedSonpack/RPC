package com.pojiang.porpc.server.tcp;

import com.pojiang.porpc.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxTcpServer implements HttpServer {
    @Override
    public void doStart(int port) {
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();
        // 创建 TCP 服务器
        NetServer server = vertx.createNetServer();
        // 处理请求
        // 每当有新的客户端连接到服务器时，TcpServerHandler 的 handle(NetSocket netSocket) 方法会被自动调用
        // 三个handle的作用
        /**
         * // 第一段代码：设置连接处理器
         * server.connectHandler(new TcpServerHandler()); // 当有新连接时，调用 TcpServerHandler 的 handle 方法
         *
         * // 第二段代码：设置缓冲区处理器
         * netSocket.handler(bufferHandlerWrapper); // 当收到新数据时，调用 bufferHandlerWrapper 的 handle 方法
         *
         * // 第三段代码：解析数据流
         * recordParser.handle(buffer); // 调用 RecordParser 的 handle 方法，逐步解析数据
         */
        server.connectHandler(new TcpServerHandler());

        // 启动 TCP 服务器并监听指定端⼝
        server.listen(port, result -> {
            if (result.succeeded()) {
                log.info("TCP server started on port " + port);
            } else {
                log.info("Failed to start TCP server: " + result.cause());
            }
        });
    }
}