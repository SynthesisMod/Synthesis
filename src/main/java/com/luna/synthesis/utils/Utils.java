package com.luna.synthesis.utils;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.lwjgl.opengl.GL11.*;

public class Utils {

    private static final Pattern linkPattern = Pattern.compile("((?:[a-z0-9]{2,}:\\/\\/)?(?:(?:[0-9]{1,3}\\.){3}[0-9]{1,3}|(?:[-\\w_\\.]{1,}\\.[a-z]{2,}?))(?::[0-9]{1,5})?.*?(?=[!\"\u00A7 \n]|$))", Pattern.CASE_INSENSITIVE);

    // Copied and adapted from Patcher
    public static boolean isDivider(String message) {
        if (message == null) return false;
        if (message.length() < 5) {
            return false;
        } else {
            // Dear god, forgive me, for I am tired.
            if (message.equals("--------------  Guild: Message Of The Day  --------------") || message.equals("----------  Guild: Message Of The Day (Preview)  ----------")) {
                return true;
            } else {
                for (int i = 0; i < message.length(); i++) {
                    char c = message.charAt(i);
                    if (c != '-' && c != '=' && c != '\u25AC') {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Adapted from ForgeHooks::newChatWithLinks, may change linkPattern in the future
    public static IChatComponent newChatWithLinks(String string) {
        IChatComponent ichat = null;
        Matcher matcher = linkPattern.matcher(string);
        int lastEnd = 0;

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            String part = string.substring(lastEnd, start);
            if (part.length() > 0) {
                if (ichat == null) {
                    ichat = new ChatComponentText(part);
                } else {
                    ichat.appendText(part);
                }
            }
            lastEnd = end;
            String url = string.substring(start, end);
            IChatComponent link = new ChatComponentText(url);

            try {
                if ((new URI(url)).getScheme() == null) {
                    url = "http://" + url;
                }
            } catch (URISyntaxException e) {
                if (ichat == null) ichat = new ChatComponentText(url);
                else ichat.appendText(url);
                continue;
            }

            ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
            link.getChatStyle().setChatClickEvent(click);
            if (ichat == null) {
                ichat = link;
            } else {
                ichat.appendSibling(link);
            }
        }

        String end = string.substring(lastEnd);
        if (ichat == null) {
            ichat = new ChatComponentText(end);
        } else if (end.length() > 0) {
            ichat.appendText(string.substring(lastEnd));
        }
        return ichat;
    }

    public static void renderBeamSegment(double x, double y, double z, double partialTicks, double textureScale, double totalWorldTime, int yOffset, int height, float[] colors) {
        double beamRadius = 0.2D;
        double glowRadius = 0.25D;
        int i = yOffset + height;
        GL11.glTexParameteri(3553, 10242, 10497);
        GL11.glTexParameteri(3553, 10243, 10497);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE, GL_ONE, GL_ZERO);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        double d0 = totalWorldTime + partialTicks;
        double d1 = height < 0 ? d0 : -d0;
        double d2 = MathHelper.func_181162_h(d1 * 0.2D - (double)MathHelper.floor_double(d1 * 0.1D));
        float f = colors[0];
        float f1 = colors[1];
        float f2 = colors[2];
        double d3 = d0 * 0.025D * -1.5D;
        double d4 = 0.5D + Math.cos(d3 + 2.356194490192345D) * beamRadius;
        double d5 = 0.5D + Math.sin(d3 + 2.356194490192345D) * beamRadius;
        double d6 = 0.5D + Math.cos(d3 + (Math.PI / 4D)) * beamRadius;
        double d7 = 0.5D + Math.sin(d3 + (Math.PI / 4D)) * beamRadius;
        double d8 = 0.5D + Math.cos(d3 + 3.9269908169872414D) * beamRadius;
        double d9 = 0.5D + Math.sin(d3 + 3.9269908169872414D) * beamRadius;
        double d10 = 0.5D + Math.cos(d3 + 5.497787143782138D) * beamRadius;
        double d11 = 0.5D + Math.sin(d3 + 5.497787143782138D) * beamRadius;
        double d14 = -1.0D + d2;
        double d15 = (double)height * textureScale * (0.5D / beamRadius) + d14;
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldRenderer.pos(x + d4, y + (double)i, z + d5).tex(1.0D, d15).color(f, f1, f2, 1.0F).endVertex();
        worldRenderer.pos(x + d4, y + (double)yOffset, z + d5).tex(1.0D, d14).color(f, f1, f2, 1.0F).endVertex();
        worldRenderer.pos(x + d6, y + (double)yOffset, z + d7).tex(0.0D, d14).color(f, f1, f2, 1.0F).endVertex();
        worldRenderer.pos(x + d6, y + (double)i, z + d7).tex(0.0D, d15).color(f, f1, f2, 1.0F).endVertex();
        worldRenderer.pos(x + d10, y + (double)i, z + d11).tex(1.0D, d15).color(f, f1, f2, 1.0F).endVertex();
        worldRenderer.pos(x + d10, y + (double)yOffset, z + d11).tex(1.0D, d14).color(f, f1, f2, 1.0F).endVertex();
        worldRenderer.pos(x + d8, y + (double)yOffset, z + d9).tex(0.0D, d14).color(f, f1, f2, 1.0F).endVertex();
        worldRenderer.pos(x + d8, y + (double)i, z + d9).tex(0.0D, d15).color(f, f1, f2, 1.0F).endVertex();
        worldRenderer.pos(x + d6, y + (double)i, z + d7).tex(1.0D, d15).color(f, f1, f2, 1.0F).endVertex();
        worldRenderer.pos(x + d6, y + (double)yOffset, z + d7).tex(1.0D, d14).color(f, f1, f2, 1.0F).endVertex();
        worldRenderer.pos(x + d10, y + (double)yOffset, z + d11).tex(0.0D, d14).color(f, f1, f2, 1.0F).endVertex();
        worldRenderer.pos(x + d10, y + (double)i, z + d11).tex(0.0D, d15).color(f, f1, f2, 1.0F).endVertex();
        worldRenderer.pos(x + d8, y + (double)i, z + d9).tex(1.0D, d15).color(f, f1, f2, 1.0F).endVertex();
        worldRenderer.pos(x + d8, y + (double)yOffset, z + d9).tex(1.0D, d14).color(f, f1, f2, 1.0F).endVertex();
        worldRenderer.pos(x + d4, y + (double)yOffset, z + d5).tex(0.0D, d14).color(f, f1, f2, 1.0F).endVertex();
        worldRenderer.pos(x + d4, y + (double)i, z + d5).tex(0.0D, d15).color(f, f1, f2, 1.0F).endVertex();
        tessellator.draw();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        GlStateManager.depthMask(false);
        d3 = 0.5D - glowRadius;
        d4 = 0.5D - glowRadius;
        d5 = 0.5D + glowRadius;
        d6 = 0.5D - glowRadius;
        d7 = 0.5D - glowRadius;
        d8 = 0.5D + glowRadius;
        d9 = 0.5D + glowRadius;
        d10 = 0.5D + glowRadius;
        double d13 = -1.0D + d2;
        d14 = (double)height * textureScale + d13;
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldRenderer.pos(x + d3, y + (double)i, z + d4).tex(1.0D, d14).color(f, f1, f2, 0.125F).endVertex();
        worldRenderer.pos(x + d3, y + (double)yOffset, z + d4).tex(1.0D, d13).color(f, f1, f2, 0.125F).endVertex();
        worldRenderer.pos(x + d5, y + (double)yOffset, z + d6).tex(0.0D, d13).color(f, f1, f2, 0.125F).endVertex();
        worldRenderer.pos(x + d5, y + (double)i, z + d6).tex(0.0D, d14).color(f, f1, f2, 0.125F).endVertex();
        worldRenderer.pos(x + d9, y + (double)i, z + d10).tex(1.0D, d14).color(f, f1, f2, 0.125F).endVertex();
        worldRenderer.pos(x + d9, y + (double)yOffset, z + d10).tex(1.0D, d13).color(f, f1, f2, 0.125F).endVertex();
        worldRenderer.pos(x + d7, y + (double)yOffset, z + d8).tex(0.0D, d13).color(f, f1, f2, 0.125F).endVertex();
        worldRenderer.pos(x + d7, y + (double)i, z + d8).tex(0.0D, d14).color(f, f1, f2, 0.125F).endVertex();
        worldRenderer.pos(x + d5, y + (double)i, z + d6).tex(1.0D, d14).color(f, f1, f2, 0.125F).endVertex();
        worldRenderer.pos(x + d5, y + (double)yOffset, z + d6).tex(1.0D, d13).color(f, f1, f2, 0.125F).endVertex();
        worldRenderer.pos(x + d9, y + (double)yOffset, z + d10).tex(0.0D, d13).color(f, f1, f2, 0.125F).endVertex();
        worldRenderer.pos(x + d9, y + (double)i, z + d10).tex(0.0D, d14).color(f, f1, f2, 0.125F).endVertex();
        worldRenderer.pos(x + d7, y + (double)i, z + d8).tex(1.0D, d14).color(f, f1, f2, 0.125F).endVertex();
        worldRenderer.pos(x + d7, y + (double)yOffset, z + d8).tex(1.0D, d13).color(f, f1, f2, 0.125F).endVertex();
        worldRenderer.pos(x + d3, y + (double)yOffset, z + d4).tex(0.0D, d13).color(f, f1, f2, 0.125F).endVertex();
        worldRenderer.pos(x + d3, y + (double)i, z + d4).tex(0.0D, d14).color(f, f1, f2, 0.125F).endVertex();
        tessellator.draw();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
    }
}
