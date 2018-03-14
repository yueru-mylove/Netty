package com.phei.netty.protostack.encoder;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.ByteInput;

import java.io.IOException;

public class ChannelBufferByteInput implements ByteInput {

    private ByteBuf buf;

    public ChannelBufferByteInput(ByteBuf buf) {
        this.buf = buf;
    }

    public int read() throws IOException {
        return buf.isReadable() ? buf.readByte() & 0xff : -1;
    }

    public int read(byte[] bytes) throws IOException {
        return this.read(bytes, 0, bytes.length);
    }

    public int read(byte[] bytes, int i, int i1) throws IOException {
        int available = buf.readableBytes();
        if (available == 0) {
            return -1;
        } else {
            i1 = Math.min(available, i1);
            buf.readBytes(bytes, i, i1);
            return i1;
        }
    }

    public int available() throws IOException {
        return this.buf.readableBytes();
    }

    public long skip(long l) throws IOException {
        int i = this.buf.readableBytes();
        if(i < l) {
            l = i;
        }
        this.buf.readerIndex((int) (this.buf.readerIndex() + l));
        return l;
    }

    public void close() throws IOException {

    }
}
