package fi.dy.masa.minecraft.mods.multishot.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import org.lwjgl.opengl.GL11;
import fi.dy.masa.minecraft.mods.multishot.state.MultishotState;

public class MultishotGui extends Gui
{
	private Minecraft mc = null;
	private MultishotGui instance = null;

	public MultishotGui(Minecraft mc)
	{
		super();
		this.mc = mc;
		this.instance = this;
	}

	public MultishotGui getInstance()
	{
		return this.instance;
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
			this.mc.renderEngine.bindTexture("/mods/multishot/gui/hud.png");
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			GL11.glDisable(GL11.GL_LIGHTING);

			int x = (this.mc.displayWidth / 2) - 48;
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
/*
				// Random testing:
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glColor4f(1.0f, 0.0f, 0.0f, 0.5f);
				GL11.glLineWidth(2.0f);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				//GL11.glDepthMask(false);
				Tessellator t = Tessellator.instance;
				t.startDrawing(1);
				//t.setColorOpaque_F(0.5f, 0.5f, 0.5f);
				t.addVertex(0.0D, 0.0D, 0.0D);
				t.addVertex(0.1D, 0.1D, 0.1D);
				t.draw();
				//GL11.glDepthMask(true);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_BLEND);

				//GuiIngame.drawRect(10, 10, 20, 20, 0xffff0000);
				//this.mc.fontRenderer.drawString("REC", 10, 40, 0xff00ff00);
				//System.out.println("rec");
*/
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
		}
	}
}
