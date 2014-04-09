package fi.dy.masa.minecraft.mods.multishot.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.config.MultishotConfigs;
import fi.dy.masa.minecraft.mods.multishot.libs.Constants;

@SideOnly(Side.CLIENT)
public class MultishotScreenConfigsMotion extends MultishotScreenBase
{
	private GuiButton buttonMotionMode = null;
	private GuiButton buttonMotionSpeed = null;
	private GuiButton buttonMovementX = null;
	private GuiButton buttonMovementZ = null;
	private GuiButton buttonMovementY = null;
	private GuiButton buttonRotationYaw = null;
	private GuiButton buttonRotationPitch = null;

	public MultishotScreenConfigsMotion (Configuration cfg, MultishotConfigs msCfg, GuiScreen parent)
	{
		super(cfg, msCfg, parent);
		MultishotScreenConfigsMotion.multishotScreenConfigsMotion = this;
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		super.drawScreen(par1,  par2,  par3);
		int xl = (this.width / 2) - 130;
		int xr = (this.width / 2) + 0;
		int y = (this.height / 2) - 75;
		this.fontRenderer.drawString("Non-Linear:", xl + 5, y + 32	, 0xffffffff);
		this.fontRenderer.drawString("Linear:", xr + 5, y + 10, 0xffffffff);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		super.initGui();
		this.guiButtonScreenMotion.enabled = false;

		int xl = (this.width / 2) - 130;
		int xr = (this.width / 2) + 0;
		int y = (this.height / 2) - 75;

		this.buttonMotionMode		= createGuiButton(Constants.GUI_BUTTON_ID_MOTION_MODE,		xl, y + 0, 120, 20);
		this.buttonMotionSpeed		= createGuiButton(Constants.GUI_BUTTON_ID_MOTION_SPEED,		xl, y + 44, 120, 20);

		this.buttonMovementX		= createGuiButton(Constants.GUI_BUTTON_ID_MOTION_X,			xr, y + 22, 130, 20);
		this.buttonMovementZ		= createGuiButton(Constants.GUI_BUTTON_ID_MOTION_Z,			xr, y + 44, 130, 20);
		this.buttonMovementY		= createGuiButton(Constants.GUI_BUTTON_ID_MOTION_Y,			xr, y + 66, 130, 20);
		this.buttonRotationYaw		= createGuiButton(Constants.GUI_BUTTON_ID_ROTATION_YAW,		xr, y + 88, 130, 20);
		this.buttonRotationPitch	= createGuiButton(Constants.GUI_BUTTON_ID_ROTATION_PITCH,	xr, y + 110, 130, 20);

		buttonList.add(this.buttonMotionMode);
		buttonList.add(this.buttonMotionSpeed);
		buttonList.add(this.buttonMovementX);
		buttonList.add(this.buttonMovementZ);
		buttonList.add(this.buttonMovementY);
		buttonList.add(this.buttonRotationYaw);
		buttonList.add(this.buttonRotationPitch);
	}

	@Override
    protected void mouseClicked(int par1, int par2, int par3)
	{
		super.mouseClicked(par1,  par2,  par3);
		this.initGui();
	}

	@Override
	public void mouseScrolled(int value)
	{
		super.mouseScrolled(value);
		this.initGui();
	}
}
