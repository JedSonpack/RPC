package com.pojiang.porpc.proxy;

import cn.hutool.core.collection.CollUtil;
import com.pojiang.porpc.RpcApplication;
import com.pojiang.porpc.config.RpcConfig;
import com.pojiang.porpc.constant.RpcConstant;
import com.pojiang.porpc.fault.retry.RetryStrategy;
import com.pojiang.porpc.fault.retry.RetryStrategyFactory;
import com.pojiang.porpc.fault.tolerant.TolerantStrategy;
import com.pojiang.porpc.fault.tolerant.TolerantStrategyFactory;
import com.pojiang.porpc.loadblancer.LoadBalancer;
import com.pojiang.porpc.loadblancer.LoadBalancerFactory;
import com.pojiang.porpc.model.RpcRequest;
import com.pojiang.porpc.model.RpcResponse;
import com.pojiang.porpc.model.ServiceMetaInfo;
import com.pojiang.porpc.registry.Registry;
import com.pojiang.porpc.registry.RegistryFactory;
import com.pojiang.porpc.server.tcp.VertxTcpClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 服务代理 JDK动态代理
 * 本质上还是封装请求，简化消费者调用
 */
public class ServiceProxy implements InvocationHandler {


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("toString")) {
            // 返回简单的字符串，避免复杂逻辑
            // 避免toString调用
            System.out.println("避免了toString调用");
            return "Proxy for " + proxy.getClass().getInterfaces()[0].getName();
        }

        // 指定序列化器,这里不需要了，因为encode的时候会调用
//        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 发请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .ParameterTypes(method.getParameterTypes())
                .args(args)
                .build();


        // 从注册中⼼获取服务提供者请求地址
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 类名
        String serviceName = method.getDeclaringClass().getName();

        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());

        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);


        List<ServiceMetaInfo> serviceMetaInfoList =
                registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            throw new RuntimeException("暂⽆服务地址");
        }
        // 暂时先取第⼀个
//        ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);
        // 获取服务！！！
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
        // 将调⽤⽅法名（请求路径）作为负载均衡参数
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", rpcRequest.getMethodName());

        ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
        RpcResponse rpcResponse = null;

        try {
            // 建立重试机制
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(RpcApplication.getRpcConfig().getRetryStrategy());
            rpcResponse = retryStrategy.doRetry(() -> {
                System.out.println("开始执行 RPC 请求...");
//                int i = 1 / 0;
                return VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo);
            });
        } catch (Exception e) {
            // 出现错误，触发容错机制
            HashMap<String, Object> hm = new HashMap<>();
            // FailBack使用
            hm.put("returnType", method.getReturnType());
            // FailOver使用
            hm.put("requestParams", requestParams);
            hm.put("serviceMetaInfoList", serviceMetaInfoList);
            hm.put("loadBalancer", loadBalancer);
            hm.put("rpcRequest",rpcRequest);
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(RpcApplication.getRpcConfig().getTolerantStrategy());
            rpcResponse = tolerantStrategy.doTolerant(hm, e);
        }

        return rpcResponse.getData();
    }
}