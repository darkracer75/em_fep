package com.em.fep.server;

import com.em.fep.codec.FepRequestDecoder;
import com.em.fep.codec.FepRequestEncoder;
import com.em.fep.codec.PacketLengthFieldBasedFrameDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringEncoder;

public class FepServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        PacketLengthFieldBasedFrameDecoder frameDecoder =  new PacketLengthFieldBasedFrameDecoder(4096, 0, 4);
        ChannelPipeline p = ch.pipeline();
        p.addLast( frameDecoder, new StringEncoder(), new FepRequestDecoder(), new FepRequestEncoder(), new FepServerHandShakingHandler());
    }
}
