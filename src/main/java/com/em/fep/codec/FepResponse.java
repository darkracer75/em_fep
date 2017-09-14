package com.em.fep.codec;

public interface FepResponse {

    PacketHeader getHeader();

    byte[] toByteArray();

    void setHeader(PacketHeader header);
}
