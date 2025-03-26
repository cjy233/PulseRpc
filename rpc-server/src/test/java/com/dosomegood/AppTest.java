package com.dosomegood;

import lombok.SneakyThrows;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * Unit test for simple App.
 */
public class AppTest {
    @SneakyThrows
    public static void main(String[] args) {
        // 重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);

        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                // 要连接的服务器列表
                .connectString("117.72.123.135:2181")
                .retryPolicy(retryPolicy)
                .build();

        zkClient.start();

        // 需要创建zkClient
        String path = "/node1";

// 创建 NodeCache 实例
        NodeCache nodeCache = new NodeCache(zkClient, path);

// 注册监听器
        NodeCacheListener listener = () -> {
            if (nodeCache.getCurrentData() != null) {
                String data = new String(nodeCache.getCurrentData().getData());
                System.out.println("节点数据变化: " + data);
            } else {
                System.out.println("节点被删除");
            }
        };

        nodeCache.getListenable().addListener(listener);

// 启动 NodeCache
        nodeCache.start();

// 模拟程序运行一段时间
        System.in.read();

//        zkClient.create()
//                .creatingParentsIfNeeded()
//                .withMode(CreateMode.PERSISTENT)
//                .forPath("/node1/00001");
    }
}
