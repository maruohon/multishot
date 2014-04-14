package fi.dy.masa.minecraft.mods.multishot.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.config.MsConfigs;
import fi.dy.masa.minecraft.mods.multishot.reference.MsConstants;

@SideOnly(Side.CLIENT)
public class MsScreenMotion extends MsScreenBase
{
	private GuiButton buttonMotionMode = null;
	private GuiButton buttonMotionSpeed = null;
	private GuiButton buttonMovementX = null;
	private GuiButton buttonMovementZ = null;
	private GuiButton buttonMovementY = null;
	private GuiButton buttonRotationYaw = null;
	private GuiButton buttonRotationPitch = null;

	public MsScreenMotion (Configuration cfg, MsConfigs msCfg, GuiScreen parent)
	{
		super(cfg, msCfg, parent);
		MsScreenMotion.multishotScreenConfigsMotion = this;
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		super.drawScreen(par1,  par2,  par3);
		int xl = (this.width / 2) - 130;
		int xr = (this.width / 2) + 0;
		int y = (this.height / 2) - 75;
		this.fontRendererObj.drawString("Non-Linear:", xl + 5, y + 32	, 0xffffffff);
		this.fontRendererObj.drawString("Linear:", xr + 5, y + 10, 0xffffffff);
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

		this.buttonMotionMode		= createGuiButton(MsConstants.GUI_BUTTON_ID_MOTION_MODE,		xl, y + 0, 120, 20);
		this.buttonMotionSpeed		= createGuiButton(MsConstants.GUI_BUTTON_ID_MOTION_SPEED,		xl, y + 44, 120, 20);

		this.buttonMovementX		= createGuiButton(MsConstants.GUI_BUTTON_ID_MOTION_X,			xr, y + 22, 130, 20);
		this.buttonMovementZ		= createGuiButton(MsConstants.GUI_BUTTON_ID_MOTION_Z,			xr, y + 44, 130, 20);
		this.buttonMovementY		= createGuiButton(MsConstants.GUI_BUTTON_ID_MOTION_Y,			xr, y + 66, 130, 20);
		this.buttonRotationYaw		= createGuiButton(MsConstants.GUI_BUTTON_ID_ROTATION_YAW,		xr, y + 88, 130, 20);
		this.buttonRotationPitch	= createGuiButton(MsConstants.GUI_BUTTON_ID_ROTATION_PITCH,	xr, y + 110, 130, 20);

		buttonList.add(this.buttonMotionMode);
		buttonList.add(this.buttonMotionSpeed);
		buttonList.add(this.buttonMovementX);
		buttonList.add(this.buttonMovementZ);
		buttonList.add(this.buttonMovementY);
		buttonList.add(this.buttonRotationYaw);
		buttonList.add(this.buttonRotationPitch);
	}

	@Override
	public void handleMouseInput()
	{
		super.handleMouseInput();
		this.initGui();
	}
}
