package com.dosomegood.rpc.transmission.socket.server;

import com.dosomegood.rpc.dto.RpcReq;
import com.dosomegood.rpc.dto.RpcResp;
import com.dosomegood.rpc.handler.RpcReqHandler;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
@AllArgsConstructor
public class SocketReqHandler implements Runnable {
    private final Socket socket;

    private final RpcReqHandler rpcReqHandler;

    @SneakyThrows
    @Override
    public void run() {
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
        RpcReq rpcReq = (RpcReq) inputStream.readObject();
        System.out.println(rpcReq);

        // 根据请求去通过provider实际调用，这部分封装成了 RpcReqHandler
        Object data = rpcReqHandler.invoke(rpcReq);


        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

        RpcResp<?> rpcResp = RpcResp.success(data,rpcReq.getReqId());

        outputStream.writeObject(rpcResp);

        outputStream.flush();
    }
}
