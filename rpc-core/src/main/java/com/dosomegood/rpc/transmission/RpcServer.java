package com.dosomegood.rpc.transmission;

import com.dosomegood.rpc.config.RpcServiceConfig;

public interface RpcServer {
    void start();

    void publishService(RpcServiceConfig config);
}
