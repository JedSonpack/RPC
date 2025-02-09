package com.pojiang.porpc.protoco;

import com.pojiang.porpc.serializer.Serializer;
import com.pojiang.porpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 编码器（和序列化器不一样）
 * 将ProtocolMessage 变为 buffer
 */
public class ProtocolMessageEncoder {


    /**
     * 编码:将Protocol编码为Buffer
     *
     * @param protocolMessage
     * @return
     * @throws IOException
     */
    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws IOException {
        if (protocolMessage == null || protocolMessage.getHeader() == null) {
            return Buffer.buffer();
        }

        ProtocolMessage.Header header = protocolMessage.getHeader();
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());

        // 获取序列化器
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("序列化消息的协议不存在");
        }

        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());

        // 只是将请求或者响应序列户
        byte[] bytes = serializer.serialize(protocolMessage.getBody());

        // 就是消息体的长度
        buffer.appendInt(bytes.length);

        // 封装消息体
        buffer.appendBytes(bytes);

        return buffer;
    }
}
