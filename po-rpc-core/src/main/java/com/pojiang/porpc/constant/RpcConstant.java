package com.pojiang.porpc.constant;

/**
 * RPC 的相关常量
 */
public interface RpcConstant {

    /**
     * 前缀为rpc,  可以读取到rpc.username等属性
     */
    String DEFAULT_CONFIG_PREFIX = "rpc";


    /**
     * 默认的读取文件的后缀
     */

    String FILE_END_WITH = ".properties";
//    String FILE_END_WITH = ".yml";


    /**
     * 默认服务版本
     */
    String DEFAULT_SERVICE_VERSION = "1.0";

}
