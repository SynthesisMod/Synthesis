package com.luna.synthesis.features.utilities;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import com.luna.synthesis.events.packet.PacketReceivedEvent;
import com.luna.synthesis.utils.ChatLib;
import com.luna.synthesis.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.util.*;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;

public class AncestralSpade {

    private final Config config = Synthesis.getInstance().getConfig();
    private final ResourceLocation beaconBeam = new ResourceLocation("textures/entity/beacon_beam.png");
    private boolean awaiting = false;
    private boolean awaitingForArrow = false;
    private long lastSpoon = -1L;
    private Vec3 pos1 = null;
    private Vec3 pos2 = null;
    private Vec3 vec1 = null;
    private Vec3 vec2 = null;
    private BlockPos solution = null;

    @SubscribeEvent
    public void onPacketReceived(PacketReceivedEvent event) {
        if (!config.utilitiesAncestralSpade) return;
        if (event.getPacket() instanceof S2APacketParticles) {
            S2APacketParticles packet = (S2APacketParticles) event.getPacket();
            if (packet.getParticleType() == EnumParticleTypes.FIREWORKS_SPARK && packet.getXOffset() == 0 && packet.getYOffset() == 0 && packet.getZOffset() == 0) {
                if (packet.getParticleSpeed() == 0 && packet.getParticleCount() == 1) {
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
            } else if (packet.getParticleType() == EnumParticleTypes.REDSTONE && packet.getParticleSpeed() == 1 && packet.getParticleCount() == 0) {
                if (awaitingForArrow) {
                    if (pos1 == null) {
                        pos1 = new Vec3(packet.getXCoordinate(), packet.getYCoordinate(), packet.getZCoordinate());
                    } else if (vec1 == null) {
                        if (packet.getXCoordinate() - pos1.xCoord == 0 && packet.getZCoordinate() - pos1.zCoord == 0) return;
                        vec1 = new Vec3(packet.getXCoordinate() - pos1.xCoord, packet.getYCoordinate() - pos1.yCoord, packet.getZCoordinate() - pos1.zCoord).normalize();
                        awaitingForArrow = false;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent event) {
        if (!config.utilitiesAncestralSpade) return;
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = Minecraft.getMinecraft().thePlayer.getHeldItem();
            if (item == null) return;
            if (StringUtils.stripControlCodes(item.getDisplayName()).contains("Ancestral Spade")) {
                if (System.currentTimeMillis() >= lastSpoon + 3000) {
                    if (Minecraft.getMinecraft().thePlayer.rotationPitch == 90 || Minecraft.getMinecraft().thePlayer.rotationPitch == -90) {
                        awaiting = true;
                        lastSpoon = System.currentTimeMillis();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (solution != null && Minecraft.getMinecraft().thePlayer != null) {
            if (Math.pow(Minecraft.getMinecraft().thePlayer.posX - solution.getX(), 2) + Math.pow(Minecraft.getMinecraft().thePlayer.posZ - solution.getZ(), 2) <= 9) {
                solution = null;
            }
        }
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        if (event.type == 2) return;
        if (!config.utilitiesAncestralSpade || !config.utilitiesAncestralSpadeArrow) return;
        String msg = StringUtils.stripControlCodes(event.message.getUnformattedText());
        if (msg.startsWith("You dug out a Griffin Burrow!")) {
            awaitingForArrow = true;
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (config.utilitiesAncestralSpadeWaypoint && solution != null) {
            double renderPosX = Minecraft.getMinecraft().getRenderManager().viewerPosX;
            double renderPosY = Minecraft.getMinecraft().getRenderManager().viewerPosY;
            double renderPosZ = Minecraft.getMinecraft().getRenderManager().viewerPosZ;
            GlStateManager.pushMatrix();
            GlStateManager.translate(-renderPosX, -renderPosY, -renderPosZ);
            Minecraft.getMinecraft().getTextureManager().bindTexture(beaconBeam);
            Utils.renderBeamSegment(solution.getX(), 0, solution.getZ(), event.partialTicks, 1.0, Minecraft.getMinecraft().theWorld.getTotalWorldTime(), 0, 256, config.utilitiesAncestralSpadeWaypointColor.getColorComponents(null));
            GlStateManager.translate(renderPosX, renderPosY, renderPosZ);
            GlStateManager.popMatrix();
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        pos1 = null;
        pos2 = null;
        vec1 = null;
        vec2 = null;
        awaiting = false;
        solution = null;
    }

    // All math in the mod is thanks to Lucy, this included, thank you, Lucy!
    // If you don't understand something don't blame me, I just copied her notes.
    // And no, you cannot expect me to do very simple math, I will simply ask the math genius when possible.
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
        this.solution = solution;
        pos1 = pos2 = vec1 = vec2 = null;
    }
}
