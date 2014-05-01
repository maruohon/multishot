package fi.dy.masa.minecraft.mods.multishot.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.libs.MsMathHelper;
import fi.dy.masa.minecraft.mods.multishot.motion.MsMotion.MsPoint;
import fi.dy.masa.minecraft.mods.multishot.reference.MsConstants;
import fi.dy.masa.minecraft.mods.multishot.reference.MsTextures;
import fi.dy.masa.minecraft.mods.multishot.state.MsClassReference;
import fi.dy.masa.minecraft.mods.multishot.state.MsState;


@SideOnly(Side.CLIENT)
public class MsGui extends Gui
{
	private Minecraft mc = null;
	private static MsGui instance = null;
	private GuiMessage[] guiMessages = null;
	private int msgWr = 0;

	public MsGui(Minecraft mc)
	{
		super();
		this.mc = mc;
		instance = this;
		this.guiMessages = new GuiMessage[5];
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

	public static MsGui getInstance()
	{
		return instance;
	}

	@SubscribeEvent
	public void drawHud(RenderGameOverlayEvent event)
	{
		if (event.isCancelable() || event.type != ElementType.CROSSHAIRS || MsState.getHideGui() == true)
		{
			return;
		}

		ScaledResolution scaledResolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
		this.mc.getTextureManager().bindTexture(MsTextures.GUI_HUD);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glDisable(GL11.GL_LIGHTING);

		int scaledX = scaledResolution.getScaledWidth();
		int scaledY = scaledResolution.getScaledHeight();
		int offsetX = MsClassReference.getMsConfigs().getGuiOffsetX();
		int offsetY = MsClassReference.getMsConfigs().getGuiOffsetY();
		int x = 0;
		int y = 0;
		int msgX = 0;
		int msgY = 0;
		float msgScale = 0.5f;

		// 0 = Top Right, 1 = Bottom Right, 2 = Bottom Left, 3 = Top Left
		if (MsClassReference.getMsConfigs().getGuiPosition() == 0) // Top Right
		{
			x = scaledX + offsetX - 48;
			y = 0 + offsetY;
			msgX = (int)((float)(scaledX + offsetX - 215) / msgScale);
			msgY = (int)((float)(offsetY + 1) / msgScale);
		}
		else if (MsClassReference.getMsConfigs().getGuiPosition() == 1) // Bottom Right
		{
			x = scaledX + offsetX - 48;
			y = scaledY + offsetY - 16;
			msgX = (int)((float)(scaledX + offsetX - 165) / msgScale);
			msgY = (int)((float)(scaledY + offsetY - 43) / msgScale);
		}
		else if (MsClassReference.getMsConfigs().getGuiPosition() == 2) // Bottom Left
		{
			x = offsetX + 0;
			y = scaledY + offsetY - 16;
			msgX = (int)((float)(offsetX + 1) / msgScale);
			msgY = (int)((float)(scaledY + offsetY - 43) / msgScale);
		}
		else if (MsClassReference.getMsConfigs().getGuiPosition() == 3) // Top Left
		{
			x = offsetX + 0;
			y = offsetY + 0;
			msgX = (int)((float)(offsetX + 50) / msgScale);
			msgY = (int)((float)(offsetY + 1) / msgScale);
		}

		// We now always force lock the controls in motion mode
		if (MsState.getControlsLocked() == true || MsState.getMotion() == true)
		{
			this.drawTexturedModalRect(x + 0, y, 0, 0, 16, 16); // Controls locked
		}
		else
		{
			this.drawTexturedModalRect(x + 0, y, 0, 16, 16, 16); // Controls not locked
		}
		if (MsState.getMotion() == true)
		{
			this.drawTexturedModalRect(x + 16, y, 16, 0, 16, 16); // Motion ON
		}
		else
		{
			this.drawTexturedModalRect(x + 16, y, 16, 16, 16, 16); // Motion OFF
		}
		if (MsState.getRecording() == true)
		{
			if (MsState.getPaused() == true)
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
		GL11.glPopMatrix();
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
		double hDist = MsMathHelper.distance2D(plX, plZ, pX, pZ); // horizontal distance from the player to the marker
		if (hDist != 0.0)
		{
			// the angle in which the player sees the marker, in relation to the xz-plane
			anglev = Math.atan((plY - pY) / hDist);
		}

		double ptTopY = pY + (Math.cos(anglev) * markerR);
		double ptTopX = pX + (Math.sin(anglev) * markerR * Math.sin(angleh));
		double ptTopZ = pZ + (Math.sin(anglev) * markerR * Math.cos(angleh));
		double ptBottomY = pY - (Math.cos(anglev) * markerR);
		double ptBottomX = pX - (Math.sin(anglev) * markerR * Math.sin(angleh));
		double ptBottomZ = pZ - (Math.sin(anglev) * markerR * Math.cos(angleh));

		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		//GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		GL11.glTranslated(-plX, -plY, -plZ);

		GL11.glColor4f(r, g, b, a);
		GL11.glLineWidth(2.0f);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(ptX1, pY, ptZ1); // "left" corner
		GL11.glVertex3d(ptTopX, ptTopY, ptTopZ); // top corner
		GL11.glVertex3d(ptX2, pY, ptZ2); // "right" corner
		GL11.glVertex3d(ptBottomX, ptBottomY, ptBottomZ); // bottom corner
		GL11.glEnd();

		//GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
	}

	private void drawPathSegment(MsPoint p1, MsPoint p2, int rgba, double partialTicks)
	{

		double p1X = p1.getX();
		double p1Y = p1.getY();
		double p1Z = p1.getZ();
		double p2X = p2.getX();
		double p2Y = p2.getY();
		double p2Z = p2.getZ();
		float r = (float)((rgba & 0xff000000) >>> 24) / 255.0f;
		float g = (float)((rgba & 0x00ff0000) >>> 16) / 255.0f;
		float b = (float)((rgba & 0x0000ff00) >>> 8) / 255.0f;
		float a = (float)(rgba & 0x000000ff) / 255.0f;

		EntityClientPlayerMP player = this.mc.thePlayer;
		// Player position
		double plX = player.lastTickPosX + ((player.posX - player.lastTickPosX) * partialTicks);
		double plY = player.lastTickPosY + ((player.posY - player.lastTickPosY) * partialTicks);
		double plZ = player.lastTickPosZ + ((player.posZ - player.lastTickPosZ) * partialTicks);

		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		//GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		//GL11.glDisable(GL11.GL_CULL_FACE);
		//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		GL11.glTranslated(-plX, -plY, -plZ);

		GL11.glColor4f(r, g, b, a);
		GL11.glLineWidth(2.0f);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(p1X, p1Y, p1Z);
		GL11.glVertex3d(p2X, p2Y, p2Z);
		GL11.glEnd();

		//GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
	}

	private void drawPointCameraAngle(MsPoint pt, MsPoint pt2, int rgba1, int rgba2, double partialTicks)
	{
		// Path marker coordinates
		double ptX = pt.getX();
		double ptY = pt.getY();
		double ptZ = pt.getZ();

		float r1 = (float)((rgba1 & 0xff000000) >>> 24) / 255.0f;
		float g1 = (float)((rgba1 & 0x00ff0000) >>> 16) / 255.0f;
		float b1 = (float)((rgba1 & 0x0000ff00) >>> 8) / 255.0f;
		float a1 = (float)(rgba1 & 0x000000ff) / 255.0f;
		float r2 = (float)((rgba2 & 0xff000000) >>> 24) / 255.0f;
		float g2 = (float)((rgba2 & 0x00ff0000) >>> 16) / 255.0f;
		float b2 = (float)((rgba2 & 0x0000ff00) >>> 8) / 255.0f;
		float a2 = (float)(rgba2 & 0x000000ff) / 255.0f;

		EntityClientPlayerMP player = this.mc.thePlayer;
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
			tgtY = pt2.getY();
		}

		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		//GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		//GL11.glDisable(GL11.GL_CULL_FACE);
		//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		GL11.glTranslated(-plX, -plY, -plZ);

		GL11.glLineWidth(2.0f);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4f(r1, g1, b1, a1);
		GL11.glVertex3d(ptX, ptY, ptZ);
		GL11.glColor4f(r2, g2, b2, a2);
		GL11.glVertex3d(tgtX, tgtY, tgtZ);
		GL11.glEnd();

		//GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
	}

	@SubscribeEvent
	public void updatePlayerRotation(RenderWorldLastEvent event)
	{
		float yaw = MsClassReference.getMotion().prevYaw + (MsClassReference.getMotion().yawIncrement * event.partialTicks);
		float pitch = MsClassReference.getMotion().prevPitch + (MsClassReference.getMotion().pitchIncrement * event.partialTicks);
		//if (yaw > 180.0f) { yaw -= 360.0f; }
		//else if (yaw < -180.0f) { yaw += 360.0f; }

		// FIXME debug stuff:
		// Note: the text will only get rendered when using the RenderGameOverlayEvent, not using the RenderWorldLastEvent
		// Then again, we can't use RenderGameOverlayEvent to actually do the rotation stuff, because that event won't
		// happen when the HUD is hidden (with F1).
/*
		if (MultishotScreenBase.isCtrlKeyDown())
		{
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			String s1 = String.format("ms prevYaw: %f", MsClassReference.getMotion().prevYaw);
			String s2 = String.format("mc prevYaw: %f", this.mc.thePlayer.prevRotationYaw);
			String s3 = String.format("rotationYaw: %f", this.mc.thePlayer.rotationYaw);
			String s4 = String.format("yawInc: %f", MsClassReference.getMotion().yawIncrement);
			String s5 = String.format("yaw: %f", yaw);
			String s6 = String.format("targetAtan2: %f", MsClassReference.getMotion().targetAtan2);
			String s7 = String.format("targetAtan2Deg: %f", MsClassReference.getMotion().targetAtan2Deg);
			this.mc.fontRenderer.drawStringWithShadow(s1, 5, 20, 0xffffffff);
			this.mc.fontRenderer.drawStringWithShadow(s2, 5, 30, 0xffffffff);
			this.mc.fontRenderer.drawStringWithShadow(s3, 5, 40, 0xffffffff);
			this.mc.fontRenderer.drawStringWithShadow(s4, 5, 50, 0xffffffff);
			this.mc.fontRenderer.drawStringWithShadow(s5, 5, 60, 0xffffffff);
			this.mc.fontRenderer.drawStringWithShadow(s6, 5, 70, 0xffffffff);
			this.mc.fontRenderer.drawStringWithShadow(s7, 5, 80, 0xffffffff);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();
			//System.out.printf("ms prevYaw: %f mc prevYaw: %f yawInc: %f yaw: %f\n", MsClassReference.getMotion().prevYaw, this.mc.thePlayer.prevRotationYaw, MsClassReference.getMotion().yawIncrement, yaw);
		}
*/
		// Update the player rotation and pitch here in smaller steps, so that the camera doesn't jitter so terribly
		if (MsState.getMotion() == true)
		{
			EntityClientPlayerMP p = this.mc.thePlayer;
			int mode = MsClassReference.getMsConfigs().getMotionMode();
			// Linear motion mode
			if (mode == MsConstants.MOTION_MODE_LINEAR && (MsClassReference.getMsConfigs().getRotationYaw() != 0 || MsClassReference.getMsConfigs().getRotationPitch() != 0))
			{
				//p.setPositionAndRotation(p.posX, p.posY, p.posZ, yaw, pitch);
				p.rotationYaw = yaw;
				p.prevRotationYaw = yaw;
				p.rotationPitch = pitch;
				p.prevRotationPitch = pitch;
			}
			// Circular motion mode
			else if (mode == MsConstants.MOTION_MODE_CIRCLE && MsClassReference.getMotion().getUseTarget() == true)
			{
				//p.setPositionAndRotation(p.posX, p.posY, p.posZ, yaw, pitch);
				p.rotationYaw = yaw;
				p.prevRotationYaw = yaw;
				p.rotationPitch = pitch;
				p.prevRotationPitch = pitch;
			}
		}
	}

	@SubscribeEvent
	public void drawMotionMarkers(RenderWorldLastEvent event)
	{
		// Draw the path and/or points
		if (MsState.getHideGui() == true || this.mc.gameSettings.hideGUI == true)
		{
			return;
		}

		int centerColor = 0x0000ffaa;
		int targetColor = 0xff0000aa;
		int pathMarkerColor = 0x0000ffaa;
		int pathMarkerColorHL = 0xffff00aa;
		int pathLineColor = 0x0022ffaa;
		int pathCameraAngleColor = 0xff2222aa;

		int mode = MsClassReference.getMsConfigs().getMotionMode();
		// Circle and ellipse center and target markers
		if (mode == MsConstants.MOTION_MODE_CIRCLE || mode == MsConstants.MOTION_MODE_ELLIPSE)
		{
			MsPoint centerPoint;
			MsPoint targetPoint;
			if (mode == MsConstants.MOTION_MODE_CIRCLE)
			{
				centerPoint = MsClassReference.getMotion().getCircleCenter();
				targetPoint = MsClassReference.getMotion().getCircleTarget();
			}
			else // Constants.MOTION_MODE_ELLIPSE
			{
				centerPoint = MsClassReference.getMotion().getEllipseCenter();
				targetPoint = MsClassReference.getMotion().getEllipseTarget();
			}
			if (centerPoint != null)
			{
				this.drawPointMarker(centerPoint, centerColor, (double)event.partialTicks);
			}
			if (targetPoint != null)
			{
				this.drawPointMarker(targetPoint, targetColor, (double)event.partialTicks);
			}
		}
		// Path points, segments and camera looking angles
		else if (mode == MsConstants.MOTION_MODE_PATH_LINEAR || mode == MsConstants.MOTION_MODE_PATH_SMOOTH)
		{
			MsPoint[] path = MsClassReference.getMotion().getPath();
			EntityClientPlayerMP p = this.mc.thePlayer;
			int len;
			int nearest;
			if (path != null && path.length > 0)
			{
				len = path.length;
				nearest = MsClassReference.getMotion().getNearestPathPointIndex(p.posX, p.posZ, p.posY);
				MsPoint tgtpt = MsClassReference.getMotion().getPathTarget();
				// Do we have a global target point, or per-point camera angles?
				if (tgtpt != null)
				{
					this.drawPointMarker(tgtpt, targetColor, (double)event.partialTicks);
				}
				for (int i = 0; i < len; i++)
				{
					// Draw the nearest marker in a different color to highlight it
					if (i == nearest)
					{
						this.drawPointMarker(path[i], pathMarkerColorHL, (double)event.partialTicks);
					}
					else
					{
						this.drawPointMarker(path[i], pathMarkerColor, (double)event.partialTicks);
					}
					// Do we have a global target point, or per-point camera angles?
					if (tgtpt != null)
					{
						this.drawPointCameraAngle(path[i], tgtpt, pathLineColor, pathCameraAngleColor, (double)event.partialTicks);
					}
					else
					{
						this.drawPointCameraAngle(path[i], path[i], pathLineColor, pathCameraAngleColor, (double)event.partialTicks);
					}
					// Draw line segments between points
					if (i > 0)
					{
						this.drawPathSegment(path[i - 1], path[i], pathLineColor, (double)event.partialTicks);
					}
				}
			}
		}
	}
}
