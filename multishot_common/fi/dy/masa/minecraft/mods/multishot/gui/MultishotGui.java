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
import fi.dy.masa.minecraft.mods.multishot.state.MultishotState;


@SideOnly(Side.CLIENT)
public class MultishotGui extends Gui
{
	private Minecraft mc = null;
	private static MultishotGui instance = null;
	private GuiMessage[] guiMessages = null;
	private int msgWr = 0;

	public MultishotGui(Minecraft mc)
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

			int x = scaledResolution.getScaledWidth() - 48;
			if (MultishotState.getRecording() == true)
			{
				if (MultishotState.getPaused() == true)
				{
					this.drawTexturedModalRect(x + 32, 0, 32, 16, 16, 16); // Recording and paused
				}
				else
				{
					this.drawTexturedModalRect(x + 32, 0, 32, 0, 16, 16); // Recording, not paused
				}
			}
			else
			{
				this.drawTexturedModalRect(x + 32, 0, 32, 32, 16, 16); // Stopped
			}
			if (MultishotState.getMotion() == true)
			{
				this.drawTexturedModalRect(x + 16, 0, 16, 0, 16, 16); // Motion ON
			}
			else
			{
				this.drawTexturedModalRect(x + 16, 0, 16, 16, 16, 16); // Motion OFF
			}
			if (MultishotState.getControlsLocked() == true)
			{
				this.drawTexturedModalRect(x + 0, 0, 0, 0, 16, 16); // Controls locked
			}
			else
			{
				this.drawTexturedModalRect(x + 0, 0, 0, 16, 16, 16); // Controls not locked
			}
			GL11.glPushMatrix();
			float m = 0.5f;
			GL11.glScalef(m, m, m);
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
						int msgx = (int)((float)scaledResolution.getScaledWidth() / m);
						this.mc.ingameGUI.drawString(this.mc.fontRenderer, s, msgx - 490, 2 + yoff, 0xffffffff);
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
