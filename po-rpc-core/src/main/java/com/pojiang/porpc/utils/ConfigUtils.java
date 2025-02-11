package com.pojiang.porpc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import com.pojiang.porpc.constant.RpcConstant;

/**
 * 配置工具类 用来加载配置文件
 */
public class ConfigUtils {
    /**
     * 加载配置对象
     *
     * @param tClass
     * @param prefix
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix,"");
    }

    /**
     * 加载配置对象，⽀持区分环境
     *
     * @param tClass
     * @param prefix
     * @param environment
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix, String
            environment) {

        // 拼接加载的properties
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            configFileBuilder.append("-").append(environment);
        }
        configFileBuilder.append(RpcConstant.FILE_END_WITH);
        // hutool的方法
        Props props = new Props(configFileBuilder.toString());
        return props.toBean(tClass, prefix); //转换成一个tclass类型的对象，只包含prefix开头的属性
    }
}
