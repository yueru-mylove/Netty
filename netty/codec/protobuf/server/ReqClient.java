package com.phei.netty.codec.protobuf.server;

import com.phei.netty.codec.protobuf.SubscribeReqProto;
import com.phei.netty.codec.protobuf.SubscribeResProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class ReqClient {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8888;

    public void connect(String host, int port) {

        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(nioEventLoopGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {

                        socketChannel.pipeline().addLast(new ProtobufVarint32FrameDecoder())
                                .addLast(new ProtobufVarint32LengthFieldPrepender())
                                .addLast(new ProtobufDecoder(SubscribeResProto.SubscribeRes.getDefaultInstance()))
                                .addLast(new ProtobufEncoder())
                                .addLast(new ReqClientHandler());
                    }
                });
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            nioEventLoopGroup.shutdownGracefully();
        }
    }


    private class ReqClientHandler extends ChannelHandlerAdapter{

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {

            for (int i = 0; i < 100; i++) {
                ctx.write(req(i));
            }
            ctx.flush();
        }

        private SubscribeReqProto.SubscribeReq req(int i) {

            SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
            builder.setSubReqID(i);
            builder.setUserName("burning");
            builder.setProductName("netty权威指南");
            builder.setAddress("nanjing yuhuatai");

            return builder.build();
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

    public static void main(String[] args) {
        new ReqClient().connect(HOST, PORT);
    }
}
