package com.pojiang.porpc.fault.tolerant;

import com.pojiang.porpc.model.RpcResponse;

import java.util.Map;

/**
 * 容错机制
 */
public interface TolerantStrategy {

    /**
     * 容错方法
     * @param context
     * @param e
     * @return
     */
    RpcResponse doTolerant(Map<String, Object> context, Exception e);
}
