package com.pojiang.porpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.pojiang.porpc.config.RegistryConfig;
import com.pojiang.porpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 注册中心的实现类
 */
public class EtcdRegistry implements Registry {

    private Client client;
    private KV kvClient;


    /**
     * 根节点 所有的子节点都要加上这个根结点
     * 这里只是为了区分不同的项目
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";
    /**
     * 本机注册的节点 key 集合（⽤于维护续期）(心跳检测)
     */
    private static final Set<String> localRegisterNodeKeySet = new HashSet<>();
    /**
     * 注册中⼼服务缓存
     */
    private RegistryServiceCache registryServiceCache = new
            RegistryServiceCache();


    /**
     * 正在监听的 key 集合
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    @Override
    public void init(RegistryConfig registryConfig) {
        // 创建etcd客户端
        client = Client.builder().endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).build();
        // 创建KV客户端实例
        kvClient = client.getKVClient();
        // 启动心跳检测
        heartBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // 创建Lease客户端：获取一个 Lease 客户端，用于管理租约。
        Lease leaseClient = client.getLeaseClient();
        // 创建⼀个 30 秒的租约()，获取租约的唯一标识符 leaseId
        long leaseId = leaseClient.grant(600L).get().getID();
        //设置要存储的键值对
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        //生成key和value
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        // value 为服务注册信息的 JSON 序列化
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo),
                StandardCharsets.UTF_8);

        // 将键值对和租约关联起来，并设置过期时间
        // 设置租约 ID，表示这个键值对将与这个租约关联。
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();
        localRegisterNodeKeySet.add(registerKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(registerKey, StandardCharsets.UTF_8));
    }


    /**
     * 服务发现，根据服务名称作为前缀，从 Etcd 获取服务下的节点列表：
     *
     * @param serviceKey 服务键名
     * @return
     */
    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 找到serviceKey的服务
        // 前缀搜索，结尾⼀定要加 '/'
        List<ServiceMetaInfo> cacheServiceMetaInfo = registryServiceCache.readCache();
        if (cacheServiceMetaInfo != null) {
            return cacheServiceMetaInfo;
        }
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
        try {
            // 前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(
                            ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                            getOption)
                    .get()
                    .getKvs();
            // 解析服务信息
            List<ServiceMetaInfo> serviceMetaInfoList = keyValues.stream()
                    .map(keyValue -> {
                        String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                        String value =
                                keyValue.getValue().toString(StandardCharsets.UTF_8);
                        watch(key);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    })
                    .collect(Collectors.toList());
            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    /**
     * ⽤于项⽬关闭后释放资源
     */
    @Override
    public void destroy() {
        // 便利当前节点所有的KEY
        for (String key : localRegisterNodeKeySet) {
            System.out.println("当前节点下线");
            try {
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException(key + "节点下线失败");
            }
        }
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void heartBeat() {
        //30秒检测以下，没有宕🐔的，重新注册，相当于续约
        CronUtil.schedule("*/59 * * * * *", new Task() {
            @Override
            public void execute() {
                // 遍历本节点所有的 key
                for (String key : localRegisterNodeKeySet) {
                    try {
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key,
                                        StandardCharsets.UTF_8))
                                .get()
                                .getKvs();
                        // 该节点已过期（需要重启节点才能重新注册）
                        if (CollUtil.isEmpty(keyValues)) {
                            continue;
                        }
                        // 节点未过期，重新注册（相当于续签）
                        KeyValue keyValue = keyValues.get(0);
                        String value =
                                keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value,
                                ServiceMetaInfo.class);
                        register(serviceMetaInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(key + "续签失败", e);
                    }
                }
            }
        });
        // ⽀持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceNodeKey) {
        Watch watchClient = client.getWatchClient();
        boolean newWatch = watchingKeySet.add(serviceNodeKey);
        if (newWatch) { //从前未被监视过
            watchClient.watch(ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8), response -> {
                List<WatchEvent> events = response.getEvents();
                for (WatchEvent event : events) {
                    switch (event.getEventType()) {
                        case DELETE:
                            // 清理注册服务缓存
                            registryServiceCache.clearCache();
                            break;
                        case PUT:
                        default:
                            break;
                    }
                }
            });
        }
    }
}
