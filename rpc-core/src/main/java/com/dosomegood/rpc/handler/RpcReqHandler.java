package com.dosomegood.rpc.handler;

import com.dosomegood.rpc.dto.RpcReq;
import com.dosomegood.rpc.provider.ServiceProvider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.security.Provider;

@Slf4j
public class RpcReqHandler {
    private ServiceProvider provider;
    public RpcReqHandler(ServiceProvider provider) {
        this.provider = provider;
    }

    @SneakyThrows
    public Object invoke(RpcReq rpcReq)  {
        String rpcServiceName = rpcReq.getRpcServiceName();
        Object service = provider.getService(rpcServiceName);
        Method method = service.getClass().getMethod(rpcReq.getMethodName(), rpcReq.getParamTypes());
        Object object = method.invoke(service, rpcReq.getParams());
        return object;
    }

}
