package com.phei.netty.protostack.auth;

import com.phei.netty.protostack.bean.Header;
import com.phei.netty.protostack.bean.NettyMessage;
import com.phei.netty.protostack.util.MessageType;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginAuthResHandler extends ChannelHandlerAdapter {

    private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<String, Boolean>();

    private String[] whiteList = {"10.5.5.1", "127.0.0.1"};

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        NettyMessage message = (NettyMessage) msg;
        if(message != null && message.getHeader().getType() == MessageType.LOGIN_REQ.byteValue()) {
            String nodeIndex = ctx.channel().remoteAddress().toString();
            NettyMessage loginRes = null;
            if(nodeCheck.containsKey(nodeIndex)) {
                loginRes = buildResponse((byte) -1);
            } else {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                boolean isOK = false;
                for(String WIP : whiteList) {
                    if(ip.equals(WIP)) {
                        isOK = true;
                        break;
                    }
                }

                loginRes = isOK ? buildResponse((byte) 0) : buildResponse((byte) -1);
                if(isOK) {
                    nodeCheck.put(nodeIndex, true);
                }
            }
            System.out.println("The login response is:" +loginRes +"body{" + loginRes.getBody() + "}");
            ctx.writeAndFlush(loginRes);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        nodeCheck.remove(ctx.channel().remoteAddress().toString()); // 删除缓存
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }

    private NettyMessage buildResponse(byte b) {

        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_RES.byteValue());
        message.setHeader(header);
        message.setBody(b);
        return message;
    }
}

