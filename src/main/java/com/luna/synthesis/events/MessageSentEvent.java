package com.luna.synthesis.events;

import lombok.Getter;
import net.minecraftforge.fml.common.eventhandler.Event;

public class MessageSentEvent extends Event {
    @Getter private final String message;
    private Boolean cancelled = false;

    public MessageSentEvent(String message) {
        this.message = message;
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