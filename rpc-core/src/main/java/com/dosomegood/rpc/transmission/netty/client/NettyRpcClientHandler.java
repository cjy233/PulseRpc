package com.dosomegood.rpc.transmission.netty.client;

import com.dosomegood.rpc.constant.RpcConstant;
import com.dosomegood.rpc.dto.RpcMsg;
import com.dosomegood.rpc.dto.RpcReq;
import com.dosomegood.rpc.dto.RpcResp;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcClientHandler extends SimpleChannelInboundHandler<RpcMsg> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMsg rpcMsg) throws Exception {
        log.debug("收到服务端数据: {}", rpcMsg);

        RpcResp<?> rpcResp = (RpcResp<?>) rpcMsg.getData();

        AttributeKey<RpcResp<?>> key = AttributeKey.valueOf(RpcConstant.NETTY_RPC_KEY);

        ctx.channel().attr(key).set(rpcResp);

        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务端发生异常", cause);
        ctx.close();
    }




}
