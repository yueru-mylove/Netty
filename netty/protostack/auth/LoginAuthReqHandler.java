package com.phei.netty.protostack.auth;


import com.phei.netty.protostack.bean.Header;
import com.phei.netty.protostack.bean.NettyMessage;
import com.phei.netty.protostack.util.MessageType;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class LoginAuthReqHandler extends ChannelHandlerAdapter {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        ctx.writeAndFlush(buildLoginReq());
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        if(message != null && message.getHeader().getType() == MessageType.LOGIN_RES.byteValue()) {
            byte body = (byte)message.getBody();
            if(body != (byte) 0) {
                ctx.close();
            } else {
                System.out.println("login is ok" + message);
                ctx.fireChannelRead(msg);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }

    private NettyMessage buildLoginReq() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_REQ.byteValue());
        message.setHeader(header);
        return message;
    }
}
