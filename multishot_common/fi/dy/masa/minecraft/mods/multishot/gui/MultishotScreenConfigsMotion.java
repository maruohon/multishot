package fi.dy.masa.minecraft.mods.multishot.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.config.MultishotConfigs;
import fi.dy.masa.minecraft.mods.multishot.libs.Constants;

@SideOnly(Side.CLIENT)
public class MultishotScreenConfigsMotion extends MultishotScreenBase
{
	private GuiButton buttonMovementX = null;
	private GuiButton buttonMovementZ = null;
	private GuiButton buttonMovementY = null;
	private GuiButton buttonRotationYaw = null;
	private GuiButton buttonRotationPitch = null;

	public MultishotScreenConfigsMotion (MultishotConfigs cfg, GuiScreen parent)
	{
		super(cfg, parent);
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

		int xl = (this.width / 2) - 130;
		int xr = (this.width / 2) + 5;
		int y = (this.height / 2) - 75;
		this.buttonMovementX		= createGuiButton(Constants.GUI_BUTTON_ID_MOTION_X,			xl, y + 0, 120, 20);
		this.buttonMovementZ		= createGuiButton(Constants.GUI_BUTTON_ID_MOTION_Z,			xl, y + 23, 120, 20);
		this.buttonMovementY		= createGuiButton(Constants.GUI_BUTTON_ID_MOTION_Y,			xl, y + 46, 120, 20);
		this.buttonRotationYaw		= createGuiButton(Constants.GUI_BUTTON_ID_ROTATION_YAW,		xr, y + 0, 120, 20);
		this.buttonRotationPitch	= createGuiButton(Constants.GUI_BUTTON_ID_ROTATION_PITCH,	xr, y + 23, 120, 20);
		buttonList.add(this.buttonMovementX);
		buttonList.add(this.buttonMovementZ);
		buttonList.add(this.buttonMovementY);
		buttonList.add(this.buttonRotationYaw);
		buttonList.add(this.buttonRotationPitch);
		// FIXME debug:
		System.out.println("MultishotScreenConfigsMotion().initGUI()");
		System.out.println("buttonList.size():" + buttonList.size());
	}
}
