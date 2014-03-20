package fi.dy.masa.minecraft.mods.multishot.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.libs.Reference;

@SideOnly(Side.CLIENT)
public class MultishotScreenConfigsGeneric extends MultishotScreenBase
{
	private int i = 3; // FIXME debug
	private GuiButton buttonMultishotEnabled = null;
	private GuiButton buttonMotionEnabled = null;
	private GuiButton buttonZoom = null;
	private GuiButton buttonLockControls = null;
	private GuiButton buttonOpenDirectory = null;
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

		// FIXME testing:
		int x = (this.width / 2) - 130;
		int y = (this.height / 2) - 70;
		this.buttonMultishotEnabled	= new GuiButton(10, x, y + 0, 120, 20, "Multishot Enabled:");
		this.buttonMotionEnabled	= new GuiButton(11, x, y + 22, 120, 20, "Motion Enabled:");
		this.buttonLockControls		= new GuiButton(13, x, y + 44, 120, 20, "Lock Controls:");
		this.buttonZoom				= new GuiButton(12, x, y + 66, 120, 20, "Zoom Level:");
		this.buttonOpenDirectory	= new GuiButton(14, x, y + 88, 120, 20, "Open Directory");
		this.buttonLoadDefaults		= new GuiButton(15, x, y + 110, 120, 20, "Load Defaults");

		buttonList.add(this.buttonMultishotEnabled);
		buttonList.add(this.buttonMotionEnabled);
		buttonList.add(this.buttonZoom);
		buttonList.add(this.buttonLockControls);
		buttonList.add(this.buttonOpenDirectory);
		buttonList.add(this.buttonLoadDefaults);
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
