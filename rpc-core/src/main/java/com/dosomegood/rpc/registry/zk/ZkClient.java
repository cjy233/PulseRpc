package com.dosomegood.rpc.registry.zk;

import cn.hutool.core.util.StrUtil;
import com.dosomegood.rpc.constant.RpcConstant;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;

@Slf4j
public class ZkClient {
    // 重试之间等待的初始时间
    private static final int BASE_SLEEP_TIME = 1000;
    // 最大重试次数
    private static final int MAX_RETRIES = 3;

    private CuratorFramework client;

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

        if (client.checkExists().forPath(path) != null) {
            log.info("该节点已存在: {}", path);
            return;
        }

        log.info("创建节点: {}", path);
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(path);
    }

    @SneakyThrows
    public List<String> getChildrenNode(String path) {

        if (StrUtil.isBlank(path)) {
            throw new IllegalArgumentException("path为空");
        }

        return client.getChildren().forPath(path);
    }



}
