package com.phei.netty.protostack.heartbeat;

import com.phei.netty.protostack.bean.Header;
import com.phei.netty.protostack.bean.NettyMessage;
import com.phei.netty.protostack.util.MessageType;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class HeartBeatReqHandler extends ChannelHandlerAdapter {

    private volatile ScheduledFuture<?> heartBeat;

    public HeartBeatReqHandler() {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        if(message != null && message.getHeader().getType() == MessageType.LOGIN_RES.byteValue()) {

            ctx.executor().scheduleAtFixedRate(new HeartBeatReqHandler.HeartBeatTask(ctx), 0, 5000, TimeUnit.SECONDS);
        } else if(message.getHeader() != null && message.getHeader().getType() == MessageType.HEART_BEAT_RES.byteValue()) {
            System.out.println("Client receive  server heart beat message: ---->" + message);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private class HeartBeatTask implements Runnable{

        private final ChannelHandlerContext context;

        public HeartBeatTask(final ChannelHandlerContext context) {
            this.context = context;
        }

        public void run() {

            NettyMessage heartBeat = buildHeartBeat();
            System.out.println("client send heart beat message to server : --->" + heartBeat);
            context.writeAndFlush(heartBeat);
        }


        private NettyMessage buildHeartBeat() {

            NettyMessage message = new NettyMessage();
            Header header = new Header();
            header.setType(MessageType.HEART_BEAT_REQ.byteValue());
            message.setHeader(header);
            return message;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(heartBeat != null) {
            heartBeat.cancel(true);
            heartBeat = null;
        }

        ctx.fireExceptionCaught(cause);
    }
}
