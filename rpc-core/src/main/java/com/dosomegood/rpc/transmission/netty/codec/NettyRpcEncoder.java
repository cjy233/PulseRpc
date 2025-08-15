package com.dosomegood.rpc.transmission.netty.codec;

import com.dosomegood.rpc.compress.Compress;
import com.dosomegood.rpc.compress.Impl.GzipCompress;
import com.dosomegood.rpc.constant.RpcConstant;
import com.dosomegood.rpc.dto.RpcMsg;
import com.dosomegood.rpc.factory.SingletonFactory;
import com.dosomegood.rpc.serialize.Impl.KryoSerializer;
import com.dosomegood.rpc.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyRpcEncoder extends MessageToByteEncoder<RpcMsg> {

    private static final AtomicInteger ID_GEN = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMsg rpcMsg, ByteBuf byteBuf) throws Exception {
        byteBuf.writeBytes(RpcConstant.RPC_MAGIC_CODE);
        byteBuf.writeByte(rpcMsg.getVersion().getCode());

// 往右挪动4位，给报文长度腾出空间
        byteBuf.writerIndex(byteBuf.writerIndex() + 4);

        byteBuf.writeByte(rpcMsg.getMsgType().getCode());
        byteBuf.writeByte(rpcMsg.getSerializeType().getCode());
        byteBuf.writeByte(rpcMsg.getCompressType().getCode());
        byteBuf.writeInt(ID_GEN.incrementAndGet());

        int msgLen = RpcConstant.REQ_HEAD_LEN;
        if (!rpcMsg.getMsgType().isHeartbeat()
                && Objects.nonNull(rpcMsg.getData())) {
            byte[] data = data2Bytes(rpcMsg);
            byteBuf.writeBytes(data);
            msgLen += data.length;
        }

        int curIdx = byteBuf.writerIndex();
        byteBuf.writerIndex(curIdx - msgLen + RpcConstant.RPC_MAGIC_CODE.length + 1);
        byteBuf.writeInt(msgLen);
        byteBuf.writerIndex(curIdx);

    }


    private byte[] data2Bytes(RpcMsg rpcMsg) {
        // todo 获取序列化和数据压缩类型
        // rpcMsg.getSerializeType();
        // rpcMsg.getCompressType();

        Serializer serializer = SingletonFactory.getInstance(KryoSerializer.class);
        byte[] data = serializer.serialize(rpcMsg.getData());

        Compress compress = SingletonFactory.getInstance(GzipCompress.class);
        return compress.compress(data);
    }
}
