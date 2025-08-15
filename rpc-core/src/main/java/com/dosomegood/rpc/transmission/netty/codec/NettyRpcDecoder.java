package com.dosomegood.rpc.transmission.netty.codec;

import cn.hutool.core.util.ArrayUtil;
import com.dosomegood.rpc.compress.Compress;
import com.dosomegood.rpc.compress.Impl.GzipCompress;
import com.dosomegood.rpc.constant.RpcConstant;
import com.dosomegood.rpc.dto.RpcMsg;
import com.dosomegood.rpc.dto.RpcReq;
import com.dosomegood.rpc.dto.RpcResp;
import com.dosomegood.rpc.enums.CompressType;
import com.dosomegood.rpc.enums.MsgType;
import com.dosomegood.rpc.enums.SerializeType;
import com.dosomegood.rpc.enums.VersionType;
import com.dosomegood.rpc.exception.RpcException;
import com.dosomegood.rpc.factory.SingletonFactory;
import com.dosomegood.rpc.serialize.Impl.KryoSerializer;
import com.dosomegood.rpc.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.concurrent.atomic.AtomicInteger;

public class NettyRpcDecoder extends LengthFieldBasedFrameDecoder {


    public NettyRpcDecoder() {
        super(RpcConstant.REQ_MAX_LEN,5,4,-9,0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);

        return decodeFrame(frame);
    }

    private Object decodeFrame (ByteBuf byteBuf ) {
        readAndCheckMagicCode ( byteBuf ) ;

        byte versionCode = byteBuf.readByte ( ) ;
        VersionType version = VersionType.from ( versionCode ) ;

        int msgLen = byteBuf.readInt ( ) ;

        byte msgTypeCode = byteBuf.readByte ( ) ;

        MsgType msgType = MsgType.from ( msgTypeCode ) ;

        byte serializerTypeCode = byteBuf.readByte ( ) ;

        SerializeType serializeType = SerializeType.from ( serializerTypeCode ) ;

        byte compressTypeCode = byteBuf.readByte ( ) ;

        CompressType compressType = CompressType.from ( compressTypeCode ) ;

        int reqId = byteBuf.readInt ( ) ;

        Object data = readData ( byteBuf ,  msgLen - RpcConstant.REQ_HEAD_LEN , msgType ) ;
        return RpcMsg.builder ( )
                .reqId ( reqId )
                .msgType ( msgType )
                .version ( version )
                .compressType ( compressType )
                .serializeType ( serializeType )
                .data ( data )
                .build ( ) ;
    }

    private void readAndCheckMagicCode ( ByteBuf byteBuf ) {
        byte [ ] magicBytes = new byte [ RpcConstant.RPC_MAGIC_CODE.length ] ;
        byteBuf.readBytes ( magicBytes ) ;

        if (!ArrayUtil.equals(magicBytes,RpcConstant.RPC_MAGIC_CODE ) ) {
            throw new RpcException( " 魔法值异常 : " + new String ( magicBytes ) ) ;
        }
    }

    private Object readData ( ByteBuf byteBuf , int dataLen , MsgType msgType ) {
        if ( msgType.isReq ( ) ) {
            return readData ( byteBuf , dataLen , RpcReq.class ) ;
        }
        return readData ( byteBuf , dataLen , RpcResp.class ) ;
    }

    private < T > T readData ( ByteBuf byteBuf , int dataLen , Class < T > clazz ) {
        if ( dataLen <= 0 ) {
            return null ;
        }
        byte [ ] data = new byte [ dataLen ] ;
        byteBuf.readBytes ( data ) ;
        Compress compress = SingletonFactory.getInstance ( GzipCompress.class ) ;
        data = compress.decompress ( data ) ;
        Serializer serializer = SingletonFactory.getInstance ( KryoSerializer.class ) ;
        return serializer.deserialize ( data , clazz ) ;
    }


}
