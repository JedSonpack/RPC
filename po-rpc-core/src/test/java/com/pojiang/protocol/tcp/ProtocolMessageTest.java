package com.pojiang.protocol.tcp;

import cn.hutool.core.util.IdUtil;
import com.pojiang.porpc.constant.RpcConstant;
import com.pojiang.porpc.model.RpcRequest;
import com.pojiang.porpc.protoco.*;
import com.pojiang.porpc.serializer.Serializer;
import com.pojiang.porpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

@Slf4j
public class ProtocolMessageTest {
    @Test
    public void testCoder() throws IOException {
        // 构造消息
        ProtocolMessage protocolMessage = new ProtocolMessage();
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
        header.setSerializer((byte) ProtocolMessageSerializerEnum.JDK.getKey());
        header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
        header.setStatus((byte) ProtocolMessageStatusEnum.OK.getValue());
        header.setRequestId(IdUtil.getSnowflakeNextId());

        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName("myService");
        rpcRequest.setMethodName("myMethod");
        rpcRequest.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        rpcRequest.setParameterTypes(new Class[]{String.class});
        rpcRequest.setArgs(new Object[]{"aaa", "bbb"});
        protocolMessage.setHeader(header);
        protocolMessage.setBody(rpcRequest);

        Serializer jdk = SerializerFactory.getInstance("jdk");
        header.setBodyLength(jdk.serialize(rpcRequest).length);

        System.out.println(protocolMessage);
        Buffer encode = ProtocolMessageEncoder.encode(protocolMessage);
        ProtocolMessage<?> decoder = ProtocolMessageDecoder.decoder(encode);
        Assert.assertNotNull(decoder);
        System.out.println(decoder);
    }
}
