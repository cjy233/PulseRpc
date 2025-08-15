package com.dosomegood.rpc.dto;


import com.dosomegood.rpc.enums.RpcRespStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RpcResp<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String reqId;

    private Integer code;

    private String msg;

    private T data;

    public static <T> RpcResp<T> success(T data,String reqId) {
        RpcResp<T> rpcResp = new RpcResp<T>();
        rpcResp.setCode(0);
        rpcResp.setReqId(reqId);
        rpcResp.setData(data);

        return rpcResp;
    }

    public static <T> RpcResp<T> fail(String reqId,RpcRespStatus status) {
        RpcResp<T> rpcResp = new RpcResp<T>();
        rpcResp.setCode(status.getCode());
        rpcResp.setReqId(reqId);
        rpcResp.setMsg(status.getMsg());
        return rpcResp;
    }

    public static <T> RpcResp<T> fail(String reqId,Integer code,String msg) {
        RpcResp<T> rpcResp = new RpcResp<T>();
        rpcResp.setCode(code);
        rpcResp.setReqId(reqId);
        rpcResp.setMsg(msg);
        return rpcResp;
    }

    public static <T> RpcResp<T> fail(String reqId,String msg) {
        RpcResp<T> rpcResp = new RpcResp<T>();
        rpcResp.setCode(RpcRespStatus.FAIL.getCode());
        rpcResp.setReqId(reqId);
        rpcResp.setMsg(msg);
        return rpcResp;
    }



}

