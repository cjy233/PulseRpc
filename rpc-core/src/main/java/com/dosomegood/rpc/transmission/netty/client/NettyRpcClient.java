package com.dosomegood.rpc.transmission.netty.client;

import com.dosomegood.rpc.constant.RpcConstant;
import com.dosomegood.rpc.dto.RpcMsg;
import com.dosomegood.rpc.dto.RpcReq;
import com.dosomegood.rpc.dto.RpcResp;
import com.dosomegood.rpc.enums.CompressType;
import com.dosomegood.rpc.enums.MsgType;
import com.dosomegood.rpc.enums.SerializeType;
import com.dosomegood.rpc.enums.VersionType;
import com.dosomegood.rpc.factory.SingletonFactory;
import com.dosomegood.rpc.registry.ServiceDiscovery;
import com.dosomegood.rpc.registry.impl.ZkServiceDiscovery;
import com.dosomegood.rpc.transmission.RpcClient;
import com.dosomegood.rpc.transmission.netty.codec.NettyRpcDecoder;
import com.dosomegood.rpc.transmission.netty.codec.NettyRpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class NettyRpcClient implements RpcClient {

    private final ServiceDiscovery serviceDiscovery;
    private static final Bootstrap bootstrap;
    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;



    static {
        bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, DEFAULT_CONNECT_TIMEOUT)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new NettyRpcDecoder());
                        channel.pipeline().addLast(new NettyRpcEncoder());
                        channel.pipeline().addLast(new NettyRpcClientHandler());
                    }
                });
    }

    public NettyRpcClient() {
        this.serviceDiscovery = SingletonFactory.getInstance(ZkServiceDiscovery.class);
    }


    @SneakyThrows
    @Override
    public RpcResp<?> sendReq(RpcReq req) {
        InetSocketAddress address = serviceDiscovery.lookup(req);

        ChannelFuture channelFuture = bootstrap.connect(address).sync();

        log.info("nettyrpcclient连接到:" + address);

        Channel channel = channelFuture.channel();

        RpcMsg rpcMsg = RpcMsg.builder ( )
                .version ( VersionType.VERSION1 )
                .serializeType ( SerializeType.KRYO )
                .compressType ( CompressType.GZIP )
                .msgType ( MsgType.RPC_REQ )
                .data ( req )
                .build ( ) ;

        channel.writeAndFlush ( rpcMsg ) .addListener ( ChannelFutureListener.CLOSE_ON_FAILURE ) ;

// 阻塞等待, 直到关闭
        channel.closeFuture().sync();

// 获取服务端响应的数据
        AttributeKey<RpcResp<?>> key = AttributeKey.valueOf(RpcConstant.NETTY_RPC_KEY);

        return channel.attr(key).get();
    }
}
