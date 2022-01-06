package com.luna.synthesis.features.utilities;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import com.luna.synthesis.events.packet.PacketReceivedEvent;
import com.luna.synthesis.utils.ChatLib;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WishingCompass {

    private final Config config = Synthesis.getInstance().getConfig();
    private boolean awaiting = false;
    private long lastCompass = -1L;
    private Vec3 pos1 = null;
    private Vec3 pos2 = null;
    private Vec3 vec1 = null;
    private Vec3 vec2 = null;

    @SubscribeEvent
    public void onPacketReceived(PacketReceivedEvent event) {
        if (!config.utilitiesWishingCompass) return;
        if (event.getPacket() instanceof S2APacketParticles) {
            S2APacketParticles packet = (S2APacketParticles) event.getPacket();
            if (packet.getParticleType() == EnumParticleTypes.VILLAGER_HAPPY && packet.getParticleSpeed() == 0.0 && packet.getParticleCount() == 1.0) {
                if (awaiting) {
                    if (pos1 == null) {
                        pos1 = new Vec3(packet.getXCoordinate(), packet.getYCoordinate(), packet.getZCoordinate());
                        awaiting = false;
                    } else if (pos2 == null) {
                        pos2 = new Vec3(packet.getXCoordinate(), packet.getYCoordinate(), packet.getZCoordinate());
                        awaiting = false;
                    }
                } else {
                    if (vec1 == null && pos1 != null) {
                        vec1 = new Vec3(packet.getXCoordinate() - pos1.xCoord, packet.getYCoordinate() - pos1.yCoord, packet.getZCoordinate() - pos1.zCoord).normalize();
                    } else if (vec2 == null && pos2 != null) {
                        vec2 = new Vec3(packet.getXCoordinate() - pos2.xCoord, packet.getYCoordinate() - pos2.yCoord, packet.getZCoordinate() - pos2.zCoord).normalize();
                        calculateIntercept();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent event) {
        if (!config.utilitiesWishingCompass) return;
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = Minecraft.getMinecraft().thePlayer.getHeldItem();
            if (item == null) return;
            if (StringUtils.stripControlCodes(item.getDisplayName()).contains("Wishing Compass")) {
                if (config.utilitiesBlockWishingCompass && System.currentTimeMillis() - lastCompass < 4000) {
                    ChatLib.chat("Last wishing compass hasn't disappeared yet, chill.");
                    event.setCanceled(true);
                    return;
                }
                awaiting = true;
                lastCompass = System.currentTimeMillis();
            }
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        pos1 = null;
        pos2 = null;
        vec1 = null;
        vec2 = null;
        awaiting = false;
    }

    // All math in the mod is thanks to Lucy, this included, thank you, Lucy!
    // If you don't understand something don't blame me, I just copied her notes.
    // And no, you cannot expect me to do very simple math, I will simply ask the math genius when possible.
    private void calculateIntercept() {
        double a = pos1.xCoord;
        double b = pos1.yCoord;
        double c = pos1.zCoord;
        double i = vec1.xCoord;
        double j = vec1.yCoord;
        double k = vec1.zCoord;
        //
        double h = pos2.xCoord;
        double v = pos2.yCoord;
        double w = pos2.zCoord;
        double l = vec2.xCoord;
        double m = vec2.yCoord;
        double n = vec2.zCoord;

        double t = ((b - v) / m - (a - h) / l) / ((i / l) - (j / m));
        double s = (j * (c - w) + k * (v - b)) / (j * n - k * m);

        if (t < 0 || s < 0) {
            ChatLib.chat("Something went wrong. Did you wait until the first particle trail disappeared? Did you move from one quadrant to another?");
        } else {
            BlockPos solution = new BlockPos((a + t * i + h + s * l) / 2, (b + t * j + v + s * m) / 2, (c + t * k + w + s * n) / 2);
            if (Math.abs(solution.getX() - 513) < 65 && Math.abs(solution.getZ() - 513) < 65) {
                ChatLib.chat("This compass points to the nucleus! You need to place crystals so the compass points somewhere else. It's also possible that the structure hasn't spawned.");
            } else {
                ChatLib.chat("Solution: (" + solution.getX() + ", " + solution.getY() + ", " + solution.getZ() + ")");
                if (config.utilitiesWishingCompassWaypoint) {
                    ClientCommandHandler.instance.executeCommand(Minecraft.getMinecraft().thePlayer, "/sthw set WishingCompass " + solution.getX() + " " + solution.getY() + " " + solution.getZ());
                }
            }
        }

        pos1 = pos2 = vec1 = vec2 = null;
    }
}
