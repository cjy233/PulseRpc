package com.dosomegood.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

@ToString
@Getter
@AllArgsConstructor
public enum VersionType {
    VERSION1((byte) 1, "版本 1");

    private final byte code;
    private final String desc;

    public static VersionType from ( byte code ) {
        return Arrays.stream ( values ( ) )
                .filter ( msgType -> msgType.code == code )
                .findFirst ( )
                .orElseThrow ( ( ) -> new IllegalArgumentException ( " 找不到 对应 的 code 异常 " + code ) ) ;
    }
}
