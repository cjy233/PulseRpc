package com.dosomegood.rpc.provider.Impl;

import cn.hutool.core.util.StrUtil;
import com.dosomegood.rpc.config.RpcServiceConfig;
import com.dosomegood.rpc.constant.RpcConstant;
import com.dosomegood.rpc.factory.SingletonFactory;
import com.dosomegood.rpc.provider.ServiceProvider;
import com.dosomegood.rpc.registry.ServiceRegistry;
import com.dosomegood.rpc.registry.impl.ZkServiceRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ZkServiceProvider implements ServiceProvider {

    private final Map<String, Object> SERVICE_CACHE = new HashMap<>();

    private final ServiceRegistry serviceRegistry;

    public ZkServiceProvider() {
        this(SingletonFactory.getInstance(ZkServiceRegistry.class));
    }

    public ZkServiceProvider(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void publishService(RpcServiceConfig config) {
        config.rpcServiceNames().forEach(rpcServiceName -> publishService(rpcServiceName, config.getService()));
    }

    @Override
    public Object getService(String rpcServiceName) {
        if(StrUtil.isEmpty(rpcServiceName)){
            throw new IllegalArgumentException("rpcServiceName is null");
        }
        if(!SERVICE_CACHE.containsKey(rpcServiceName)){
            throw new IllegalArgumentException("rpcServiceName ："+rpcServiceName+" not exist");
        }

        return SERVICE_CACHE.get(rpcServiceName);
    }

    @SneakyThrows
    private void publishService(String rpcServiceName, Object service) {
        String host = InetAddress.getLocalHost().getHostAddress();
        int port = RpcConstant.SERVER_PORT;

        InetSocketAddress address = new InetSocketAddress(host, port);
        
        serviceRegistry.registerService(rpcServiceName, address);

        SERVICE_CACHE.put(rpcServiceName, service);

    }
}
