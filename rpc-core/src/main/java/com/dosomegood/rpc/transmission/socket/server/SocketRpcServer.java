package com.dosomegood.rpc.transmission.socket.server;

import com.dosomegood.rpc.config.RpcServiceConfig;
import com.dosomegood.rpc.constant.RpcConstant;
import com.dosomegood.rpc.dto.RpcReq;
import com.dosomegood.rpc.dto.RpcResp;
import com.dosomegood.rpc.factory.SingletonFactory;
import com.dosomegood.rpc.handler.RpcReqHandler;
import com.dosomegood.rpc.provider.Impl.SimpleServiceProvider;
import com.dosomegood.rpc.provider.Impl.ZkServiceProvider;
import com.dosomegood.rpc.provider.ServiceProvider;
import com.dosomegood.rpc.transmission.RpcServer;
import com.dosomegood.rpc.util.ThreadPoolUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

@Slf4j
public class SocketRpcServer implements RpcServer {

    private final int port;

    private final RpcReqHandler rpcReqHandler;

    private final ServiceProvider serviceProvider;

    private final ExecutorService executor;


    public SocketRpcServer(){
        this(RpcConstant.SERVER_PORT);
    }

    public SocketRpcServer(int port) {
        this.port = port;
        this.executor = ThreadPoolUtils.createIoThreadPool("socket-rpc-server-");
        this.rpcReqHandler = new RpcReqHandler(SingletonFactory.getInstance(ZkServiceProvider.class));
        this.serviceProvider = SingletonFactory.getInstance(ZkServiceProvider.class);
    }

    public SocketRpcServer(int port, ServiceProvider provider) {
        this.port = port;
        this.rpcReqHandler = new RpcReqHandler(provider);
        this.serviceProvider = provider;
        this.executor = ThreadPoolUtils.createIoThreadPool("socket-rpc-server-");
    }

    @Override
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("Server started on port {}", port);
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                executor.submit(new SocketReqHandler(socket, rpcReqHandler));
            }
        } catch (Exception e) {
            log.error("服务端异常", e); // "Server-side exception" in Chinese
        }

    }

    @Override
    public void publishService(RpcServiceConfig config) {
        serviceProvider.publishService(config);
    }






}
