package com.luna.synthesis.events.packet;

import lombok.Getter;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PacketSentEvent extends Event {
    @Getter private final Packet<?> packet;
    private Boolean cancelled = false;

    public PacketSentEvent(Packet<?> packet) {
        this.packet = packet;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    @Override
    public boolean isCanceled() {
        return cancelled;
    }

    @Override
    public void setCanceled(boolean cancel) {
        cancelled = cancel;
    }
}
