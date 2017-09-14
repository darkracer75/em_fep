package com.em.fep.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class FepRequestDecoder extends MessageToMessageDecoder<ByteBuf> {

    public static final Charset charset = Charset.defaultCharset();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {

        PacketHeader header = new PacketHeader();
        header.setLength(Integer.valueOf(readString(4, msg)));
        header.setTaskDivCode(readString(3, msg));
        header.setOrganCode(readString(2, msg));
        header.setKindCode(readString(4, msg));
        header.setTransDivCode(readString(1, msg));
        header.setSendRcvFlag(readString(1, msg));
        header.setFileName(readString(8, msg));
        header.setResponseCode(readString(3, msg));

        FepRequest fepRequest = FepRequestFactory.getFepRequest(header.getKindCode());

        if (fepRequest != null) {
            fepRequest.setHeader(header);
            fepRequest.parseBody(msg.readBytes(msg.readableBytes()));
            out.add(fepRequest);
        }
    }

    public String readString(int length, ByteBuf in) {
        return in.readBytes(length).toString(charset);
    }

}
