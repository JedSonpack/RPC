package com.pojiang.porpc.fault.tolerant;

import com.pojiang.porpc.model.RpcResponse;

import java.util.Map;

/**
 * 快速失败（立即通知外部调用方）
 */
public class FailFastTolerantStrategy implements TolerantStrategy {
    /**
     * 直接抛异常
     * @param context
     * @param e
     * @return
     */
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("触发容错机制，使用快速失败，直接抛出错误：服务出错！！！", e);
    }
}
