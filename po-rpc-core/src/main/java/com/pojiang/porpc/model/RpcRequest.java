package com.pojiang.porpc.model;

import com.pojiang.porpc.constant.RpcConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 封装RPC请求，为Java的反射机制提供必要参数
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {
    /**
     * 服务的名称
     */
    private String serviceName;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 参数类型列表
     */
    private Class<?>[] ParameterTypes;

    /**
     * 参数列表
     */
    private Object[] args;

    /**
     * 服务的版本号
     */
    private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;
}
