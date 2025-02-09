package com.pojiang.porpc.loadblancer;

import com.pojiang.porpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * 负载均衡 （消费端使用）
 */
public interface LoadBalancer {

    /**
     * 选择服务调⽤
     *
     * @param requestParams       请求参数
     * @param serviceMetaInfoList 可⽤服务列表
     * @return
     */
    ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
