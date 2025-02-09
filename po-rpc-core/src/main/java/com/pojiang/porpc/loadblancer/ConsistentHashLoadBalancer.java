package com.pojiang.porpc.loadblancer;

import com.pojiang.porpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性哈希负载均衡
 * 实现虚拟节点
 *
 * 每次调⽤负载均衡器时，都会重新构造 Hash 环，这是为了能够即时处理节点的变化。
 */
public class ConsistentHashLoadBalancer implements LoadBalancer {

    /**
     * 虚拟节点
     */
    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    /**
     * 虚拟节点的个数
     */
    private static final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }

        // 构建虚拟环,每一个都建立多个虚拟节点(100个)
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                int key = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualNodes.put(key, serviceMetaInfo);
            }
        }
        int hash = getHash(requestParams);

        // 选择最接近且⼤于等于调⽤请求 hash 值的虚拟节点
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if (entry == null) {
            // 如果没有⼤于等于调⽤请求 hash 值的虚拟节点，则返回环⾸部的节点
            entry = virtualNodes.firstEntry();
        }
        return entry.getValue();

    }

    public int getHash(Object o) {
        return o.hashCode();
    }
}
