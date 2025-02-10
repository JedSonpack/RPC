package com.pojiang.porpc.server;

/**
 * 定义服务器接口，将来可能有多个服务器
 */
public interface HttpServer {

    /**
     * 启动服务器
     * @param port
     */
    void doStart(int port);
}
