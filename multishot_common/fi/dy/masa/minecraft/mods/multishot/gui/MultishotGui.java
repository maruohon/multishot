package fi.dy.masa.minecraft.mods.multishot.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.config.MultishotConfigs;
import fi.dy.masa.minecraft.mods.multishot.state.MultishotState;


@SideOnly(Side.CLIENT)
public class MultishotGui extends Gui
{
	private Minecraft mc = null;
	private MultishotConfigs multishotConfigs = null;
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

	public void addMessage(String msg)
	{
		this.guiMessages[this.msgWr] = new GuiMessage(msg, System.currentTimeMillis(), 5000);
		if (++this.msgWr >= 5)
		{
			this.msgWr = 0;
		}
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
}
