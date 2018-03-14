package com.phei.netty.httpserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import static io.netty.handler.codec.http.HttpResponseStatus.*;

public class HttpFileServer {

    private static final String DEFAULT_URL = "/src/main/java/com/";

    public void run(final String url, final int port) {

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {

                        socketChannel.pipeline().addLast("http-decode", new HttpRequestDecoder())
                                .addLast("http-agg", new HttpObjectAggregator(65536))
                                .addLast("http-encoder", new HttpResponseEncoder())
                                // 支持异步发送大的码流
                                .addLast("http-chunked", new ChunkedWriteHandler())
                                .addLast("fileserverhandler", new HttpFileServerHandler(url));
                    }
                });


        try {
            ChannelFuture future = serverBootstrap.bind("10.5.5.1", port).sync();
            System.out.println("http文件目录服务器启动k:the server start at http://10.5.5.1:8080" + url);
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) {

        new HttpFileServer().run(DEFAULT_URL, 8080);
    }
}
