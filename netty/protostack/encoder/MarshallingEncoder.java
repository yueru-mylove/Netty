package com.phei.netty.protostack.encoder;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.Marshaller;

import java.io.IOException;

public class MarshallingEncoder {

    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
    private Marshaller marshaller;

    public MarshallingEncoder() {
        this.marshaller = MarshallingCodeFactory.buildMarshaller();
    }

    protected void encode(Object msg, ByteBuf buf) {
        try {
            int lengthPos = buf.writerIndex();
            buf.writeBytes(LENGTH_PLACEHOLDER);
            ChannelBufferByteOutput bufferByteOutput = new ChannelBufferByteOutput(buf);
            marshaller.start(bufferByteOutput);
            marshaller.writeObject(msg);
            marshaller.finish();
            buf.setInt(lengthPos, buf.writerIndex() - lengthPos - 4);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (marshaller != null) {
                try {
                    marshaller.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
