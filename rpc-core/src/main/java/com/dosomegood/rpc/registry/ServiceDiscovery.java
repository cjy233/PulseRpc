package com.dosomegood.rpc.registry;

import com.dosomegood.rpc.dto.RpcReq;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {

    InetSocketAddress lookup(RpcReq rpcReq);
}
