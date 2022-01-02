package com.luna.synthesis.events.packet;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class PacketEvent {
    @SubscribeEvent
    public void onNetworkEvent(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        event.manager.channel().pipeline().addAfter(
            "fml:packet_handler",
            "synthesis_packet_handler",
            new CustomChannelDuplexHandler()
        );
    }
}
