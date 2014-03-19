package fi.dy.masa.minecraft.mods.multishot.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MultishotScreenConfigsGeneric extends MultishotScreenBase
{
	private int i = 3;

	public MultishotScreenConfigsGeneric (GuiScreen parent)
	{
		super(parent);
		multishotScreenConfigsGeneric = this;
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		//this.drawBackground(0);
		super.drawScreen(par1,  par2,  par3);
		this.fontRenderer.drawString("Generic", 10, this.height - 30, 0xffffffff);
	}

	@Override
	public void initGui()
	{
		super.initGui();
		// FIXME testing:
		System.out.println("MultishotScreenConfigsGeneric().initGUI()");
		this.guiButtonScreenGeneric.enabled = false;
		// FIXME testing:
		buttonList.add(new GuiButton(10, 10, 40, 50, 20, "Test 11"));
		buttonList.add(new GuiButton(11, 200, 40, 50, 20, "Test 12"));
		buttonList.add(new GuiButton(12, 10, 62, 50, 20, "Test 13"));
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		super.actionPerformed(par1GuiButton);
		//FIXME testing:
		if (par1GuiButton.id == 12)
		{
			par1GuiButton.displayString = "Test " + ++i;
		}
	}
}
