package com.dosomegood.rpc.provider.Impl;

import com.dosomegood.rpc.config.RpcServiceConfig;
import com.dosomegood.rpc.provider.ServiceProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class SimpleServiceProvider implements ServiceProvider {

    private final static Map<String,Object> SERVICE_CACHE = new HashMap<>();

    @Override
    public void publishService(RpcServiceConfig config) {
        List<String> rpcServiceNames = config.rpcServiceNames();

        if (rpcServiceNames.isEmpty()) {
            throw new RuntimeException("没有实现接口");
        }

        log.debug("发布服务:{}",rpcServiceNames);

        rpcServiceNames.forEach(rpcServiceName -> SERVICE_CACHE.put(rpcServiceName,config.getService()));
    }

    @Override
    public Object getService(String rpcServiceName) {
        if (SERVICE_CACHE.containsKey(rpcServiceName)) {
            log.info("获取到对应服务：{}",rpcServiceName);
            return SERVICE_CACHE.get(rpcServiceName);
        }
        throw new IllegalArgumentException("找不到对应服务："+rpcServiceName);
    }
}
