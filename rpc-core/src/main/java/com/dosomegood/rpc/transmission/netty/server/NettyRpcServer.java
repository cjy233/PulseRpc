package com.dosomegood.rpc.transmission.netty.server;

import com.dosomegood.rpc.config.RpcServiceConfig;
import com.dosomegood.rpc.constant.RpcConstant;
import com.dosomegood.rpc.factory.SingletonFactory;
import com.dosomegood.rpc.provider.Impl.ZkServiceProvider;
import com.dosomegood.rpc.provider.ServiceProvider;
import com.dosomegood.rpc.transmission.RpcServer;
import com.dosomegood.rpc.transmission.netty.codec.NettyRpcDecoder;
import com.dosomegood.rpc.transmission.netty.codec.NettyRpcEncoder;
import com.dosomegood.rpc.util.ShutdownHookUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcServer implements RpcServer {
    private final ServiceProvider serviceProvider ;

    private final int port ;

    public NettyRpcServer () {
        this ( SingletonFactory.getInstance ( ZkServiceProvider.class ) , RpcConstant.SERVER_PORT ) ;
    }

    public NettyRpcServer ( int port ) {
        this ( SingletonFactory.getInstance ( ZkServiceProvider.class ) , port ) ;
    }

    public NettyRpcServer ( ServiceProvider serviceProvider ) {
        this ( serviceProvider , RpcConstant.SERVER_PORT ) ;
    }

    public NettyRpcServer(ServiceProvider serviceProvider, int port) {
        this.serviceProvider = serviceProvider;
        this.port = port;
    }



    @SneakyThrows
    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new NettyRpcDecoder());
                        channel.pipeline().addLast(new NettyRpcEncoder());
                        channel.pipeline().addLast(new NettyRpcServerHandler(serviceProvider));
                    }
                });

        ShutdownHookUtils.clearAll();

        ChannelFuture channelFuture = bootstrap.bind(port).sync();

        log.info("nettyrpcserver已经启动端口");

        channelFuture.channel().closeFuture().sync();
    }

    @Override
    public void publishService(RpcServiceConfig config) {
        serviceProvider.publishService(config);
    }
}
