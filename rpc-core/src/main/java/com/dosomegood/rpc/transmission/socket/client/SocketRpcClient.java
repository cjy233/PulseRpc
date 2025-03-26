package com.dosomegood.rpc.transmission.socket.client;

import com.dosomegood.rpc.dto.RpcReq;
import com.dosomegood.rpc.dto.RpcResp;
import com.dosomegood.rpc.factory.SingletonFactory;
import com.dosomegood.rpc.registry.ServiceDiscovery;
import com.dosomegood.rpc.registry.impl.ZkServiceDiscovery;
import com.dosomegood.rpc.transmission.RpcClient;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
public class SocketRpcClient implements RpcClient {

    private static ServiceDiscovery serviceDiscovery;

    public SocketRpcClient() {
        this(SingletonFactory.getInstance(ZkServiceDiscovery.class));
    }

    public SocketRpcClient(ServiceDiscovery serviceDiscovery) {
        SocketRpcClient.serviceDiscovery = serviceDiscovery;
    }

    @Override
    public RpcResp<?> sendReq(RpcReq req) {

        InetSocketAddress address = serviceDiscovery.lookup(req);

        try (Socket socket = new Socket(address.getAddress(), address.getPort())) {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(req);
            outputStream.flush();

            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            Object o = inputStream.readObject();

            return (RpcResp<?>) o;
        } catch (Exception e) {
            log.error("发送rpc请求失败", e);
        }
        return null;
    }


}
