package com.phei.netty.protostack.encoder;

import com.phei.netty.protostack.bean.Header;
import com.phei.netty.protostack.bean.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.HashMap;
import java.util.Map;

public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    private MarshallingDecoder marshallingDecoder;

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFiledLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFiledLength);
        this.marshallingDecoder = new MarshallingDecoder();
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {

        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setCrcCode(in.readInt());
        header.setLength(in.readInt());
        header.setSessionID(in.readLong());
        header.setType(in.readByte());
        header.setPriority(in.readByte());

        int size = in.readInt();
        if (size > 0) {
            Map<String, Object> attachment = new HashMap<String, Object>();
            int keySize = 0;
            byte[] keyBytes = null;
            String key = null;
            Object value = null;

            for (int i = 0; i < size; i++) {
                keySize = in.readInt();
                byte[] bytes = new byte[keySize];
                in.readBytes(bytes);
                key = new String(keyBytes, "utf-8");
                attachment.put(key, marshallingDecoder.decode(in));
            }

            keyBytes = null;
            key = null;
            header.setAttachment(attachment);
        }
        if (in.readableBytes() > 4) {
            message.setBody(marshallingDecoder.decode(in));
        }

        message.setHeader(header);
        return message;
    }
}
