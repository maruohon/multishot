package fi.dy.masa.minecraft.mods.multishot.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.config.MultishotConfigs;
import fi.dy.masa.minecraft.mods.multishot.libs.Constants;
import fi.dy.masa.minecraft.mods.multishot.libs.Reference;

@SideOnly(Side.CLIENT)
public abstract class MultishotScreenBase extends GuiScreen
{
	protected Minecraft mc = null;
	protected Configuration configuration = null;
	protected MultishotConfigs multishotConfigs = null;
	protected GuiScreen parent = null;
	protected GuiButton guiButtonScreenGeneric = null;
	protected GuiButton guiButtonScreenMotion = null;
	protected GuiButton guiButtonBackToGame = null;
	protected static MultishotScreenConfigsGeneric multishotScreenConfigsGeneric = null;
	protected static MultishotScreenConfigsMotion multishotScreenConfigsMotion = null;
	protected List<GuiButton> multishotScreenButtons = null;

	public MultishotScreenBase (Configuration cfg, MultishotConfigs msCfg, GuiScreen parent)
	{
		this.mc = Minecraft.getMinecraft();
		this.configuration = cfg;
		this.multishotConfigs = msCfg;
		this.parent = parent;
		this.multishotScreenButtons = new ArrayList<GuiButton>();
	}

	@Override
	public void drawScreen(int i, int j, float f)
	{
		drawDefaultBackground();	// The default dark background
		super.drawScreen(i, j, f);

		String s = "Multishot settings"; // FIXME needs localization
		int textWidth = this.fontRenderer.getStringWidth(s);
		this.fontRenderer.drawString(s, (this.width / 2) - (textWidth / 2), (this.height / 2) - 115, 0xffffffff);
		s = Reference.MOD_NAME + " v" + Reference.VERSION;
		this.fontRenderer.drawString(s, (this.width / 2) - 155, (this.height / 2) + 110, 0xff909090);

		// FIXME debug
		this.fontRenderer.drawString("drawScreen(" + i + ", " + j + ", " + f + ")", 10, this.height - 20, 0xffffffff);
		this.fontRenderer.drawString("w:" + this.width + " h:" + this.height, 190, this.height - 20, 0xffffffff);

		Gui.drawRect((this.width / 2) - 160, (this.height / 2) - 120, (this.width / 2) + 160, (this.height / 2) - 121, 0xffffffff);
		Gui.drawRect((this.width / 2) - 160, (this.height / 2) - 120, (this.width / 2) - 161, (this.height / 2) + 121, 0xffffffff);
		Gui.drawRect((this.width / 2) - 160, (this.height / 2) + 120, (this.width / 2) + 160, (this.height / 2) + 121, 0xffffffff);
		Gui.drawRect((this.width / 2) + 160, (this.height / 2) - 120, (this.width / 2) + 161, (this.height / 2) + 121, 0xffffffff);

	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		//System.out.println("MultishotScreenBase().initGUI()"); // FIXME debug
		// Create the settings screen buttons
		int x = (this.width / 2) - 130;
		int y = (this.height / 2) - 100;
		this.guiButtonScreenGeneric	= new GuiButton(Constants.GUI_BUTTON_ID_SCREEN_GENERIC,	x + 0, y + 0, 60, 20, "Generic"); // FIXME add localization to these
		this.guiButtonScreenMotion	= new GuiButton(Constants.GUI_BUTTON_ID_SCREEN_MOTION,	x + 64, y + 0, 60, 20, "Motion");
		this.guiButtonBackToGame	= new GuiButton(Constants.GUI_BUTTON_ID_BACK_TO_GAME, (this.width / 2) - 100, (this.height / 2) + 80, 200, 20, "Back To Game");
		// Add the buttons that change the menu screen into a list, against which the button presses will be checked
		// when checking if we need to change the menu screen.
		multishotScreenButtons.clear();
		multishotScreenButtons.add(this.guiButtonScreenGeneric);
		multishotScreenButtons.add(this.guiButtonScreenMotion);
		buttonList.clear();
		buttonList.add(this.guiButtonScreenGeneric);
		buttonList.add(this.guiButtonScreenMotion);
		buttonList.add(this.guiButtonBackToGame);
	}

	@Override
	public void keyTyped(char keyChar, int keyID)
	{
		if (keyID == 1)
		{
			if (this.parent == null)
			{
				this.mc.displayGuiScreen((GuiScreen)null);
				this.mc.setIngameFocus();
				if (this.configuration.hasChanged())
				{
					this.configuration.save();
				}
			}
			else
			{
				this.mc.displayGuiScreen(this.parent);
			}
		}
	}

	@Override
	protected void mouseClicked(int par1, int par2, int par3)
	{
		// Let the regular left clicks go through "the usual channels"
		super.mouseClicked(par1, par2, par3);
		System.out.println("par3: " + par3); // FIXME debug

		for (int l = 0; l < this.buttonList.size(); ++l)
		{
			GuiButton guibutton = (GuiButton)this.buttonList.get(l);

			if (guibutton.mousePressed(this.mc, par1, par2))
			{
				this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);	
				if (par3 == 1) // Right click
				{
					this.actionPerformedRight(guibutton);
				}
				else if (par3 == 2) // Middle click
				{
					this.actionPerformedMiddle(guibutton);
				}
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		//System.out.println("MultishotScreenBase().actionPerformed()"); // FIXME debug
		if (this.isMenuScreenButton(par1GuiButton))
		{
			this.changeActiveScreen(par1GuiButton);
			//System.out.println("MultishotScreenBase().actionPerformed().isMenuScreenButton() == true"); // FIXME debug
		}
		else if(par1GuiButton.id == Constants.GUI_BUTTON_ID_BACK_TO_GAME)
		{
			this.mc.displayGuiScreen((GuiScreen)null);
			this.mc.setIngameFocus();
			if (this.configuration.hasChanged())
			{
				this.configuration.save();
			}
		}
		else if (par1GuiButton.id == Constants.GUI_BUTTON_ID_LOAD_DEFAULTS)
		{
			this.multishotConfigs.resetAllConfigs();
		}
		else if (isConfigButton(par1GuiButton))
		{
			int mode = 0; // 0..3 for 1/10/100/1000 at a time
			if (isCtrlKeyDown())
			{
				if(isShiftKeyDown())
				{
					mode = 3;
				}
				else
				{
					mode = 1;
				}
			}
			else if(isShiftKeyDown())
			{
				mode = 2;
			}
			this.multishotConfigs.changeValue(par1GuiButton.id, mode, 0);
		}
	}

	protected void actionPerformedRight(GuiButton par1GuiButton)
	{
		//System.out.println("MultishotScreenBase().actionPerformedRight()"); // FIXME debug
		if (isConfigButton(par1GuiButton))
		{
			int mode = 0; // 0..3 for 1/10/100/1000 at a time
			if (isCtrlKeyDown())
			{
				if(isShiftKeyDown())
				{
					mode = 3;
				}
				else
				{
					mode = 1;
				}
			}
			else if(isShiftKeyDown())
			{
				mode = 2;
			}
			this.multishotConfigs.changeValue(par1GuiButton.id, mode, 1);
		}
	}

	protected void actionPerformedMiddle(GuiButton par1GuiButton)
	{
		//System.out.println("MultishotScreenBase().actionPerformedMiddle()"); // FIXME debug
		if (isConfigButton(par1GuiButton))
		{
			this.multishotConfigs.resetValue(par1GuiButton.id);
		}
	}

	// Is this button one that changes the menu screen?
	protected boolean isMenuScreenButton(GuiButton btn)
	{
		for (int i = 0; i < this.multishotScreenButtons.size(); i++)
		{
			if (this.multishotScreenButtons.get(i).id == btn.id)
			{
				return true;
			}
		}
		return false;
	}

	protected boolean isConfigButton(GuiButton btn)
	{
		// FIXME This is really error prone!!
		return ! this.isMenuScreenButton(btn);
	}

	// Change the active menu screen
	protected void changeActiveScreen(GuiButton btn)
	{
		if (btn.id == Constants.GUI_BUTTON_ID_SCREEN_GENERIC)
		{
			//System.out.println("MultishotScreenBase().changeActiveScreen() if btn.id == GENERIC"); // FIXME debug
			if (multishotScreenConfigsGeneric == null)
			{
				//System.out.println("MultishotScreenBase().changeActiveScreen() if generic == null"); // FIXME debug
				multishotScreenConfigsGeneric = new MultishotScreenConfigsGeneric(this.configuration, this.multishotConfigs, null);
			}
			this.mc.displayGuiScreen(multishotScreenConfigsGeneric);
		}
		else if (btn.id == Constants.GUI_BUTTON_ID_SCREEN_MOTION)
		{
			//System.out.println("MultishotScreenBase().changeActiveScreen() if btn.id == MOTION"); // FIXME debug
			if (multishotScreenConfigsMotion == null)
			{
				//System.out.println("MultishotScreenBase().changeActiveScreen() if motion == null"); // FIXME debug
				multishotScreenConfigsMotion = new MultishotScreenConfigsMotion(this.configuration, this.multishotConfigs, null);
			}
			this.mc.displayGuiScreen(multishotScreenConfigsMotion);
		}
		System.out.println("-------------------"); // FIXME debug
	}

	public GuiButton createGuiButton (int id, int x, int y, int w, int h)
	{
		String s;
		s = getButtonDisplayString(id);
		return new GuiButton(id, x, y, w, h, s);
	}

	public String getButtonDisplayString(int id)
	{
		String s;
		s = getButtonDisplayStringBase(id) + this.multishotConfigs.getDisplayString(id);
		return s;
	}

	// FIXME change this into a hash map or something, also with localization support
	public String getButtonDisplayStringBase (int id)
	{
		String s = "";
		switch(id)
		{
			case Constants.GUI_BUTTON_ID_MULTISHOT_ENABLED:
				s = "Multishot Enabled" + ": ";
				break;
			case Constants.GUI_BUTTON_ID_MOTION_ENABLED:
				s = "Motion Enabled" + ": ";
				break;
			case Constants.GUI_BUTTON_ID_LOCK_CONTROLS:
				s = "Lock Controls" + ": ";
				break;
			case Constants.GUI_BUTTON_ID_HIDE_GUI:
				s = "Hide Multishot GUI" + ": ";
				break;
			case Constants.GUI_BUTTON_ID_INTERVAL:
				s = "Shot Interval" + ": ";
				break;
			case Constants.GUI_BUTTON_ID_ZOOM:
				s = "Zoom" + ": ";
				break;
			case Constants.GUI_BUTTON_ID_TIMER_SELECT:
				s = "Recording Timer" + ": ";
				break;
			case Constants.GUI_BUTTON_ID_MOTION_X:
				s = "X-axis motion" + ": ";
				break;
			case Constants.GUI_BUTTON_ID_MOTION_Z:
				s = "Z-axis motion" + ": ";
				break;
			case Constants.GUI_BUTTON_ID_MOTION_Y:
				s = "Y-axis motion" + ": ";
				break;
			case Constants.GUI_BUTTON_ID_ROTATION_YAW:
				s = "Yaw Rotation" + ": ";
				break;
			case Constants.GUI_BUTTON_ID_ROTATION_PITCH:
				s = "Pitch Rotation" + ": ";
				break;
			case Constants.GUI_BUTTON_ID_BROWSE:
				s = "Browse";
				break;
			case Constants.GUI_BUTTON_ID_OPEN_DIR:
				s = "Open Directory";
				break;
			case Constants.GUI_BUTTON_ID_LOAD_DEFAULTS:
				s = "Load Defaults";
				break;
			default:
				break;
		}
		return s;
	}

	public String formatByteSize (long size)
	{
		double sized = (double)size;
		if (size >= (1024L * 1024L * 1024L * 1024L)) // TB
		{
			return String.format("%.2f TB", sized / (1024.0 * 1024.0 * 1024.0 * 1024.0));
		}
		if (size >= (1024L * 1024L * 1024L)) // GB
		{
			return String.format("%.2f GB", sized / (1024.0 * 1024.0 * 1024.0));
		}
		if (size >= (1024L * 1024L))	// MB
		{
			return String.format("%.2f MB", sized / (1024.0 * 1024.0));
		}
		if (size >= 1024L)	// kB
		{
			return String.format("%.2f kB", sized / 1024.0);
		}
		return String.format("%d B", size); // B
	}
}
