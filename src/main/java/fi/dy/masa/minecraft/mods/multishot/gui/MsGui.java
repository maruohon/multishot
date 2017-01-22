package fi.dy.masa.minecraft.mods.multishot.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import fi.dy.masa.minecraft.mods.multishot.config.Configs;
import fi.dy.masa.minecraft.mods.multishot.motion.Motion;
import fi.dy.masa.minecraft.mods.multishot.motion.Motion.MsPath;
import fi.dy.masa.minecraft.mods.multishot.motion.Motion.MsPoint;
import fi.dy.masa.minecraft.mods.multishot.reference.Constants;
import fi.dy.masa.minecraft.mods.multishot.reference.Reference;
import fi.dy.masa.minecraft.mods.multishot.state.State;
import fi.dy.masa.minecraft.mods.multishot.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
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

    public void addMessage(String msg, int lifetime)
    {
        this.guiMessages[this.msgWr] = new GuiMessage(msg, System.currentTimeMillis(), lifetime);
        if (++this.msgWr >= this.guiMessages.length)
        {
            this.msgWr = 0;
        }
    }

    public void addMessage(String msg)
    {
        addMessage(msg, 10000); // default to 10000ms = 10s
    }

    @SubscribeEvent
    public void drawHud(RenderGameOverlayEvent.Post event)
    {
        if (State.getHideGui() == true || event.getType() != ElementType.ALL)
        {
            return;
        }

        ScaledResolution scaledResolution = new ScaledResolution(this.mc);

        int scaledX = scaledResolution.getScaledWidth();
        int scaledY = scaledResolution.getScaledHeight();
        int offsetX = Configs.getConfig().getGuiOffsetX();
        int offsetY = Configs.getConfig().getGuiOffsetY();
        int x = 0;
        int y = 0;
        int msgX = 0;
        int msgY = 0;
        float msgScale = 0.5f;

        Configs msCfg = Configs.getConfig();
        // 0 = Top Right, 1 = Bottom Right, 2 = Bottom Left, 3 = Top Left
        if (msCfg.getGuiPosition() == 0) // Top Right
        {
            x = scaledX + offsetX - 48;
            y = 0 + offsetY;
            msgX = (int)((float)(scaledX + offsetX - 215) / msgScale);
            msgY = (int)((float)(offsetY + 1) / msgScale);
        }
        else if (msCfg.getGuiPosition() == 1) // Bottom Right
        {
            x = scaledX + offsetX - 48;
            y = scaledY + offsetY - 16;
            msgX = (int)((float)(scaledX + offsetX - 165) / msgScale);
            msgY = (int)((float)(scaledY + offsetY - 43) / msgScale);
        }
        else if (msCfg.getGuiPosition() == 2) // Bottom Left
        {
            x = offsetX + 0;
            y = scaledY + offsetY - 16;
            msgX = (int)((float)(offsetX + 1) / msgScale);
            msgY = (int)((float)(scaledY + offsetY - 43) / msgScale);
        }
        else if (msCfg.getGuiPosition() == 3) // Top Left
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

        // Draw the message area
        GlStateManager.pushMatrix();
        GlStateManager.scale(msgScale, msgScale, msgScale);

        for(int i = 0, j = this.msgWr, yoff = 0; i < 5; i++, j++)
        {
            if (j > 4)
            {
                j = 0;
            }
            if (this.guiMessages[j] != null)
            {
                String s = this.guiMessages[j].getMsg();
                boolean isDead = this.guiMessages[j].getIsDead();
                if (isDead == false)
                {
                    this.mc.ingameGUI.drawString(this.mc.fontRendererObj, s, msgX, msgY + yoff, 0xffffffff);
                    yoff += 8;
                }
            }
        }

        GlStateManager.popMatrix();
    }

    private void drawPointMarker(MsPoint p, int rgba, float partialTicks)
    {
        double pX = p.getX();
        double pY = p.getY() + this.mc.player.getEyeHeight(); // Draw the markers at the player's eye level, not feet
        double pZ = p.getZ();
        float r = (float)((rgba & 0xff000000) >>> 24) / 255.0f;
        float g = (float)((rgba & 0x00ff0000) >>> 16) / 255.0f;
        float b = (float)((rgba & 0x0000ff00) >>> 8) / 255.0f;
        float a = (float)(rgba & 0x000000ff) / 255.0f;

        EntityPlayer player = this.mc.player;
        // Player position
        double plX = player.lastTickPosX + ((player.posX - player.lastTickPosX) * partialTicks);
        double plY = player.lastTickPosY + ((player.posY - player.lastTickPosY) * partialTicks);
        double plZ = player.lastTickPosZ + ((player.posZ - player.lastTickPosZ) * partialTicks);

        double markerR = 0.2; // marker size (radius)

        double angleh = Math.PI / 2.0;
        double zDiff = pZ - plZ;
        if (zDiff != 0.0)
        {
            // the angle in which the player sees the marker, in relation to the positive z-axis
            angleh = Math.atan2(pX - plX, zDiff);
        }

        // Marker left and right corner positions
        double ptX1 = pX + (Math.cos(angleh) * markerR);
        double ptX2 = pX - (Math.cos(angleh) * markerR);
        double ptZ1 = pZ - (Math.sin(angleh) * markerR);
        double ptZ2 = pZ + (Math.sin(angleh) * markerR);

        double anglev = Math.PI / 2.0;
        double hDist = MathHelper.distance2D(plX, plZ, pX, pZ); // horizontal distance from the player to the marker
        if (hDist != 0.0)
        {
            // the angle in which the player sees the marker, in relation to the xz-plane
            anglev = Math.atan((plY - p.getY()) / hDist);
        }

        double ptTopY = pY + (Math.cos(anglev) * markerR);
        double ptTopX = pX + (Math.sin(anglev) * markerR * Math.sin(angleh));
        double ptTopZ = pZ + (Math.sin(anglev) * markerR * Math.cos(angleh));
        double ptBottomY = pY - (Math.cos(anglev) * markerR);
        double ptBottomX = pX - (Math.sin(anglev) * markerR * Math.sin(angleh));
        double ptBottomZ = pZ - (Math.sin(anglev) * markerR * Math.cos(angleh));

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GlStateManager.translate(-plX, -plY, -plZ);
        GlStateManager.glLineWidth(2.0f);

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(ptX1, pY, ptZ1).color(r, g, b, a).endVertex(); // "left" corner
        buffer.pos(ptBottomX, ptBottomY, ptBottomZ).color(r, g, b, a).endVertex(); // bottom corner
        buffer.pos(ptX2, pY, ptZ2).color(r, g, b, a).endVertex(); // "right" corner
        buffer.pos(ptTopX, ptTopY, ptTopZ).color(r, g, b, a).endVertex(); // top corner
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private void drawPathSegment(MsPoint p1, MsPoint p2, int rgba, double partialTicks)
    {

        double p1X = p1.getX();
        double p1Y = p1.getY() + this.mc.player.getEyeHeight(); // Draw the markers at the player's eye level, not feet
        double p1Z = p1.getZ();
        double p2X = p2.getX();
        double p2Y = p2.getY() + this.mc.player.getEyeHeight(); // Draw the markers at the player's eye level, not feet
        double p2Z = p2.getZ();
        float r = (float)((rgba & 0xff000000) >>> 24) / 255.0f;
        float g = (float)((rgba & 0x00ff0000) >>> 16) / 255.0f;
        float b = (float)((rgba & 0x0000ff00) >>> 8) / 255.0f;
        float a = (float)(rgba & 0x000000ff) / 255.0f;

        EntityPlayer player = this.mc.player;
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
        VertexBuffer buffer = tessellator.getBuffer();

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
        // Path marker coordinates
        double ptX = pt.getX();
        double ptY = pt.getY() + this.mc.player.getEyeHeight(); // Draw the markers at the player's eye level, not feet
        double ptZ = pt.getZ();

        float r1 = (float)((rgba1 & 0xff000000) >>> 24) / 255.0f;
        float g1 = (float)((rgba1 & 0x00ff0000) >>> 16) / 255.0f;
        float b1 = (float)((rgba1 & 0x0000ff00) >>> 8) / 255.0f;
        float a1 = (float)(rgba1 & 0x000000ff) / 255.0f;
        float r2 = (float)((rgba2 & 0xff000000) >>> 24) / 255.0f;
        float g2 = (float)((rgba2 & 0x00ff0000) >>> 16) / 255.0f;
        float b2 = (float)((rgba2 & 0x0000ff00) >>> 8) / 255.0f;
        float a2 = (float)(rgba2 & 0x000000ff) / 255.0f;

        EntityPlayer player = this.mc.player;
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
            tgtY = pt2.getY() + this.mc.player.getEyeHeight(); // Draw the markers at the player's eye level, not feet;
        }

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager.translate(-plX, -plY, -plZ);
        GlStateManager.glLineWidth(2.0f);

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(ptX, ptY, ptZ).color(r1, g1, b1, a1).endVertex();
        buffer.pos(tgtX, tgtY, tgtZ).color(r2, g2, b2, a2).endVertex();
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public void drawMotionMarkers(RenderWorldLastEvent event)
    {
        // Draw the path and/or points
        if (State.getHideGui() == true || this.mc.gameSettings.hideGUI == true)
        {
            return;
        }

        int centerColor = 0x0000ffaa;
        int targetColor = 0xff0000aa;
        int pathMarkerColor = 0x0000ffaa;
        int pathMarkerColorHL = 0xffff00aa;
        int pathLineColor = 0x0022ffaa;
        int pathLineColorLast = 0x00ff55aa;
        int pathCameraAngleColor = 0xff2222aa;

        final float partialTicks = event.getPartialTicks();
        int mode = Configs.getConfig().getMotionMode();
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
                this.drawPointMarker(centerPoint, centerColor, partialTicks);
            }
            if (targetPoint != null)
            {
                this.drawPointMarker(targetPoint, targetColor, partialTicks);
            }
        }
        // Path points, segments and camera looking angles
        else if (mode == Constants.MOTION_MODE_PATH_LINEAR || mode == Constants.MOTION_MODE_PATH_SMOOTH)
        {
            EntityPlayer player = this.mc.player;
            MsPath path = motion.getPath();
            MsPoint tgtpt = motion.getPath().getTarget();

            // Do we have a global target point, or per-point camera angles?
            if (tgtpt != null)
            {
                this.drawPointMarker(tgtpt, targetColor, partialTicks);
            }

            int len = path.getNumPoints();
            if (path != null && len > 0)
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
                        this.drawPointMarker(pt, pathMarkerColorHL, partialTicks);
                    }
                    else
                    {
                        this.drawPointMarker(pt, pathMarkerColor, partialTicks);
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
