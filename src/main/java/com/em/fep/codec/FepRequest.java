package com.em.fep.codec;

import io.netty.buffer.ByteBuf;

public interface FepRequest {

    PacketHeader getHeader();

    void parseBody(ByteBuf body);

    void setHeader(PacketHeader header);
}
