package fi.dy.masa.minecraft.mods.multishot.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.libs.Constants;

@SideOnly(Side.CLIENT)
public class MultishotScreenConfigsMotion extends MultishotScreenBase
{
	private GuiButton buttonMovementX = null;
	private GuiButton buttonMovementZ = null;
	private GuiButton buttonMovementY = null;
	private GuiButton buttonRotationYaw = null;
	private GuiButton buttonRotationPitch = null;

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

		int xl = (this.width / 2) - 130;
		int xr = (this.width / 2) + 5;
		int y = (this.height / 2) - 75;
		this.buttonMovementX		= new GuiButton(Constants.GUI_BUTTON_ID_MOVEMENT_X,		xl, y + 0, 120, 20, "X-axis movement: OFF");
		this.buttonMovementZ		= new GuiButton(Constants.GUI_BUTTON_ID_MOVEMENT_Z,		xl, y + 23, 120, 20, "Z-axis movement: OFF");
		this.buttonMovementY		= new GuiButton(Constants.GUI_BUTTON_ID_MOVEMENT_Y,		xl, y + 46, 120, 20, "Y-axis movement: OFF");
		this.buttonRotationYaw		= new GuiButton(Constants.GUI_BUTTON_ID_ROTATION_YAW,	xr, y + 0, 120, 20, "Yaw rotation: OFF");
		this.buttonRotationPitch	= new GuiButton(Constants.GUI_BUTTON_ID_ROTATION_PITCH,	xr, y + 23, 120, 20, "Pitch rotation: OFF");
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
