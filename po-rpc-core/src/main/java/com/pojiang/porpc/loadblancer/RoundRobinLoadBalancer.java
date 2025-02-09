package com.pojiang.porpc.loadblancer;

import com.pojiang.porpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询方式的负载均衡
 * 使⽤ JUC 包的 AtomicInteger 实现原⼦计数器，防⽌并发冲突问题。
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    /**
     * 原子性的计数器，防止并发冲突
     */
    private final AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }
        if (serviceMetaInfoList.size() == 1) {
            return serviceMetaInfoList.get(0);
        }
        int n = serviceMetaInfoList.size();
        // 加一并取模
        int idx = atomicInteger.getAndIncrement() % n;
        return serviceMetaInfoList.get(idx);
    }
}
