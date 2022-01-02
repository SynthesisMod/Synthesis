package com.luna.synthesis.events.packet;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.Packet;
import net.minecraftforge.common.MinecraftForge;

// https://github.com/ChatTriggers/ChatTriggers/pull/232
public class CustomChannelDuplexHandler extends ChannelDuplexHandler {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Packet<?>) {
            PacketReceivedEvent event = new PacketReceivedEvent((Packet<?>) msg);
            MinecraftForge.EVENT_BUS.post(event);

            if (!event.isCanceled())
                ctx.fireChannelRead(event.getPacket());
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        if (msg instanceof Packet<?>) {
            PacketSentEvent event = new PacketSentEvent((Packet<?>) msg);
            MinecraftForge.EVENT_BUS.post(event);

            if (!event.isCanceled())
                ctx.write(event.getPacket(), promise);
        }
    }
}