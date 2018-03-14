package com.phei.netty.protostack.encoder;


import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.ByteInput;
import org.jboss.marshalling.Unmarshaller;

import java.io.IOException;

public class MarshallingDecoder {

    private final Unmarshaller unmarshaller;

    public MarshallingDecoder() {
        this.unmarshaller = MarshallingCodeFactory.buildUnmarshaller();
    }

    protected Object decode(ByteBuf buf) {

        int objSize = buf.readInt();
        ByteBuf byteBuf = buf.slice(buf.readerIndex(), objSize);
        ByteInput input = new ChannelBufferByteInput(buf);

        try {
            unmarshaller.start(input);
            Object object = unmarshaller.readObject();
            unmarshaller.finish();
            buf.readerIndex(buf.readerIndex() + objSize);
            return object;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                unmarshaller.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
