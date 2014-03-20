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
		MultishotScreenConfigsMotion.multishotScreenConfigsMotion = this;
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		super.drawScreen(par1,  par2,  par3);
		// FIXME debug:
		this.fontRenderer.drawString("Motion", 10, this.height - 30, 0xffffffff);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		super.initGui();
		this.guiButtonScreenMotion.enabled = false;
		// FIXME testing:
		int x = (this.width / 2) - 130;
		int y = (this.height / 2) - 70;
		buttonList.add(new GuiButton(20, x, y + 0, 120, 20, "Test 21"));
		buttonList.add(new GuiButton(21, x, y + 22, 120, 20, "Test 22"));
		buttonList.add(new GuiButton(22, x, y + 44, 120, 20, "Test 23"));
		// FIXME debug:
		System.out.println("MultishotScreenConfigsMotion().initGUI()");
		System.out.println("buttonList.size():" + buttonList.size());
	}
}
