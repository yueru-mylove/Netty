package com.phei.netty.protostack.heartbeat;

import com.phei.netty.protostack.bean.Header;
import com.phei.netty.protostack.bean.NettyMessage;
import com.phei.netty.protostack.util.MessageType;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class HeartBeatResHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        NettyMessage message = (NettyMessage) msg;
        if(message.getHeader() != null && message.getHeader().getType() == MessageType.HEART_BEAT_REQ.byteValue()) {

            System.out.println("received client heart beat message: ----> " + message);
            NettyMessage heartBeat = buildHeartBeat();
            System.out.println("send heart beat response message to ciient: ---->" + heartBeat);
            ctx.writeAndFlush(heartBeat);
        } else {
            ctx.fireChannelRead(msg);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }

    private NettyMessage buildHeartBeat () {

        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.HEART_BEAT_RES.byteValue());
        message.setHeader(header);
        return message;
    }


}
