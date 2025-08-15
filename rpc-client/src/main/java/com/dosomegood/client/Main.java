package com.dosomegood.client;

import com.dosomegood.api.User;
import com.dosomegood.api.UserService;
import com.dosomegood.client.utils.ProxyUtils;
import com.dosomegood.rpc.dto.RpcReq;
import com.dosomegood.rpc.dto.RpcResp;
import com.dosomegood.rpc.proxy.RpcClientProxy;
import com.dosomegood.rpc.transmission.RpcClient;
import com.dosomegood.rpc.transmission.netty.client.NettyRpcClient;
import com.dosomegood.rpc.transmission.socket.client.SocketRpcClient;
import com.dosomegood.rpc.util.ThreadPoolUtils;

import java.util.concurrent.ExecutorService;

public class Main {
    public static void main(String[] args) {
        UserService userService = ProxyUtils.getProxy(UserService.class);

        User user = userService.getUser(1L);

        System.out.println(user);

//        RpcClient rpcClient = new NettyRpcClient();
//
//        RpcResp<?> rpcResp = rpcClient.sendReq(RpcReq.builder().interfaceName("请求数据").build());

    }

}
