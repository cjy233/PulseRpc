package com.dosomegood.rpc.transmission.netty.server;

import com.dosomegood.rpc.dto.RpcMsg;
import com.dosomegood.rpc.dto.RpcReq;
import com.dosomegood.rpc.dto.RpcResp;
import com.dosomegood.rpc.enums.CompressType;
import com.dosomegood.rpc.enums.MsgType;
import com.dosomegood.rpc.enums.SerializeType;
import com.dosomegood.rpc.enums.VersionType;
import com.dosomegood.rpc.factory.SingletonFactory;
import com.dosomegood.rpc.handler.RpcReqHandler;
import com.dosomegood.rpc.provider.ServiceProvider;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<RpcMsg> {

    private final RpcReqHandler rpcReqHandler ;

    public NettyRpcServerHandler (ServiceProvider serviceProvider) {
        this.rpcReqHandler = new RpcReqHandler(serviceProvider) ;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMsg rpcMsg) throws Exception {

        log.debug("接收到客户端请求: {}", rpcMsg);

        MsgType msgType ;
        Object data ;
        if ( rpcMsg.getMsgType () . isHeartbeat ( ) ) {
            msgType = MsgType.HEARTBEAT_RESP ;
            data = null;
        } else {
            msgType = MsgType.RPC_RESP ;
            RpcReq rpcReq = ( RpcReq ) rpcMsg.getData ( ) ;
            data = handleRpcReq(rpcReq);
        }

        RpcMsg msg = RpcMsg.builder ( )
                .reqId ( rpcMsg.getReqId ( ) )
                .version ( VersionType.VERSION1 )
                .msgType ( msgType)
                .compressType ( CompressType.GZIP )
                .serializeType ( SerializeType.KRYO )
                .data ( data )
                .build ( ) ;

        ctx.channel()
                .writeAndFlush(msg)
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务端异常");
        ctx.close();
    }

    private RpcResp < ? > handleRpcReq ( RpcReq rpcReq ) {
        try {
            Object object = rpcReqHandler.invoke ( rpcReq ) ;
            return RpcResp.success(object,rpcReq.getReqId()) ;
        } catch ( Exception e ) {
            log.info ( " 调用 失败 , " , e ) ;
            return RpcResp.fail(rpcReq.getReqId(),e.getMessage());
        }
    }

}
