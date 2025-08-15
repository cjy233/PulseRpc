package com.dosomegood.server;

import com.dosomegood.api.UserService;
import com.dosomegood.rpc.config.RpcServiceConfig;
import com.dosomegood.rpc.proxy.RpcClientProxy;
import com.dosomegood.rpc.transmission.RpcServer;
import com.dosomegood.rpc.transmission.netty.server.NettyRpcServer;
import com.dosomegood.rpc.transmission.socket.server.SocketRpcServer;
import com.dosomegood.rpc.util.ShutdownHookUtils;
import com.dosomegood.server.nettytest.NettyServer;
import com.dosomegood.server.service.UserServiceImpl;

public class Main {
    public static void main(String[] args) {
//        RpcServer rpcServer = new SocketRpcServer(8888);
//        rpcServer.start();
//
//        ShutdownHookUtils.clearAll();
//
        RpcServiceConfig config = new RpcServiceConfig(new UserServiceImpl());

        RpcServer rpcServer = new NettyRpcServer();

        rpcServer.publishService(config);

        rpcServer.start();

//        NettyRpcServer rpcServer = new NettyRpcServer();
//
//        rpcServer.start();


    }
}

