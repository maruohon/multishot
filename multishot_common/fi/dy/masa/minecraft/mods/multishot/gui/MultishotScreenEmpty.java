package fi.dy.masa.minecraft.mods.multishot.gui;

import net.minecraft.client.gui.GuiScreen;

public class MultishotScreenEmpty extends GuiScreen
{
	private static MultishotScreenEmpty multishotScreenEmpty = null;
	public MultishotScreenEmpty ()
	{
		multishotScreenEmpty = this;
	}

	public static MultishotScreenEmpty getInstance()
	{
		return multishotScreenEmpty;
	}

	@Override
	public void drawScreen (int i, int j, float f)
	{
	}

	@Override
	public void initGui ()
	{
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	@Override
	public void keyTyped (char keyChar, int keyID)
	{
	}
}
