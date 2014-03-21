package fi.dy.masa.minecraft.mods.multishot.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.config.MultishotConfigs;
import fi.dy.masa.minecraft.mods.multishot.libs.Constants;
import fi.dy.masa.minecraft.mods.multishot.libs.Reference;

@SideOnly(Side.CLIENT)
public class MultishotScreenConfigsGeneric extends MultishotScreenBase
{
	private GuiButton buttonInterval = null;
	private GuiButton buttonZoom = null;
	private GuiButton buttonBrowse = null;
	private GuiButton buttonOpenDirectory = null;
	private GuiButton buttonMultishotEnabled = null;
	private GuiButton buttonMotionEnabled = null;
	private GuiButton buttonLockControls = null;
	private GuiButton buttonHideGui = null;
	private GuiButton buttonLoadDefaults = null;

	public MultishotScreenConfigsGeneric (MultishotConfigs cfg, GuiScreen parent)
	{
		super(cfg, parent);
		MultishotScreenConfigsGeneric.multishotScreenConfigsGeneric = this;
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		super.drawScreen(par1,  par2,  par3);
		// FIXME debug:
		this.fontRenderer.drawString("Generic", 10, this.height - 30, 0xffffffff);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		super.initGui();
		this.guiButtonScreenGeneric.enabled = false;

		int xl = (this.width / 2) - 130;
		int xr = (this.width / 2) + 5;
		int yt = (this.height / 2) - 75;
		this.buttonInterval			= createGuiButton(Constants.GUI_BUTTON_ID_INTERVAL,			xl, yt + 0, 125, 20);
		this.buttonZoom				= createGuiButton(Constants.GUI_BUTTON_ID_ZOOM,				xl, yt + 23, 125, 20);
		this.buttonBrowse			= createGuiButton(Constants.GUI_BUTTON_ID_BROWSE,			xl, yt + 132, 80, 20);
		this.buttonOpenDirectory	= createGuiButton(Constants.GUI_BUTTON_ID_OPEN_DIR,			xl + 85, yt + 132, 90, 20);
		this.buttonMultishotEnabled	= createGuiButton(Constants.GUI_BUTTON_ID_MULTISHOT_ENABLED,xr, yt + 0, 125, 20);
		this.buttonMotionEnabled	= createGuiButton(Constants.GUI_BUTTON_ID_MOTION_ENABLED,	xr, yt + 23, 125, 20);
		this.buttonLockControls		= createGuiButton(Constants.GUI_BUTTON_ID_LOCK_CONTROLS,	xr, yt + 46, 125, 20);
		this.buttonHideGui			= createGuiButton(Constants.GUI_BUTTON_ID_HIDE_GUI,			xr, yt + 69, 125, 20);
		this.buttonLoadDefaults		= createGuiButton(Constants.GUI_BUTTON_ID_LOAD_DEFAULTS,	xr + 45, yt + 132, 80, 20);
		buttonList.add(this.buttonInterval);
		buttonList.add(this.buttonZoom);
		buttonList.add(this.buttonBrowse);
		buttonList.add(this.buttonLoadDefaults);
		buttonList.add(this.buttonMultishotEnabled);
		buttonList.add(this.buttonMotionEnabled);
		buttonList.add(this.buttonLockControls);
		buttonList.add(this.buttonHideGui);
		buttonList.add(this.buttonOpenDirectory);
		// FIXME debug:
		System.out.println("MultishotScreenConfigsGeneric().initGUI()");
		System.out.println("buttonList.size():" + buttonList.size());
		System.out.println("Multishot base directory ('" + this.mc.mcDataDir.getAbsolutePath() + "/" + Reference.MULTISHOT_BASE_DIR + "')");  
	}

	@Override
    protected void mouseClicked(int par1, int par2, int par3)
	{
		super.mouseClicked(par1,  par2,  par3);
		this.initGui();
	}
}
