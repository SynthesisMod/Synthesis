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
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AncestralSpade {

    private final Config config = Synthesis.getInstance().getConfig();
    private boolean awaiting = false;
    private int particleCount = 0;
    private long lastItem = -1L;
    private Vec3 pos1 = null;
    private Vec3 pos2 = null;
    private Vec3 vec1 = null;
    private Vec3 vec2 = null;

    @SubscribeEvent
    public void onPacketReceived(PacketReceivedEvent event) {
        if (!config.utilitiesTriangulation) return;
        if (event.getPacket() instanceof S2APacketParticles) {
            S2APacketParticles packet = (S2APacketParticles) event.getPacket();
            if (packet.getParticleType() == EnumParticleTypes.FIREWORKS_SPARK) {
                particleCount++;
                if (awaiting) {
                    if (particleCount == 10 && pos1 == null) {
                        pos1 = new Vec3(packet.getXCoordinate(), packet.getYCoordinate(), packet.getZCoordinate());
                        awaiting = false;
                    } else if (particleCount == 10 && pos2 == null) {
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
        if (!config.utilitiesTriangulation) return;
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = Minecraft.getMinecraft().thePlayer.getHeldItem();
            if (item == null) return;
            if (StringUtils.stripControlCodes(item.getDisplayName()).contains("Ancestral Spade")) {
                if (config.utilitiesBlockTriangulationItem && System.currentTimeMillis() - lastItem < 4000) {
                    ChatLib.chat("Last trail hasn't disappeared yet, chill.");
                    event.setCanceled(true);
                    return;
                }
                awaiting = true;
                particleCount = 0;
                lastItem = System.currentTimeMillis();
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
        lastItem = -1L;
        particleCount = 0;
    }

    // Thank you, Ollie, for doing this for me, so I don't have to
    private void calculateIntercept() {
        double p1x = pos1.xCoord;
        double p1z = pos1.zCoord;
        double v1x = vec1.xCoord;
        double v1z = vec1.zCoord;
        //
        double p2x = pos2.xCoord;
        double p2z = pos2.zCoord;
        double v2x = vec2.xCoord;
        double v2z = vec2.zCoord;
        double a = v1z / v1x * p1x - p1z;
        double b = v2z / v2x * p2x - p2z;
        double x = (a - b) / (v1z / v1x - v2z / v2x);
        double z = v1z / v1x * x - a;

        BlockPos solution = new BlockPos(x, 0, z);
        ChatLib.chat("Solution: (" + solution.getX() + ", " + solution.getZ() + ")");

        pos1 = pos2 = vec1 = vec2 = null;
    }
}
