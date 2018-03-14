package com.phei.netty.protostack;

import com.phei.netty.protostack.auth.LoginAuthReqHandler;
import com.phei.netty.protostack.encoder.NettyMessageDecoder;
import com.phei.netty.protostack.encoder.NettyMessageEncoder;
import com.phei.netty.protostack.heartbeat.HeartBeatReqHandler;
import com.phei.netty.protostack.util.NettyConstants;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NettyProtoClient {

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private EventLoopGroup group = new NioEventLoopGroup();

    public void connect(String host, int port) {

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {

                        socketChannel.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4,4))
                                .addLast("messagedecoder", new NettyMessageEncoder())
                                .addLast("readTimeOutHandler", new ReadTimeoutHandler(50))
                                .addLast("loginAuthHandler", new LoginAuthReqHandler())
                                .addLast("heartBeatReqHandler", new HeartBeatReqHandler());
                    }
                });


        try {
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port), new InetSocketAddress(NettyConstants.LOCAL_IP, NettyConstants.LOCAL_PORT)).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executorService.execute(new Runnable() {
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                        connect(NettyConstants.REMOTE_IP, NettyConstants.REMOTE_PORT);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    public static void main(String[] args) {

        new NettyProtoClient().connect(NettyConstants.REMOTE_IP, NettyConstants.REMOTE_PORT);
    }
}
