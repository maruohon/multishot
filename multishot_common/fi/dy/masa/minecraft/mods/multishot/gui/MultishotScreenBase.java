package fi.dy.masa.minecraft.mods.multishot.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public abstract class MultishotScreenBase extends GuiScreen
{
	protected GuiScreen parent;
	protected Minecraft mc;

	public MultishotScreenBase (GuiScreen parent)
	{
		this.parent	= parent;
		this.mc		= Minecraft.getMinecraft();
	}

	// For convenience
	public void display()
	{
		this.mc.displayGuiScreen(this);
	}

	@Override
	public void drawScreen(int i, int j, float f)
	{
		this.drawDefaultBackground();	// The default dark background
		super.drawScreen(i, j, f);
	}

	@Override
	public void keyTyped(char keyChar, int keyID)
	{
		if (keyID == 1)
		{
			if (this.parent == null)
			{
				this.mc.displayGuiScreen((GuiScreen)null);
				this.mc.setIngameFocus();
			}
			else
			{
				this.mc.displayGuiScreen(this.parent);
			}
		}
	}
}
