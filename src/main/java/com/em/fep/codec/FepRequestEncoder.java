package com.em.fep.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class FepRequestEncoder extends MessageToMessageEncoder<FepResponse> {

    @Override
    protected void encode(ChannelHandlerContext ctx, FepResponse msg, List<Object> out) throws Exception {

        byte[] bytes = msg.toByteArray();

        if (bytes.length == 0) {
            return;
        }

        out.add(ctx.alloc().buffer().writeBytes(bytes));
    }
}
