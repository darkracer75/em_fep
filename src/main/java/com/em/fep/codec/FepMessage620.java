package com.em.fep.codec;

import io.netty.buffer.ByteBuf;
import lombok.ToString;

import java.nio.charset.Charset;

@ToString
public class FepMessage620 implements  FepRequest {

    private PacketHeader header;
    private int blockNo;
    private int sequenceNo;

    @Override
    public void parseBody(ByteBuf body) {

        String bodyStr = body.readBytes(7).toString(Charset.defaultCharset());

        blockNo = Integer.valueOf(bodyStr.substring(0, 4));
        sequenceNo = Integer.valueOf(bodyStr.substring(4, 7));
    }

    public void setHeader(PacketHeader header) {
        this.header = header;
    }

    @Override
    public PacketHeader getHeader() {
        return header;
    }

    public int getBlockNo() {
        return blockNo;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

}
