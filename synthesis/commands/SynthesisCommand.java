package com.luna.synthesis.commands;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import com.luna.synthesis.managers.BackpackManager;
import com.luna.synthesis.utils.ChatLib;
import gg.essential.api.EssentialAPI;
import gg.essential.api.commands.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

import java.util.*;

public class SynthesisCommand extends Command {
    public SynthesisCommand() {
        super("synthesis");
    }

    private final Config config = Synthesis.getInstance().getConfig();

    @Override
    public Set<Alias> getCommandAliases() {
        return new HashSet<>(Arrays.asList(new Alias("syn"), new Alias("synth")));
    }

    @DefaultHandler
    public void handle() {
        EssentialAPI.getGuiUtil().openScreen(Synthesis.getInstance().getConfig().gui());
    }

    @SubCommand("bp")
    public void bp(@DisplayName("backpack number") int number, @DisplayName("texture name") Optional<String> name, @DisplayName("texture meta") Optional<Integer> meta) {
        BackpackManager bpm = Synthesis.getInstance().getBackpackManager();
        if (number < 1 || number > 18) {
            ChatLib.chat("That's not a valid backpack number.");
            return;
        }
        if (!name.isPresent()) {
            bpm.modifyData(String.valueOf(number), "", 0);
            ChatLib.chat("Removed custom texture from backpack " + number);
        } else {
            String itemName = name.get().toLowerCase();
            if (!itemName.startsWith("minecraft:")) {
                itemName = "minecraft:" + itemName;
            }
            Item item = Item.getByNameOrId(itemName);
            if (item == null) {
                ChatLib.chat("Item \"" + itemName + "\" does not exist.");
                return;
            }
            if (meta.isPresent()) {
                List<ItemStack> items = new ArrayList<>();
                item.getSubItems(item, item.getCreativeTab(), items);
                if (items.size() - 1 < meta.get()) {
                    ChatLib.chat("Meta can't be that high for this item.");
                    return;
                }
                bpm.modifyData(String.valueOf(number), itemName, meta.get());
                ChatLib.chat("Added " + itemName + " texture to backpack " + number + " with meta " + meta.get());
            } else {
                bpm.modifyData(String.valueOf(number), itemName, 0);
                ChatLib.chat("Added " + itemName + " texture to backpack " + number);
            }
        }
    }

    @SubCommand("domains")
    public void domains(@Options({"add", "remove", "list"}) String options, @DisplayName("domain") Optional<String> domain) {
        if (!Loader.isModLoaded("patcher")) {
            ChatLib.chat("You can only use this feature if you use patcher.");
            return;
        }
        switch (options) {
            case "add":
                if (!domain.isPresent()) {
                    ChatLib.chat("You need to specify a domain. Example: bigraccoon.monster");
                    return;
                }
                if (config.patcherCustomDomains.contains(domain.get().toLowerCase())) {
                    ChatLib.chat("That's already a trusted domain.");
                    return;
                }
                config.patcherCustomDomains += domain.get().toLowerCase() + ",";
                ChatLib.chat("Added &a" + domain.get() + "&r to the trusted domain list.");
                config.markDirty();
                config.writeData();
                break;
            case "remove":
                if (!domain.isPresent()) {
                    ChatLib.chat("You need to specify a domain. Example: bigraccoon.monster");
                    return;
                }
                if (!config.patcherCustomDomains.contains(domain.get().toLowerCase())) {
                    ChatLib.chat("That was not a trusted domain.");
                    return;
                }
                config.patcherCustomDomains = config.patcherCustomDomains.replace(domain.get().toLowerCase() + ",", "");
                ChatLib.chat("Removed &c" + domain.get() + "&r from the trusted domain list.");
                config.markDirty();
                config.writeData();
                break;
            case "list":
                if (config.patcherCustomDomains.equals("")) {
                    ChatLib.chat("There aren't any trusted domains.");
                    return;
                }
                ChatLib.chat("&aList of domains: ");
                for (String s : config.patcherCustomDomains.split(",")) {
                    ChatLib.chat(" &7- &a" + s);
                }
                if (domain.isPresent()) {
                    ChatLib.chat("&dYou don't need to specify another argument, btw.");
                }
                break;
        }
    }
}