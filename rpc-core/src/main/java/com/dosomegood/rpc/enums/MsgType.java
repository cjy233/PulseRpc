package com.dosomegood.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

@Getter
@ToString
@AllArgsConstructor
public enum MsgType {
    HEARTBEAT_REQ((byte) 1, "心跳 请求"),
    HEARTBEAT_RESP((byte) 2, "心跳 请求"),
    RPC_REQ((byte) 3, "rpc 请求"),
    RPC_RESP((byte) 4, "rpc 响应");

    private final byte code;
    private final String desc;

    public boolean isHeartbeat() {
        return this == HEARTBEAT_RESP || this == HEARTBEAT_REQ;
    }

    public boolean isReq ( ) {
        return this == RPC_REQ || this == HEARTBEAT_REQ ;
    }

    public static MsgType from ( byte code ) {
        return Arrays.stream ( values ( ) )
                .filter ( msgType -> msgType.code == code )
                .findFirst ( )
                .orElseThrow ( ( ) -> new IllegalArgumentException ( " 找不到 对应 的 code 异常 " + code ) ) ;
    }
}