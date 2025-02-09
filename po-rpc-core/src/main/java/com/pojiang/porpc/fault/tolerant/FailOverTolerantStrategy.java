package com.pojiang.porpc.fault.tolerant;

import com.pojiang.porpc.loadblancer.LoadBalancer;
import com.pojiang.porpc.model.RpcRequest;
import com.pojiang.porpc.model.RpcResponse;
import com.pojiang.porpc.model.ServiceMetaInfo;
import com.pojiang.porpc.server.tcp.VertxTcpClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


/**
 * 故障转移策略
 */
public class FailOverTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // todo 故障转移，换其他节点服务？
        LoadBalancer loadBalancer = (LoadBalancer) context.get("loadBalancer");
        ServiceMetaInfo serviceMetaInfo = loadBalancer.select((Map<String, Object>) context.get("requestParams"), (List<ServiceMetaInfo>) context.get("serviceMetaInfoList"));
        try {
            return VertxTcpClient.doRequest((RpcRequest) context.get("rpcRequest"), serviceMetaInfo);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex);
        }

    }
}
