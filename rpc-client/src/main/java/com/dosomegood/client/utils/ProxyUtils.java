package com.dosomegood.client.utils;

import com.dosomegood.rpc.factory.SingletonFactory;
import com.dosomegood.rpc.proxy.RpcClientProxy;
import com.dosomegood.rpc.transmission.RpcClient;
import com.dosomegood.rpc.transmission.socket.client.SocketRpcClient;

public class ProxyUtils {

    private static final RpcClient rpcClient = new SocketRpcClient();

    private static final RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);

    public static <T> T getProxy(Class<T> clazz) {
        return rpcClientProxy.getProxy(clazz);
    }
}
