package fi.dy.masa.minecraft.mods.multishot.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.libs.Constants;
import fi.dy.masa.minecraft.mods.multishot.libs.Reference;

@SideOnly(Side.CLIENT)
public class MultishotScreenConfigsGeneric extends MultishotScreenBase
{
	private int i = 3; // FIXME debug
	private GuiButton buttonInterval = null;
	private GuiButton buttonZoom = null;
	private GuiButton buttonBrowse = null;
	private GuiButton buttonOpenDirectory = null;
	private GuiButton buttonMultishotEnabled = null;
	private GuiButton buttonMotionEnabled = null;
	private GuiButton buttonLockControls = null;
	private GuiButton buttonHideGui = null;
	private GuiButton buttonLoadDefaults = null;

	public MultishotScreenConfigsGeneric (GuiScreen parent)
	{
		super(parent);
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
		this.buttonInterval			= new GuiButton(Constants.GUI_BUTTON_ID_INTERVAL,			xl, yt + 0, 125, 20, "Shot interval: OFF");
		this.buttonZoom				= new GuiButton(Constants.GUI_BUTTON_ID_ZOOM,				xl, yt + 23, 125, 20, "Zoom: OFF");
		this.buttonBrowse			= new GuiButton(Constants.GUI_BUTTON_ID_BROWSE,				xl, yt + 132, 80, 20, "Browse");
		this.buttonOpenDirectory	= new GuiButton(Constants.GUI_BUTTON_ID_OPEN_DIR,			xl + 85, yt + 132, 90, 20, "Open Directory");
		this.buttonMultishotEnabled	= new GuiButton(Constants.GUI_BUTTON_ID_MULTISHOT_ENABLED,	xr, yt + 0, 125, 20, "Multishot Enabled: OFF");
		this.buttonMotionEnabled	= new GuiButton(Constants.GUI_BUTTON_ID_MOTION_ENABLED,		xr, yt + 23, 125, 20, "Motion Enabled: OFF");
		this.buttonLockControls		= new GuiButton(Constants.GUI_BUTTON_ID_LOCK_CONTROLS,		xr, yt + 46, 125, 20, "Lock Controls: OFF");
		this.buttonHideGui			= new GuiButton(Constants.GUI_BUTTON_ID_HIDE_GUI,			xr, yt + 69, 125, 20, "Hide Multishot GUI: OFF");
		this.buttonLoadDefaults		= new GuiButton(Constants.GUI_BUTTON_ID_DEFAULTS_GENERIC,	xr + 45, yt + 132, 80, 20, "Load Defaults");
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
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		super.actionPerformed(par1GuiButton);
		//FIXME debug:
		if (par1GuiButton.id == 12)
		{
			par1GuiButton.displayString = "Test " + ++i;
		}
	}
}
