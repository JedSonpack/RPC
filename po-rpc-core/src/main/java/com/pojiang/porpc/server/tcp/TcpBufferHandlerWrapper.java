package com.pojiang.porpc.server.tcp;

import com.pojiang.porpc.protoco.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

/**
 * 装饰者模式（使⽤ recordParser 对原有的 buffer 处理能⼒进⾏增强）
 */
public class TcpBufferHandlerWrapper implements Handler<Buffer> {
    private final RecordParser recordParser;

    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler) {
        this.recordParser = initRecordParser(bufferHandler);
    }


    public RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
        // 构造parser
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);
        // 异步的
        parser.setOutput(new Handler<Buffer>() {
            int size = -1;
            // ⼀次完整的读取（头 + 体）
            Buffer resultBuffer = Buffer.buffer();

            @Override
            public void handle(Buffer buffer) {
                // 这里的buffer是TCP传输的数据
                if (size == -1) {
                    size = buffer.getInt(13); // 消息体的长度
                    parser.fixedSizeMode(size);
                    // 写⼊头信息到结果
                    resultBuffer.appendBuffer(buffer);
                } else {
                    // 第二次读取，读消息体
                    resultBuffer.appendBuffer(buffer);
                    // 已拼接为完整 Buffer，执⾏处理
                    bufferHandler.handle(resultBuffer);
                    size = -1;
                    parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    resultBuffer = Buffer.buffer();
                }
            }
        });
        return parser;
    }
    @Override
    public void handle(Buffer buffer) {
        // 用于处理传入的 Buffer 数据
        recordParser.handle(buffer);
    }
}
