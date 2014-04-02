package fi.dy.masa.minecraft.mods.multishot.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.config.MultishotConfigs;
import fi.dy.masa.minecraft.mods.multishot.handlers.MultishotKeys;
import fi.dy.masa.minecraft.mods.multishot.motion.MultishotMotion;
import fi.dy.masa.minecraft.mods.multishot.motion.MultishotMotion.MsPoint;
import fi.dy.masa.minecraft.mods.multishot.state.MultishotState;


@SideOnly(Side.CLIENT)
public class MultishotGui extends Gui
{
	private Minecraft mc = null;
	private MultishotConfigs multishotConfigs = null;
	private MultishotMotion multishotMotion = null;
	private static MultishotGui instance = null;
	private GuiMessage[] guiMessages = null;
	private int msgWr = 0;

	public MultishotGui(Minecraft mc, MultishotConfigs msCfg)
	{
		super();
		this.mc = mc;
		this.multishotConfigs = msCfg;
		instance = this;
		this.guiMessages = new GuiMessage[5];
	}

	public void setMotionInstance(MultishotMotion m)
	{
		this.multishotMotion = m;
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
		if (++this.msgWr >= 5)
		{
			this.msgWr = 0;
		}
	}

	public void addMessage(String msg)
	{
		addMessage(msg, 5000); // default to 5000 ms
	}

	public static MultishotGui getInstance()
	{
		return instance;
	}

	@ForgeSubscribe(priority = EventPriority.NORMAL)
	public void drawHud(RenderGameOverlayEvent event)
	{
		if (event.isCancelable() || event.type != ElementType.CROSSHAIRS)
		{
			return;
		}
		if (MultishotState.getHideGui() == false)
		{
			ScaledResolution scaledResolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
			this.mc.renderEngine.bindTexture("/mods/multishot/gui/hud.png");
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			GL11.glDisable(GL11.GL_LIGHTING);

			int scaledX = scaledResolution.getScaledWidth();
			int scaledY = scaledResolution.getScaledHeight();
			int offsetX = this.multishotConfigs.getGuiOffsetX();
			int offsetY = this.multishotConfigs.getGuiOffsetY();
			int x = 0;
			int y = 0;
			int msgX = 0;
			int msgY = 0;
			float msgScale = 0.5f;

			// 0 = Top Right, 1 = Bottom Right, 2 = Bottom Left, 3 = Top Left
			if (this.multishotConfigs.getGuiPosition() == 0) // Top Right
			{
				x = scaledX + offsetX - 48;
				y = 0 + offsetY;
				msgX = (int)((float)(scaledX + offsetX - 215) / msgScale);
				msgY = (int)((float)(offsetY + 1) / msgScale);
			}
			else if (this.multishotConfigs.getGuiPosition() == 1) // Bottom Right
			{
				x = scaledX + offsetX - 48;
				y = scaledY + offsetY - 16;
				msgX = (int)((float)(scaledX + offsetX - 165) / msgScale);
				msgY = (int)((float)(scaledY + offsetY - 43) / msgScale);
			}
			else if (this.multishotConfigs.getGuiPosition() == 2) // Bottom Left
			{
				x = offsetX + 0;
				y = scaledY + offsetY - 16;
				msgX = (int)((float)(offsetX + 1) / msgScale);
				msgY = (int)((float)(scaledY + offsetY - 43) / msgScale);
			}
			else if (this.multishotConfigs.getGuiPosition() == 3) // Top Left
			{
				x = offsetX + 0;
				y = offsetY + 0;
				msgX = (int)((float)(offsetX + 50) / msgScale);
				msgY = (int)((float)(offsetY + 1) / msgScale);
			}

			if (MultishotState.getControlsLocked() == true)
			{
				this.drawTexturedModalRect(x + 0, y, 0, 0, 16, 16); // Controls locked
			}
			else
			{
				this.drawTexturedModalRect(x + 0, y, 0, 16, 16, 16); // Controls not locked
			}
			if (MultishotState.getMotion() == true)
			{
				this.drawTexturedModalRect(x + 16, y, 16, 0, 16, 16); // Motion ON
			}
			else
			{
				this.drawTexturedModalRect(x + 16, y, 16, 16, 16, 16); // Motion OFF
			}
			if (MultishotState.getRecording() == true)
			{
				if (MultishotState.getPaused() == true)
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
			GL11.glPushMatrix();
			GL11.glScalef(msgScale, msgScale, msgScale);
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
						this.mc.ingameGUI.drawString(this.mc.fontRenderer, s, msgX, msgY + yoff, 0xffffffff);
						yoff += 8;
					}
				}
			}
			// FIXME debug
			//this.mc.ingameGUI.drawString(this.mc.fontRenderer, String.format("w: %d h: %d", this.mc.displayWidth, this.mc.displayHeight), 10, 10, 0xffffffff);
			//this.mc.ingameGUI.drawString(this.mc.fontRenderer, String.format("scaled w: %d h: %d", scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight()), 10, 20, 0xffffffff);
			GL11.glPopMatrix();
		}
	}

	private void drawPointMarker(MsPoint p, int rgba, double partialTicks)
	{
		double pX = p.getX();
		double pY = p.getY();
		double pZ = p.getZ();
		float r = (float)((rgba & 0xff000000) >>> 24) / 255.0f;
		float g = (float)((rgba & 0x00ff0000) >>> 16) / 255.0f;
		float b = (float)((rgba & 0x0000ff00) >>> 8) / 255.0f;
		float a = (float)(rgba & 0x000000ff) / 255.0f;

		EntityClientPlayerMP player = this.mc.thePlayer;
		Tessellator tessellator = Tessellator.instance;
		// Player position
		double plX = player.lastTickPosX + ((player.posX - player.lastTickPosX) * partialTicks);
		double plY = player.lastTickPosY + ((player.posY - player.lastTickPosY) * partialTicks);
		double plZ = player.lastTickPosZ + ((player.posZ - player.lastTickPosZ) * partialTicks);

		double markerR = 0.2;
		// Marker corner positions
		double angleh = 90;
		double xDiff = pX - plX;
		if (xDiff != 0.0) {
			angleh = Math.atan((pZ - plZ) / (xDiff));
		}
		double ptX1 = pX + (Math.sin(angleh) * markerR);
		double ptX2 = pX - (Math.sin(angleh) * markerR);
		double ptZ1 = pZ - (Math.cos(angleh) * markerR);
		double ptZ2 = pZ + (Math.cos(angleh) * markerR);
		double ptXC = (ptX1 + ptX2) / 2.0;
		double ptZC = (ptZ1 + ptZ2) / 2.0;
/*
		double anglev = 90;
		double yDiff = pY - plY;
		if (yDiff != 0.0) {
			anglev = Math.atan((pX - plX) / (yDiff));
		}
		double ptY1 = pY - (Math.cos(anglev) * markerR);
		double ptY2 = pY + (Math.cos(anglev) * markerR);
		double ptYC = (ptY1 + ptY2) / 2.0;
*/
		double ptYC = pY;
		double ptY1 = pY + markerR;
		double ptY2 = pY - markerR;

		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		//GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		GL11.glTranslated(-plX, -plY, -plZ);

		tessellator.startDrawing(GL11.GL_QUADS);
		tessellator.setColorRGBA_F(r, g, b, a);
		tessellator.addVertex(ptX1, ptYC, ptZ1);
		tessellator.addVertex(ptXC, ptY1, ptZC);
		tessellator.addVertex(ptX2, ptYC, ptZ2);
		tessellator.addVertex(ptXC, ptY2, ptZC);
		tessellator.draw();

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	@ForgeSubscribe(priority = EventPriority.NORMAL)
	public void drawPathPoints(RenderWorldLastEvent event)
	{
		// Draw the path and/or points
		if (MultishotKeys.isCtrlKeyDown() == true)
		{
			int centerColor = 0xff0000aa;
			int targetColor = 0x00ff00aa;
			int pathMarkerColor = 0x0000ffaa;

			if (this.multishotConfigs.getMotionMode() == 1 || this.multishotConfigs.getMotionMode() == 2) // 1 = Circle, 2 = Ellipse
			{
				MsPoint centerPoint;
				MsPoint targetPoint;
				if (this.multishotConfigs.getMotionMode() == 1) {
					centerPoint = this.multishotMotion.getCircleCenter();
					targetPoint = this.multishotMotion.getCircleTarget();
				}
				else {
					centerPoint = this.multishotMotion.getEllipseCenter();
					targetPoint = this.multishotMotion.getEllipseTarget();
				}
				if (centerPoint != null) {
					this.drawPointMarker(centerPoint, centerColor, (double)event.partialTicks);
				}
				if (targetPoint != null) {
					this.drawPointMarker(targetPoint, targetColor, (double)event.partialTicks);
				}
			}
			else if (this.multishotConfigs.getMotionMode() == 3) { // 3 = Path
				MsPoint[] path = this.multishotMotion.getPath();
				int len;
				if (path != null && path.length > 0) {
					len = path.length;
					for (int i = 0; i < len; i++)
					{
						this.drawPointMarker(path[i], pathMarkerColor, (double)event.partialTicks);
					}
				}
			}
		}
	}
/*
GL11.glColor3ub((byte)255, (byte)255, (byte)0);
GL11.glBegin(GL11.GL_LINES);
GL11.glVertex3d(0, 6, 0);
GL11.glVertex3d(1, 6, 1);
//GL11.glEnd();
GL11.glVertex3d(0, 6, 1);
GL11.glVertex3d(1, 6, 0);
GL11.glEnd();
*/
/*
GL11.glColor3ub((byte)255, (byte)55, (byte)0);
GL11.glBegin(GL11.GL_QUADS);
GL11.glVertex3d(0, 6, 0);
GL11.glVertex3d(0.5, 6.5, 0);
GL11.glVertex3d(1, 6, 0);
GL11.glVertex3d(0.5, 5.5, 0);
GL11.glEnd();

GL11.glColor3ub((byte)55, (byte)255, (byte)0);
GL11.glBegin(GL11.GL_QUADS);
GL11.glVertex3d(0.1, 6, 0);
GL11.glVertex3d(0.5, 6.4, 0);
GL11.glVertex3d(0.9, 6, 0);
GL11.glVertex3d(0.5, 5.6, 0);
GL11.glEnd();
*/
}
