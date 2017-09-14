package com.em.fep.codec;

import io.netty.buffer.ByteBuf;
import lombok.ToString;

import java.nio.charset.Charset;

@ToString
public class FepMessage320310 implements  FepRequest {

    private PacketHeader header;
    private int blockNo;
    private int sequenceNo;
    private int byteLength;
    private ByteBuf fileByte;

    @Override
    public void parseBody(ByteBuf body) {

        String bodyStr = body.readBytes(11).toString(Charset.defaultCharset());

        blockNo = Integer.valueOf(bodyStr.substring(0, 4));
        sequenceNo = Integer.valueOf(bodyStr.substring(4, 7));
        byteLength = Integer.valueOf(bodyStr.substring(7, 11));

        fileByte = body.readBytes(byteLength);
    }

    public void setHeader(PacketHeader header) {
        this.header = header;
    }

    @Override
    public PacketHeader getHeader() {
        return header;
    }

    public ByteBuf getFileByte() {
        return fileByte;
    }

    public int getByteLength() {
        return byteLength;
    }

    public int getBlockNo() {
        return blockNo;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

}
