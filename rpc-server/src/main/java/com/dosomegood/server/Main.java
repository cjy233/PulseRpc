package com.dosomegood.server;

import com.dosomegood.api.UserService;
import com.dosomegood.rpc.config.RpcServiceConfig;
import com.dosomegood.rpc.proxy.RpcClientProxy;
import com.dosomegood.rpc.transmission.RpcServer;
import com.dosomegood.rpc.transmission.socket.server.SocketRpcServer;
import com.dosomegood.server.service.UserServiceImpl;

public class Main {
    public static void main(String[] args) {
//        RpcServer rpcServer = new SocketRpcServer(8888);
//        rpcServer.start();

        RpcServiceConfig config = new RpcServiceConfig(new UserServiceImpl());

        RpcServer rpcServer = new SocketRpcServer();

        rpcServer.publishService(config);

        rpcServer.start();



    }
}

