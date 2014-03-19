package fi.dy.masa.minecraft.mods.multishot.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MultishotScreenConfigsMotion extends MultishotScreenBase
{
	public MultishotScreenConfigsMotion (GuiScreen parent)
	{
		super(parent);
		multishotScreenConfigsMotion = this;
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		//this.drawBackground(0);
		super.drawScreen(par1,  par2,  par3);
		this.fontRenderer.drawString("Motion", 10, this.height - 30, 0xffffffff);
	}

	@Override
	public void initGui()
	{
		super.initGui();
		// FIXME testing:
		System.out.println("MultishotScreenConfigsMotion().initGUI()");
		this.guiButtonScreenMotion.enabled = false;
		// FIXME testing:
		buttonList.add(new GuiButton(20, 10, 40, 50, 20, "Test 21"));
		buttonList.add(new GuiButton(21, 200, 40, 50, 20, "Test 22"));
		buttonList.add(new GuiButton(22, 10, 62, 50, 20, "Test 23"));
	}
}
