package com.pojiang.porpc.fault.retry;

import com.github.rholder.retry.*;
import com.google.common.base.Predicates;
import com.pojiang.porpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 固定时间重试
 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy {
    /**
     * 使⽤ Guava-Retrying 提供的 RetryerBuilder 能够很⽅便地指定重试条件、重试等待策略、重试停⽌策略、重试⼯作等。
     *
     * @param callable
     * @return
     * @throws Exception
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfResult(Predicates.<RpcResponse>isNull())
                .retryIfExceptionOfType(Exception.class)
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("重试次数{}", attempt.getAttemptNumber());
                    }
                })
                .build();

        // 执行传入的 Callable 对象的 call() 方法
        return retryer.call(callable);
    }
}
