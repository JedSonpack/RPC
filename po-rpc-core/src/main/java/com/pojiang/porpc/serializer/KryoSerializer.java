package com.pojiang.porpc.serializer;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Kryo 序列化器
 * Kryo 本身是线程不安全的，所以需要使⽤ ThreadLocal 保证每个线程有⼀个单独的 Kryo 对象实例
 */
public class KryoSerializer implements Serializer {
    /**
     * kryo 线程不安全，使⽤ ThreadLocal 保证每个线程只有⼀个 Kryo
     */
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL =
            ThreadLocal.withInitial(() -> {
                Kryo kryo = new Kryo();
                // 设置动态动态序列化和反序列化类，不提前注册所有类（可能有安全问题）
                kryo.setRegistrationRequired(false);
                return kryo;
            });


    /**
     * 序列化对象为字节数组
     *
     * @param obj 要序列化的对象
     * @return 序列化后的字节数组
     */
    @Override
    public <T> byte[] serialize(T obj) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        KRYO_THREAD_LOCAL.get().writeObject(output, obj);
        output.close();
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 反序列化字节数组为对象
     *
     * @param bytes 序列化后的字节数组
     * @param classType 目标对象的类类型
     * @return 反序列化后的对象
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> classType) {
        ByteArrayInputStream byteArrayInputStream = new
                ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        T result = KRYO_THREAD_LOCAL.get().readObject(input, classType);
        input.close();
        return result;
    }

}
