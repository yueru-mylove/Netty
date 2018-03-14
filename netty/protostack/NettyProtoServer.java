package com.phei.netty.protostack;

import com.phei.netty.protostack.auth.LoginAuthResHandler;
import com.phei.netty.protostack.encoder.NettyMessageDecoder;
import com.phei.netty.protostack.encoder.NettyMessageEncoder;
import com.phei.netty.protostack.heartbeat.HeartBeatResHandler;
import com.phei.netty.protostack.util.NettyConstants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import sun.applet.Main;

import java.util.concurrent.Future;

public class NettyProtoServer {

    public void bind() {

        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4,4))
                                .addLast(new NettyMessageEncoder())
                                .addLast("ReadTimeOutHandler", new ReadTimeoutHandler(50))
                                .addLast("LoginAuthResHandler", new LoginAuthResHandler())
                                .addLast("HeartBeatHandler", new HeartBeatResHandler());
                    }
                });

        try {
            ChannelFuture future = serverBootstrap.bind(NettyConstants.REMOTE_IP, NettyConstants.REMOTE_PORT).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new NettyProtoServer().bind();
    }
}
