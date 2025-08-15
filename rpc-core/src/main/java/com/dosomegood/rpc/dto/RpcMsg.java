package com.dosomegood.rpc.dto;

import com.dosomegood.rpc.enums.CompressType;
import com.dosomegood.rpc.enums.MsgType;
import com.dosomegood.rpc.enums.SerializeType;
import com.dosomegood.rpc.enums.VersionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcMsg implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer reqId;
    private VersionType version;
    private MsgType msgType;
    private SerializeType serializeType;
    private CompressType compressType;
    private Object data;

}
