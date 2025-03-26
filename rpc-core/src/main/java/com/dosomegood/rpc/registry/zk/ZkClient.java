package com.dosomegood.rpc.registry.zk;

import cn.hutool.core.util.StrUtil;
import com.dosomegood.rpc.constant.RpcConstant;
import com.dosomegood.rpc.util.IPUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ZkClient {
    // 重试之间等待的初始时间
    private static final int BASE_SLEEP_TIME = 1000;
    // 最大重试次数
    private static final int MAX_RETRIES = 3;

    private final CuratorFramework client;

    private static final Map<String, List<String>> SERVICE_ADDRESS_CACHE = new ConcurrentHashMap<>();

    private static final Set<String> SERVICE_ADDRESS_SET = ConcurrentHashMap.newKeySet();

    public ZkClient() {
        this(RpcConstant.ZK_IP, RpcConstant.ZK_PORT);
    }

    public ZkClient(String hostName, int port) {

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME,
                MAX_RETRIES);

        this.client = CuratorFrameworkFactory.builder()
                // 要连接的服务器列表
                .connectString(hostName+":"+port)
                .retryPolicy(retryPolicy)
                .build();

        log.info("zk开始链结。。。");

        this.client.start();
        log.info("zk连接成功");
    }

    //创建节点-上传服务使用
    @SneakyThrows
    public void createPersistentNode(String path){
        if(StrUtil.isBlank(path)){
            throw new IllegalArgumentException("path为空");
        }

        if (SERVICE_ADDRESS_SET.contains(path)) {
            log.info("该节点已存在: {}", path);
            return;
        }

        if (client.checkExists().forPath(path) != null) {
            SERVICE_ADDRESS_SET.add(path);
            log.info("该节点已存在: {}", path);
            return;
        }

        log.info("创建节点: {}", path);
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(path);
        SERVICE_ADDRESS_SET.add(path);
    }

    @SneakyThrows
    public List<String> getChildrenNode(String path) {

        if (StrUtil.isBlank(path)) {
            throw new IllegalArgumentException("path为空");
        }
        if (SERVICE_ADDRESS_CACHE.containsKey(path)) {
            return SERVICE_ADDRESS_CACHE.get(path);
        }

        List<String> children = client.getChildren().forPath(path);

        SERVICE_ADDRESS_CACHE.put(path, children);

        watchNode(path);

        return children;
    }

    @SneakyThrows
    private void watchNode(String path) {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, path, true);

        // 给某个节点注册子节点监听器
        PathChildrenCacheListener pathChildrenCacheListener = (curClient, event) -> {
            List<String> children = curClient.getChildren().forPath(path);
            SERVICE_ADDRESS_CACHE.put(path, children);
        };

        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        // 启动 pathChildrenCache
        pathChildrenCache.start();
    }

    public void clearAll(InetSocketAddress address) {
        if (Objects.isNull(address)) {
            throw new IllegalArgumentException("address 为 null");
        }

        SERVICE_ADDRESS_SET.forEach(path -> {
            if (path.endsWith(IPUtils.toIpPort(address))) {
                log.debug("zk 删除节点, {}", path);
                try {
                    // The delete operation seems incomplete in the image
                    client.delete().deletingChildrenIfNeeded().forPath(path);
                } catch (Exception e) {
                    log.error("删除节点失败{}", path, e);
                }
            }
        });
    }

}
