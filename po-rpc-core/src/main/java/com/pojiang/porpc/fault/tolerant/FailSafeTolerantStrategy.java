package com.pojiang.porpc.fault.tolerant;

import com.pojiang.porpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 静默处理，不管异常
 */
@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("发生了静默异常，记录日志，不做处理！", e);
        return new RpcResponse();
    }
}
