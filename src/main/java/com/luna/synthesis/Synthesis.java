package com.luna.synthesis;

import com.luna.synthesis.commands.SynthesisCommand;
import com.luna.synthesis.core.Config;
import com.luna.synthesis.events.packet.PacketEvent;
import com.luna.synthesis.features.cleanup.CoopCleanup;
import com.luna.synthesis.features.future.ChunkBorders;
import com.luna.synthesis.features.utilities.*;
import com.luna.synthesis.managers.BackpackManager;
import lombok.Getter;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.io.File;

@Mod(
    modid = Synthesis.MODID,
    version = Synthesis.VERSION,
    name = Synthesis.NAME,
    clientSideOnly = true
)
public class Synthesis {
    public static final String NAME = "Synthesis";
    public static final String MODID = "synthesis";
    public static final String VERSION = "Alpha-v4";
    public static final String configLocation = "./config/synthesis.toml";

    @Getter private static Synthesis instance;
    @Getter private final Config config;
    @Getter private final BackpackManager backpackManager;

    public Synthesis() {
        instance = this;
        config = new Config();
        backpackManager = new BackpackManager(new File("./config/synthesisbackpacks.json"));
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new PacketEvent());
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new CoopCleanup());
        MinecraftForge.EVENT_BUS.register(new ChunkBorders());
        MinecraftForge.EVENT_BUS.register(new SearchMode());
        MinecraftForge.EVENT_BUS.register(new BestiaryDropRate());
        MinecraftForge.EVENT_BUS.register(new ContainerChat());
        MinecraftForge.EVENT_BUS.register(new WishingCompass());
        MinecraftForge.EVENT_BUS.register(new ChatBridge());
        MinecraftForge.EVENT_BUS.register(new VisibleLinks());
        MinecraftForge.EVENT_BUS.register(new Share());
        config.preload();
        new SynthesisCommand().register();
    }
}