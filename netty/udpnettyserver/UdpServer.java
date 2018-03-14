package com.phei.netty.udpnettyserver;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ThreadLocalRandom;

public class UdpServer {

    public void run() {

        NioEventLoopGroup boss = new NioEventLoopGroup();

        Bootstrap serverBootstrap = new Bootstrap();
        serverBootstrap.group(boss).channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, Boolean.TRUE)
                .handler(new UdpServerHandler());

        try {
            serverBootstrap.bind(8888).sync().channel().closeFuture().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
        }
    }

    private class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

        private final String[] directory  = {"只要功夫深，铁棒磨成针",
                "旧时王谢堂前燕，飞入寻常百姓家；",
                "白发三千丈，缘愁似个长.",
                "洛阳亲友如相问，一片冰心在玉壶"};

        private String nextQuote() {
            int i = ThreadLocalRandom.current().nextInt(directory.length);
            return directory[i];
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
            cause.printStackTrace();
        }

        protected void messageReceived(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
            String request = datagramPacket.content().toString(CharsetUtil.UTF_8);
            System.out.println(request);

            if("谚语字典查询？".equals(request)) {
                channelHandlerContext.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("谚语查询结果：" + nextQuote(), CharsetUtil.UTF_8), datagramPacket.sender()));
            }
        }
    }

    public static void main(String[] args) {

        new UdpServer().run();
    }
}
