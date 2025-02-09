package com.pojiang.porpc.fault.tolerant;

import com.pojiang.porpc.model.RpcResponse;

import java.util.Map;

/**
 * 降级到其他服务 容错机制
 */
public class FailBackTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // 降级其他服务，感觉可以自己写一个一定可以完成的服务Mock
        Class<?> type = (Class<?>) context.get("returnType");
        RpcResponse rpcResponse = new RpcResponse();
        if (type.isPrimitive()) {
            if (type == boolean.class) {
                rpcResponse.setData(false);
            } else if (type == short.class) {
                rpcResponse.setData(0);
            } else if (type == int.class) {
                rpcResponse.setData(0);
            } else if (type == long.class) {
                rpcResponse.setData(0L);
            } else if (type == float.class) {
                rpcResponse.setData(0.0);
            } else if (type == double.class) {
                rpcResponse.setData(0.0);
            } else if (type == byte.class) {
                rpcResponse.setData(0.0);
            }

        }
        if (rpcResponse.getData() != null) {
            rpcResponse.setData(null);
        }
        rpcResponse.setException(e);
        rpcResponse.setMessage("出现故障，已启动快失败方案！！！");
        rpcResponse.setDataType(type);
        return rpcResponse;

    }
}
