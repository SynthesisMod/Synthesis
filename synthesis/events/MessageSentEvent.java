package com.luna.synthesis.events;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class MessageSentEvent extends Event {
    public String message;

    public MessageSentEvent(String message) {
        this.message = message;
    }
}