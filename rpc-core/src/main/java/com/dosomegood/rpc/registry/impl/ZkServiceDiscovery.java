package com.dosomegood.rpc.registry.impl;

import cn.hutool.core.util.StrUtil;
import com.dosomegood.rpc.constant.RpcConstant;
import com.dosomegood.rpc.dto.RpcReq;
import com.dosomegood.rpc.factory.SingletonFactory;
import com.dosomegood.rpc.loadbalance.LoadBalance;
import com.dosomegood.rpc.loadbalance.impl.RandomLoadBalance;
import com.dosomegood.rpc.registry.ServiceDiscovery;
import com.dosomegood.rpc.registry.zk.ZkClient;
import com.dosomegood.rpc.util.IPUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {

    private final ZkClient zkClient;

    private final LoadBalance loadBalance;

    public ZkServiceDiscovery() {
        this(new ZkClient());
    }

    public ZkServiceDiscovery(ZkClient zkClient) {
        this.zkClient = zkClient;
        this.loadBalance = SingletonFactory.getInstance(RandomLoadBalance.class);
    }


    @Override
    public InetSocketAddress lookup(RpcReq rpcReq) {
        String path = RpcConstant.ZK_RPC_ROOT_PATH
                + StrUtil.SLASH
                + rpcReq.getRpcServiceName();

        List<String> children = zkClient.getChildrenNode(path);
        String address = loadBalance.select(children);

        return IPUtils.toInetSocketAddress(address);
    }



}
