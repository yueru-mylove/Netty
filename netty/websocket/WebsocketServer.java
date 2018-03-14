package com.phei.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebsocketServer {

    public void run() {

        NioEventLoopGroup bos = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bos, worker).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast("http-codec", new HttpServerCodec())
                                .addLast("aggregator", new HttpObjectAggregator(65536))
                                .addLast("http-chunked", new ChunkedWriteHandler())
                                .addLast("handler", new WebSocketServerHandler());
                    }
                });


        try {
            Channel future = serverBootstrap.bind(8080).sync().channel();
            System.out.println("websocket server start at port:" + 8080 + ".");
            System.out.println("open you broswer and navigate to http://localhost:" + 8080 + "/");
            future.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bos.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }


    private static class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

        private static final Logger LOGGER = Logger.getLogger(WebSocketServerHandler.class.getName());
        private WebSocketServerHandshaker handshaker;

        protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
            if (o instanceof FullHttpRequest) {
                handleHttpRequest(channelHandlerContext, (FullHttpRequest) o);
            } else if (o instanceof WebSocketFrame) {
                handleWebSocketFrame(channelHandlerContext, (WebSocketFrame) o);
            }
        }


        private void handleHttpRequest(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) {

            if (!request.decoderResult().isSuccess() || !"websocket".equals(request.headers().get("Upgrade"))) {
                sendHttpResponse(channelHandlerContext, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
                return;
            }

            WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory("ws://localhost:8080/websocket", null, false);
            handshaker = factory.newHandshaker(request);

            if(handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(channelHandlerContext.channel());
            } else {
                handshaker.handshake(channelHandlerContext.channel(), request);
            }
        }



        private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {

            if(frame instanceof CloseWebSocketFrame) {
                handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
                return;
            }
            if(frame instanceof PingWebSocketFrame) {
                ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
                return;
            }

            if(!(frame instanceof TextWebSocketFrame)) {
                throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
            }

            String request = ((TextWebSocketFrame)frame).text();
            if(LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine(String.format("%s received %s", ctx.channel(), request));
            }
            ctx.channel().write(new TextWebSocketFrame(request + ", 欢迎使用netty websocket服务， 现在时刻，"
                            + new Date().toString()));
        }



        private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response) {

            // 返回应答给客户端
            if (response.status().code() != 200) {
                ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
                response.content().writeBytes(buf);
                buf.release();
                HttpHeaderUtil.setContentLength(response, response.content().readableBytes());
            }


            // 如果是非keep-alive, 关闭连接
            ChannelFuture future = ctx.channel().writeAndFlush(response);
            if (!HttpHeaderUtil.isKeepAlive(request) || response.status().code() != 200) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
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

        new WebsocketServer().run();
    }
}
