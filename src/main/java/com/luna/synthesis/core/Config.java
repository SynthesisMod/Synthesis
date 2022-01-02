package com.luna.synthesis.core;

import com.luna.synthesis.Synthesis;
import gg.essential.api.EssentialAPI;
import gg.essential.api.data.OnboardingData;
import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.JVMAnnotationPropertyCollector;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import scala.sys.Prop;

import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Config extends Vigilant {

    @Property(
            type = PropertyType.SELECTOR,
            name = "Collection tooltips",
            description = "Cleans some lines on the co-op collection display.",
            category = "Cleanup",
            subcategory = "Co-op",
            options = {
                    "Unchanged",
                    "Empty contributions",
                    "Other people's contributions",
                    "All contributions"
            }
    )
    public int cleanupCoopCollections = 0;

    @Property(
            type = PropertyType.SELECTOR,
            name = "Auction creation",
            description = "Removes co-op auction creation messages.",
            category = "Cleanup",
            subcategory = "Co-op",
            options = {
                    "None",
                    "Own auctions",
                    "Own auctions and show price",
                    "Other people's auctions",
                    "All auctions",
                    "All auctions and show price for own"
            }
    )
    public int cleanupCoopAuctionCreation = 0;

    @Property(
            type = PropertyType.SELECTOR,
            name = "Auction cancellations",
            description = "Removes co-op auction cancellation messages.",
            category = "Cleanup",
            subcategory = "Co-op",
            options = {
                    "None",
                    "Own auctions",
                    "Other people's auctions",
                    "All auctions"
            }
    )
    public int cleanupCoopAuctionCancellation = 0;

    @Property(
            type = PropertyType.SELECTOR,
            name = "Auction collections",
            description = "Removes co-op auction collection messages.",
            category = "Cleanup",
            subcategory = "Co-op",
            options = {
                    "None",
                    "Own auctions",
                    "Other people's auctions",
                    "All auctions"
            }
    )
    public int cleanupCoopAuctionCollection = 0;

    @Property(
            type = PropertyType.SELECTOR,
            name = "Beacon stat changes",
            description = "Removes co-op beacon stat changes messages.",
            category = "Cleanup",
            subcategory = "Co-op",
            options = {
                    "None",
                    "Own changes",
                    "Other people's changes",
                    "All changes"
            }
    )
    public int cleanupCoopBeaconStatChanges = 0;

    @Property(
            type = PropertyType.SWITCH,
            name = "Co-op travel messages",
            description = "Removes the chat message when your coop members travel to another island.",
            category = "Cleanup",
            subcategory = "Co-op"
    )
    public boolean cleanupCoopTravel = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Keep sent messages",
            description = "Clearing the chat with F3 + D won't clear sent messages from the up and down arrows.",
            category = "Future features"
    )
    public boolean futureKeepSentMessages = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Chunk borders",
            description = "Pressing F3 + G toggles chunk borders.",
            category = "Future features"
    )
    public boolean futureChunkBorders = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Container chat",
            description = "Opens the chat when having a container open.",
            category = "Utilities"
    )
    public boolean utilitiesContainerChat = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Resize chat",
            description = "Resizes chat when inside a container for it to fit on the screen.",
            category = "Utilities"
    )
    public boolean utilitiesResizeContainerChat = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Reopen chat",
            description = "When a container is closed while typing, reopen chat with what you were typing.",
            category = "Utilities"
    )
    public boolean utilitiesReopenContainerChat = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Transfer chat",
            description = "When a container is opened while typing from chat or another container, carry over what you were typing.",
            category = "Utilities"
    )
    public boolean utilitiesTransferContainerChat = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Chat search mode",
            description = "Pressing Ctrl + F when chat is open will toggle search mode.",
            category = "Utilities"
    )
    public boolean utilitiesChatSearchMode = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Chat search instant refresh",
            description = "Chat will look for chat messages every key typed instead of only after pressing enter.",
            category = "Utilities"
    )
    public boolean utilitiesChatSearchKeyRefresh = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Search scroll",
            description = "Scrolls to a message when right clicked on while on search mode.",
            category = "Utilities"
    )
    public boolean utilitiesChatScrollClick = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Backpack retexturing",
            description = "Allows you to retexture storage backpacks.",
            category = "Utilities"
    )
    public boolean utilitiesBackpackRetexturing = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "HOTM Perk Level Display",
            description = "Shows perk level as stack size.",
            category = "Utilities"
    )
    public boolean utilitiesPerkLevelDisplay = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "HOTM Max Perk Display",
            description = "Shows perk level as stack size also on maxed perks.",
            category = "Utilities"
    )
    public boolean utilitiesMaxPerkLevelDisplay = false;

    @Property(
            type = PropertyType.SELECTOR,
            name = "Drop chance to drop rate",
            description = "Displays drop chances as drop rates in bestiary.",
            category = "Utilities",
            options = {
                    "None",
                    "Hold shift",
                    "Permanent"
            }
    )
    public int utilitiesDropChanceToDropRate = 0;

    @Property(
            type = PropertyType.SWITCH,
            name = "Bestiary glance",
            description = "Displays bestiary level and progress in the bestiary menu.",
            category = "Utilities"
    )
    public boolean utilitiesBestiaryGlance = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Armadillo fix",
            description = "Stops armadillo blocking the screen when trying to mine blocks.",
            category = "Utilities"
    )
    public boolean utilitiesArmadilloFix = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Wishing compass triangulation",
            description = "Triangulates the location wishing compass points to. Use wishing compass once, wait until the particle trail has disappeared, move away a bit and use it again. Make sure /pq is NOT \"off\".",
            category = "Utilities"
    )
    public boolean utilitiesWishingCompass = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Block wishing compass",
            description = "Blocks using wishing compass if the last trail hasn't disappeared.",
            category = "Utilities"
    )
    public boolean utilitiesBlockWishingCompass = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Wishing compass waypoints",
            description = "Sets a waypoint at the location calculated by wishing compass. Uses Skytils' waypoints.",
            category = "Utilities"
    )
    public boolean utilitiesWishingCompassWaypoint = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Visible links",
            description = "Makes clickable links blue and underlined.",
            category = "Utilities"
    )
    public boolean utilitiesVisibleLinks = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Colorless panes",
            description = "Removes the color from glass panes so glass blocks are more visible.",
            category = "Utilities"
    )
    public boolean utilitiesColorlessPanes = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Better bridge message",
            description = "Reformats guild bridge messages.",
            category = "Utilities",
            subcategory = "Bridge"
    )
    public boolean utilitiesBridge = false;

    @Property(
            type = PropertyType.TEXT,
            name = "Bridge bot name",
            description = "The IGN of the minecraft account acting as a bridge. Case sensitive.",
            category = "Utilities",
            subcategory = "Bridge",
            min = 1,
            max = 16
    )
    public String utilitiesBridgeBotName = "mmmmBeepBeepBeep";

    @Property(
            type = PropertyType.TEXT,
            name = "Bridge message format",
            description = "The message that will be sent when a message is sent from discord. Use <ign> and <msg> for message sender and message. Use & for color codes.",
            category = "Utilities",
            subcategory = "Bridge"
    )
    public String utilitiesBridgeMessageFormat = "&9[Discord] &6<ign>&r: <msg>";

    @Property(
            type = PropertyType.BUTTON,
            name = "Test bridge message",
            description = "Send in chat a message formatted like the above format. Useful for testing that format.",
            category = "Utilities",
            subcategory = "Bridge"
    )
    public void utilitiesBridgeTestFormat() {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(utilitiesBridgeMessageFormat.replaceAll("&", "§").replace("<ign>", "Luna").replace("<msg>", "This is an example message. Thank you for using Synthesis!")));
    }

    @Property(
            type = PropertyType.TEXT,
            name = "Custom cape",
            description = "Use someone else's optifine cape. Only you will see this! Leave empty to not use someone else's cape.",
            category = "Utilities",
            subcategory = "Capes",
            min = 1,
            max = 16
    )
    public String utilitiesCapesCustomCape = "";

    @Property(
            type = PropertyType.SWITCH,
            name = "Trans yeti",
            description = "Gives the yeti a trans cape.",
            category = "Utilities",
            subcategory = "Capes"
    )
    public boolean utilitiesCapesTransYeti = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Trans terracotta",
            description = "Gives the terracotta a trans cape.",
            category = "Utilities",
            subcategory = "Capes"
    )
    public boolean utilitiesCapesTransTerracotta = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Non binary bonzo",
            description = "Gives bonzo a non binary cape.",
            category = "Utilities",
            subcategory = "Capes"
    )
    public boolean utilitiesCapesNonBinaryBonzo = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Candy cane grinch",
            description = "Gives the grinch a candy cane cape.",
            category = "Utilities",
            subcategory = "Capes"
    )
    public boolean utilitiesCapesCandyCaneGrinch = true;

    //PATCHER

    @Property(
            type = PropertyType.SWITCH,
            name = "Compact chat fix",
            description = "Fixes chat messages not being removed with compact chat when the chat is refreshed.",
            category = "Patcher"
    )
    public boolean patcherCompactChatFix = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Custom image preview domains",
            description = "Lets you set and use custom domains for Patcher's ImagePreview. Do not add sites you do not trust. Use at own risk.",
            category = "Patcher"
    )
    public boolean patcherCustomImagePreviewer = false;

    @Property(
            type = PropertyType.TEXT,
            name = "Custom domains",
            description = "Look, I'm way too lazy to store this in a json as an array or whatever. It's literally just text why would I care.",
            category = "Patcher",
            hidden = true
    )
    public String patcherCustomDomains = "";

    public Config() {
        super(new File(Synthesis.configLocation), "§dSynthesis", new JVMAnnotationPropertyCollector(), new CustomSortingBehavior());
        hidePropertyIf("patcherCompactChatFix", () -> !Loader.isModLoaded("patcher"));
        hidePropertyIf("patcherCustomImagePreviewer", () -> !Loader.isModLoaded("patcher"));
        hidePropertyIf("utilitiesWishingCompassWaypoint", () -> !Loader.isModLoaded("skytils"));
        addDependency("utilitiesWishingCompassWaypoint", "utilitiesWishingCompass");
        addDependency("utilitiesBlockWishingCompass", "utilitiesWishingCompass");
        registerListener("utilitiesColorlessPanes", (z) -> Minecraft.getMinecraft().renderGlobal.loadRenderers());
        initialize();
    }
}