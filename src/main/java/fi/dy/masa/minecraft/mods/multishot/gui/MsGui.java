package fi.dy.masa.minecraft.mods.multishot.gui;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import fi.dy.masa.minecraft.mods.multishot.config.Configs;
import fi.dy.masa.minecraft.mods.multishot.handlers.RenderEventHandler.MarkerColor;
import fi.dy.masa.minecraft.mods.multishot.motion.Motion;
import fi.dy.masa.minecraft.mods.multishot.motion.Motion.MsPath;
import fi.dy.masa.minecraft.mods.multishot.motion.Motion.MsPoint;
import fi.dy.masa.minecraft.mods.multishot.reference.Constants;
import fi.dy.masa.minecraft.mods.multishot.reference.Reference;
import fi.dy.masa.minecraft.mods.multishot.state.State;

public class MsGui extends Gui
{
    private static MsGui instance;
    private Minecraft mc = null;
    private GuiMessage[] guiMessages = null;
    private int msgWr = 0;

    public MsGui()
    {
        super();
        instance = this;
        this.mc = Minecraft.getMinecraft();
        this.guiMessages = new GuiMessage[5];
    }

    public static MsGui getGui()
    {
        return instance;
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event)
    {
        this.renderMotionMarkers(event.getPartialTicks());
    }

    @SubscribeEvent
    public void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event)
    {
        if (event.getType() == ElementType.ALL && State.getHideGui() == false)
        {
            this.renderHud();
        }
    }

    private class GuiMessage
    {
        private String msg = "";
        private long msgTime = 0;
        private long life = 0;

        public GuiMessage(String msg, long time, long life)
        {
            this.msg = msg;
            this.msgTime = time;
            this.life = life;
        }

        public long getAge()
        {
            return System.currentTimeMillis() - this.msgTime;
        }

        public boolean getIsDead()
        {
            return this.getAge() > this.life;
        }
/*
        public float getOpacity()
        {
            float age = (float)this.getAge();
            if (age > (1.2f * (float)this.life))
            {
                return 0.0f;
            }
            return (age - (float)this.life) / (0.2f * age);
        }
*/
        public String getMsg()
        {
            return this.msg;
        }
    }

    public void addGuiMessage(String msg, int lifetime)
    {
        synchronized(this)
        {
            this.guiMessages[this.msgWr] = new GuiMessage(msg, System.currentTimeMillis(), lifetime);

            if (++this.msgWr >= this.guiMessages.length)
            {
                this.msgWr = 0;
            }
        }
    }

    public void addGuiMessage(String msg)
    {
        this.addGuiMessage(msg, 10000); // default to 10000ms = 10s
    }

    private void renderHud()
    {
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);

        int scaledX = scaledResolution.getScaledWidth();
        int scaledY = scaledResolution.getScaledHeight();
        int offsetX = Configs.getGuiOffsetX();
        int offsetY = Configs.getGuiOffsetY();
        int x = 0;
        int y = 0;
        int msgX = 0;
        int msgY = 0;
        float msgScale = 0.5f;

        // 0 = Top Right, 1 = Bottom Right, 2 = Bottom Left, 3 = Top Left
        if (Configs.getGuiPosition() == 0) // Top Right
        {
            x = scaledX + offsetX - 48;
            y = 0 + offsetY;
            msgX = (int)((float)(scaledX + offsetX - 215) / msgScale);
            msgY = (int)((float)(offsetY + 1) / msgScale);
        }
        else if (Configs.getGuiPosition() == 1) // Bottom Right
        {
            x = scaledX + offsetX - 48;
            y = scaledY + offsetY - 16;
            msgX = (int)((float)(scaledX + offsetX - 165) / msgScale);
            msgY = (int)((float)(scaledY + offsetY - 43) / msgScale);
        }
        else if (Configs.getGuiPosition() == 2) // Bottom Left
        {
            x = offsetX + 0;
            y = scaledY + offsetY - 16;
            msgX = (int)((float)(offsetX + 1) / msgScale);
            msgY = (int)((float)(scaledY + offsetY - 43) / msgScale);
        }
        else if (Configs.getGuiPosition() == 3) // Top Left
        {
            x = offsetX + 0;
            y = offsetY + 0;
            msgX = (int)((float)(offsetX + 50) / msgScale);
            msgY = (int)((float)(offsetY + 1) / msgScale);
        }

        this.mc.getTextureManager().bindTexture(Reference.GUI_HUD);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableLighting();

        // We now always force lock the controls in motion mode
        if (State.getControlsLocked() == true || State.getMotion() == true)
        {
            this.drawTexturedModalRect(x + 0, y, 0, 0, 16, 16); // Controls locked
        }
        else
        {
            this.drawTexturedModalRect(x + 0, y, 0, 16, 16, 16); // Controls not locked
        }
        if (State.getMotion() == true)
        {
            this.drawTexturedModalRect(x + 16, y, 16, 0, 16, 16); // Motion ON
        }
        else
        {
            this.drawTexturedModalRect(x + 16, y, 16, 16, 16, 16); // Motion OFF
        }
        if (State.getRecording() == true)
        {
            if (State.getPaused() == true)
            {
                this.drawTexturedModalRect(x + 32, y, 32, 16, 16, 16); // Recording and paused
            }
            else
            {
                this.drawTexturedModalRect(x + 32, y, 32, 0, 16, 16); // Recording, not paused
            }
        }
        else
        {
            this.drawTexturedModalRect(x + 32, y, 32, 32, 16, 16); // Stopped
        }

        this.renderGuiMessages(msgX, msgY, msgScale);
    }

    private void renderGuiMessages(int msgX, int msgY, float msgScale)
    {
        GlStateManager.pushMatrix();
        GlStateManager.scale(msgScale, msgScale, msgScale);

        synchronized(this)
        {
            int maxLines = 5;

            for (int i = 0, readIndex = this.msgWr, yOff = 0; i < maxLines; i++, readIndex++)
            {
                if (readIndex >= this.guiMessages.length)
                {
                    readIndex = 0;
                }

                if (this.guiMessages[readIndex] != null)
                {
                    String s = this.guiMessages[readIndex].getMsg();

                    if (this.guiMessages[readIndex].getIsDead() == false)
                    {
                        this.mc.ingameGUI.drawString(this.mc.fontRenderer, s, msgX, msgY + yOff, 0xffffffff);
                        yOff += 8;
                    }
                }
            }
        }

        GlStateManager.popMatrix();
    }

    private void drawPointMarker(MsPoint point, int index, MarkerColor color, float partialTicks)
    {
        EntityPlayer player = this.mc.player;
        // Player position
        double plX = player.lastTickPosX + ((player.posX - player.lastTickPosX) * partialTicks);
        double plY = player.lastTickPosY + ((player.posY - player.lastTickPosY) * partialTicks);
        double plZ = player.lastTickPosZ + ((player.posZ - player.lastTickPosZ) * partialTicks);

        double pX = point.getX();
        double pY = point.getY() + player.getEyeHeight(); // Draw the markers at the player's eye level, not feet
        double pZ = point.getZ();

        double angleH = Math.PI / 2.0;
        double zDiff = pZ - plZ;

        if (zDiff != 0.0)
        {
            // the angle in which the player sees the marker, in relation to the positive z-axis
            angleH = Math.atan2(pX - plX, zDiff);
        }

        angleH = angleH * 180d / Math.PI;

        /*double angleV = Math.PI / 2.0;
        double hDist = MathHelper.distance2D(plX, plZ, pX, pZ); // horizontal distance from the player to the marker

        if (hDist != 0.0)
        {
            // the angle in which the player sees the marker, in relation to the xz-plane
            angleV = Math.atan((plY - point.getY()) / hDist);
        }*/

        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.translate(-plX + pX, -plY + pY, -plZ + pZ);
        GlStateManager.rotate((float) angleH, 0f, 1f, 0f);
        //GlStateManager.rotate((float) (angleV * 180d / Math.PI), 1f, 0f, 0f);
        GlStateManager.scale(0.35, 0.35, 0.35);
        GlStateManager.translate(-0.5, -0.5, -0.5);

        IBakedModel model = this.mc.getRenderItem().getItemModelMesher().getModelManager().getModel(color.getModelLocation());
        this.mc.getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(model, 1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        if (index >= 0)
        {
            this.renderLabel(String.valueOf(index + 1), pX - plX, pY - plY, pZ - plZ, (float) angleH, 0f);
        }
    }

    private void renderLabel(String text, double x, double y, double z, float angleH, float angleV)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 0.055f, z);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(angleH, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(angleV, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(-0.0125f, 0f, -0.0126f);
        GlStateManager.scale(-0.016F, -0.016F, 0.016F);
        /*GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture2D();*/

        FontRenderer fontrenderer = this.mc.fontRenderer;
        int strLenHalved = fontrenderer.getStringWidth(text) / 2;

        /*Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();

        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vertexbuffer.pos(-strLenHalved - 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        vertexbuffer.pos(-strLenHalved - 1,  8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        vertexbuffer.pos( strLenHalved + 1,  8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        vertexbuffer.pos( strLenHalved + 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        tessellator.draw();

        GlStateManager.enableTexture2D();

        fontrenderer.drawString(text, -strLenHalved, 0, 0x20FFFFFF);
        GlStateManager.enableDepth();

        GlStateManager.depthMask(true);*/
        fontrenderer.drawString(text, -strLenHalved, 0, 0xFF000000);

        //GlStateManager.disableAlpha(); // clean-up after drawString()
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private void drawPathSegment(MsPoint p1, MsPoint p2, int rgba, double partialTicks)
    {
        EntityPlayer player = this.mc.player;
        double p1X = p1.getX();
        double p1Y = p1.getY() + player.getEyeHeight(); // Draw the markers at the player's eye level, not feet
        double p1Z = p1.getZ();
        double p2X = p2.getX();
        double p2Y = p2.getY() + player.getEyeHeight(); // Draw the markers at the player's eye level, not feet
        double p2Z = p2.getZ();
        float r = (float)((rgba & 0xff000000) >>> 24) / 255.0f;
        float g = (float)((rgba & 0x00ff0000) >>> 16) / 255.0f;
        float b = (float)((rgba & 0x0000ff00) >>> 8) / 255.0f;
        float a = (float)(rgba & 0x000000ff) / 255.0f;

        // Player position
        double plX = player.lastTickPosX + ((player.posX - player.lastTickPosX) * partialTicks);
        double plY = player.lastTickPosY + ((player.posY - player.lastTickPosY) * partialTicks);
        double plZ = player.lastTickPosZ + ((player.posZ - player.lastTickPosZ) * partialTicks);

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager.translate(-plX, -plY, -plZ);
        GlStateManager.glLineWidth(2.0f);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(p1X, p1Y, p1Z).color(r, g, b, a).endVertex();
        buffer.pos(p2X, p2Y, p2Z).color(r, g, b, a).endVertex();
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private void drawPointCameraAngle(MsPoint pt, MsPoint pt2, int rgba1, int rgba2, double partialTicks)
    {
        EntityPlayer player = this.mc.player;
        // Path marker coordinates
        double ptX = pt.getX();
        double ptY = pt.getY() + player.getEyeHeight(); // Draw the markers at the player's eye level, not feet
        double ptZ = pt.getZ();

        float r1 = (float)((rgba1 & 0xff000000) >>> 24) / 255.0f;
        float g1 = (float)((rgba1 & 0x00ff0000) >>> 16) / 255.0f;
        float b1 = (float)((rgba1 & 0x0000ff00) >>> 8) / 255.0f;
        float a1 = (float)(rgba1 & 0x000000ff) / 255.0f;
        float r2 = (float)((rgba2 & 0xff000000) >>> 24) / 255.0f;
        float g2 = (float)((rgba2 & 0x00ff0000) >>> 16) / 255.0f;
        float b2 = (float)((rgba2 & 0x0000ff00) >>> 8) / 255.0f;
        float a2 = (float)(rgba2 & 0x000000ff) / 255.0f;

        // Player position
        double plX = player.lastTickPosX + ((player.posX - player.lastTickPosX) * partialTicks);
        double plY = player.lastTickPosY + ((player.posY - player.lastTickPosY) * partialTicks);
        double plZ = player.lastTickPosZ + ((player.posZ - player.lastTickPosZ) * partialTicks);

        double tgtX = 0.0;
        double tgtZ = 0.0;
        double tgtY = 0.0;
        // Camera angle indicator end coordinates
        // If we don't have a separate target point, draw a five meter long direction indicator
        if (pt == pt2)
        {
            double yaw = pt.getYaw() / (180.0 / Math.PI);
            double pitch = pt.getPitch() / (180.0 / Math.PI);
            double len = 5.0; // Camera angle indicator line length
            tgtX = ptX - (Math.sin(yaw) * len * Math.cos(pitch));
            tgtZ = ptZ + (Math.cos(yaw) * len * Math.cos(pitch));
            tgtY = ptY - (Math.sin(pitch) * len);
        }
        // If we have a separate target point, draw a line from the path marker to the target point
        else
        {
            tgtX = pt2.getX();
            tgtZ = pt2.getZ();
            tgtY = pt2.getY() + player.getEyeHeight(); // Draw the markers at the player's eye level, not feet;
        }

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager.translate(-plX, -plY, -plZ);
        GlStateManager.glLineWidth(2.0f);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(ptX, ptY, ptZ).color(r1, g1, b1, a1).endVertex();
        buffer.pos(tgtX, tgtY, tgtZ).color(r2, g2, b2, a2).endVertex();
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private void renderMotionMarkers(float partialTicks)
    {
        // Draw the path and/or points
        if (State.getHideGui() || this.mc.gameSettings.hideGUI)
        {
            return;
        }

        int pathLineColor        = 0x0022ffaa;
        int pathLineColorLast    = 0x00ff55aa;
        int pathCameraAngleColor = 0xff2222aa;

        int mode = Configs.getMotionMode();
        Motion motion = Motion.getMotion();

        // Circle and ellipse center and target markers
        if (mode == Constants.MOTION_MODE_CIRCLE || mode == Constants.MOTION_MODE_ELLIPSE)
        {
            MsPoint centerPoint;
            MsPoint targetPoint;
            if (mode == Constants.MOTION_MODE_CIRCLE)
            {
                centerPoint = motion.getCircleCenter();
                targetPoint = motion.getCircleTarget();
            }
            else // Constants.MOTION_MODE_ELLIPSE
            {
                centerPoint = motion.getEllipseCenter();
                targetPoint = motion.getEllipseTarget();
            }
            if (centerPoint != null)
            {
                this.drawPointMarker(centerPoint, -1, MarkerColor.BLUE, partialTicks);
            }
            if (targetPoint != null)
            {
                this.drawPointMarker(targetPoint, -1, MarkerColor.RED, partialTicks);
            }
        }
        // Path points, segments and camera looking angles
        else if (mode == Constants.MOTION_MODE_PATH_LINEAR || mode == Constants.MOTION_MODE_PATH_SMOOTH)
        {
            EntityPlayer player = this.mc.player;
            MsPath path = motion.getPath();
            MsPoint tgtpt = path.getTarget();

            // Do we have a global target point, or per-point camera angles?
            if (tgtpt != null)
            {
                this.drawPointMarker(tgtpt, -1, MarkerColor.ORANGE, partialTicks);
            }

            int len = path.getNumPoints();
            if (len > 0)
            {
                int nearest;
                nearest = motion.getPath().getNearestPointIndex(player.posX, player.posZ, player.posY);

                MsPoint pt;
                MsPoint ptl = null;
                for (int i = 0; i < len; i++)
                {
                    pt = path.getPoint(i);
                    // Draw the nearest marker in a different color to highlight it
                    if (i == nearest)
                    {
                        this.drawPointMarker(pt, i, MarkerColor.YELLOW, partialTicks);
                    }
                    else
                    {
                        this.drawPointMarker(pt, i, MarkerColor.CYAN, partialTicks);
                    }
                    // Do we have a global target point, or per-point camera angles?
                    if (tgtpt != null)
                    {
                        this.drawPointCameraAngle(pt, tgtpt, pathLineColor, pathCameraAngleColor, partialTicks);
                    }
                    else
                    {
                        this.drawPointCameraAngle(pt, pt, pathLineColor, pathCameraAngleColor, partialTicks);
                    }
                    // Draw line segments between points
                    if (i > 0)
                    {
                        this.drawPathSegment(ptl, pt, pathLineColor, partialTicks);
                    }
                    else // Draw a different colored line between the first and the last points
                    {
                        ptl = path.getPoint(path.getNumPoints() - 1);
                        this.drawPathSegment(ptl, pt, pathLineColorLast, partialTicks);
                    }
                    ptl = pt;
                }
            }
        }
    }
}
