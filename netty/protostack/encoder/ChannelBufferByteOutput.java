package com.phei.netty.protostack.encoder;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.ByteOutput;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ChannelBufferByteOutput implements ByteOutput {

    private final ByteBuf byteBuffer;

    public ChannelBufferByteOutput(ByteBuf buf) {
        this.byteBuffer = buf;
    }


    public void write(int i) throws IOException {
        this.byteBuffer.writeByte(i);
    }

    public void write(byte[] bytes) throws IOException {
        this.byteBuffer.writeBytes(bytes);
    }

    public void write(byte[] bytes, int i, int i1) throws IOException {
        this.byteBuffer.writeBytes(bytes, i, i1);
    }

    public void close() throws IOException {

    }

    public void flush() throws IOException {

    }
}
