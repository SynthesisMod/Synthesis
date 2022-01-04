package com.luna.synthesis.commands;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import com.luna.synthesis.utils.ChatLib;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class CopyToClipboardCommand extends Command {

    public CopyToClipboardCommand() {
        super("ctcc");
    }

    private final Config config = Synthesis.getInstance().getConfig();

    // I will let you know that I despise having to create commands to run code on chat click.
    // I'll end up rewriting clicks eventually. JUST YOU WAIT.
    @DefaultHandler
    public void handle(String toCopy) {
        if (!config.utilitiesShareCopyEmbed) return;
        ChatLib.chat("Copied &a" + toCopy + "&r to clipboard.");
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(toCopy), null);
    }
}
