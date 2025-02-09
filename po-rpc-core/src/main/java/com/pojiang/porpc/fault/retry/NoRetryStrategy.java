package com.pojiang.porpc.fault.retry;

import com.pojiang.porpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * 不重试机制
 */
@Slf4j
public class NoRetryStrategy implements RetryStrategy {
    /**
     *  不重试的时候，直接运行得到返回结果，就是简单执行一次Callable任务
     * @param callable
     * @return
     * @throws Exception
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}
