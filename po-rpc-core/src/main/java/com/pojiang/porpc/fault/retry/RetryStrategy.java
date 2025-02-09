package com.pojiang.porpc.fault.retry;

import com.pojiang.porpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 重试的策略
 */
public interface RetryStrategy {
    /**
     * 重试
     *使⽤ Callable 类代表⼀个任务
     * @param callable
     * @return
     * @throws Exception
     */
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
