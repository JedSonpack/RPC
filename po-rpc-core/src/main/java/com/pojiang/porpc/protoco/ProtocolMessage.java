package com.pojiang.porpc.protoco;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 协议消息结构
 * 将消息头单独封装为⼀个内部类，消息体可以使⽤泛型类型，
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMessage<T> {
    /**
     * 消息头
     */
    private Header header;
    /**
     * 消息体（请求或响应对象）
     */
    private T body;

    /**
     * 协议消息头
     */
    @Data
    public static class Header {
        /**
         * 魔数:安全校验，防⽌服务器处理了⾮框架发来的乱七⼋糟的消息
         */
        private byte magic;
        /**
         * 版本号:保证请求和响应的⼀致性
         */
        private byte version;
        /**
         * 序列化器:来告诉服务端和客户端如何解析数据
         */
        private byte serializer;
        /**
         * 消息类型（请求 / 响应）：标识是请求还是响应？或者是⼼跳检测等其他⽤途。（请求头和响应头）
         */
        private byte type;
        /**
         * 状态：如果是响应，记录响应的结果： 200 OK！
         */
        private byte status;
        /**
         * 请求 id：唯⼀标识某个请求
         */
        private long requestId;
        /**
         * 消息体⻓度：保证能够完整地获取请求响应体的内容信息
         */
        private int bodyLength;
    }

}
