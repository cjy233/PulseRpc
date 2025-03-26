package com.dosomegood.rpc.transmission;

import com.dosomegood.rpc.dto.RpcReq;
import com.dosomegood.rpc.dto.RpcResp;

public interface RpcClient {
    RpcResp<?> sendReq(RpcReq req);
}
