package fi.dy.masa.minecraft.mods.multishot.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Mouse;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.reference.MsConstants;
import fi.dy.masa.minecraft.mods.multishot.reference.MsReference;
import fi.dy.masa.minecraft.mods.multishot.state.MsClassReference;
import fi.dy.masa.minecraft.mods.multishot.state.MsState;

@SideOnly(Side.CLIENT)
public abstract class MsScreenBase extends GuiScreen
{
	protected Minecraft mc = null;
	protected GuiScreen parent = null;
	protected GuiButton guiButtonScreenGeneric = null;
	protected GuiButton guiButtonScreenMotion = null;
	protected GuiButton guiButtonBackToGame = null;
	protected static MsScreenGeneric multishotScreenConfigsGeneric = null;
	protected static MsScreenMotion multishotScreenConfigsMotion = null;
	protected List<GuiButton> multishotScreenButtons = null;
	protected int dWheel = 0;
	protected int eventX = 0;
	protected int eventY = 0;

	public MsScreenBase (GuiScreen parent)
	{
		this.mc = Minecraft.getMinecraft();
		this.parent = parent;
		this.multishotScreenButtons = new ArrayList<GuiButton>();
	}

	@Override
	public void drawScreen(int i, int j, float f)
	{
		drawDefaultBackground();	// The default dark background
		super.drawScreen(i, j, f);

		String s = "Multishot settings"; // FIXME needs localization
		int textWidth = this.fontRendererObj.getStringWidth(s);
		int x = (this.width / 2);
		int y = (this.height / 2);
		this.fontRendererObj.drawString(s, x - (textWidth / 2), y - 115, 0xffffffff);
		s = " v" + MsReference.VERSION;
		this.fontRendererObj.drawString(s, x - 130, y - 115, 0xffb0b0b0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		// Create the settings screen buttons
		int x = (this.width / 2) - 130;
		int y = (this.height / 2) - 100;
		this.guiButtonScreenGeneric	= new GuiButton(MsConstants.GUI_BUTTON_ID_SCREEN_GENERIC,	x + 0, y + 0, 60, 20, "Generic"); // FIXME add localization to these
		this.guiButtonScreenMotion	= new GuiButton(MsConstants.GUI_BUTTON_ID_SCREEN_MOTION,	x + 64, y + 0, 60, 20, "Motion");
		this.guiButtonBackToGame	= new GuiButton(MsConstants.GUI_BUTTON_ID_BACK_TO_GAME, (this.width / 2) - 100, (this.height / 2) + 80, 200, 20, "Back To Game");
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
		//System.out.printf("MultishotScreenBase.keyTyped(): keyChar: %c keyID: %d\n", keyChar, keyID);
		if (keyID == 1) // ESC
		{
			if (this.parent == null)
			{
				this.mc.displayGuiScreen((GuiScreen)null);
				this.mc.setIngameFocus();
			}
			else
			{
				this.mc.displayGuiScreen(this.parent);
			}

			if (MsClassReference.getConfiguration().hasChanged())
			{
				MsClassReference.getConfiguration().save();
				MsState.setStateFromConfigs(MsClassReference.getMsConfigs());
			}
		}
	}

	@Override
	public void handleMouseInput()
	{
		super.handleMouseInput();

		this.dWheel = Mouse.getEventDWheel();
		if (this.dWheel != 0)
		{
			this.dWheel /= 120;
			this.eventX = Mouse.getEventX() * this.width / this.mc.displayWidth;
			this.eventY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
			this.mouseScrolled(this.eventX, this.eventY, this.dWheel);
		}
	}

	public void mouseScrolled(int x, int y, int value)
	{
		GuiButton guiButton;

		for (int i = 0; i < this.buttonList.size(); ++i)
		{
			guiButton = (GuiButton)this.buttonList.get(i);

			if (guiButton.mousePressed(this.mc, x, y))
			{
				if (isConfigButton(guiButton))
				{
					int mode = this.getButtonModifier(); // 0..3 for 1/10/100/1000 at a time
					// value is the number of "notches" the wheel was scrolled, positive for up, negative for down
					MsClassReference.getMsConfigs().changeValue(guiButton.id, mode, 0, value);
					this.updateGuiButton(guiButton, guiButton.id);
				}
				break;
			}
		}
	}

	private int getButtonModifier()
	{
		if (isCtrlKeyDown() && isShiftKeyDown()) { return 3; }
		else if(isShiftKeyDown()) { return 2; }
		else if (isCtrlKeyDown()) { return 1; }
		return 0;
	}

	@Override
	protected void mouseClicked(int par1, int par2, int par3)
	{
		for (int l = 0; l < this.buttonList.size(); ++l)
		{
			GuiButton guiButton = (GuiButton)this.buttonList.get(l);

			if (guiButton.mousePressed(this.mc, par1, par2))
			{
				guiButton.func_146113_a(this.mc.getSoundHandler());
				if (par3 == 0) // Left click
				{
					this.actionPerformedLeft(guiButton);
				}
				else if (par3 == 1) // Right click
				{
					this.actionPerformedRight(guiButton);
				}
				else if (par3 == 2) // Middle click
				{
					this.actionPerformedMiddle(guiButton);
				}
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		if (this.isMenuScreenButton(par1GuiButton))
		{
			this.changeActiveScreen(par1GuiButton);
		}
		else if(par1GuiButton.id == MsConstants.GUI_BUTTON_ID_BACK_TO_GAME)
		{
			this.mc.displayGuiScreen((GuiScreen)null);
			this.mc.setIngameFocus();
			if (MsClassReference.getConfiguration().hasChanged())
			{
				MsClassReference.getConfiguration().save();
				MsState.setStateFromConfigs(MsClassReference.getMsConfigs());
			}
		}
		else if (par1GuiButton.id == MsConstants.GUI_BUTTON_ID_LOAD_DEFAULTS)
		{
			MsClassReference.getMsConfigs().resetAllConfigs();
		}
		else if (isConfigButton(par1GuiButton))
		{
			int mode = this.getButtonModifier(); // 0..4 for 1/10/100/1000/10000 at a time
			MsClassReference.getMsConfigs().changeValue(par1GuiButton.id, mode, 0);
		}
	}

	protected void actionPerformedLeft(GuiButton par1GuiButton)
	{
		this.actionPerformed(par1GuiButton);
	}

	protected void actionPerformedRight(GuiButton par1GuiButton)
	{
		if (isConfigButton(par1GuiButton))
		{
			int mode = this.getButtonModifier(); // 0..4 for 1/10/100/1000/10000 at a time
			MsClassReference.getMsConfigs().changeValue(par1GuiButton.id, mode, 1);
		}
	}

	protected void actionPerformedMiddle(GuiButton par1GuiButton)
	{
		if (isConfigButton(par1GuiButton))
		{
			int mode = this.getButtonModifier(); // 0..4 for 1/10/100/1000/10000 at a time
			if (mode == 1) // CTRL held
			{
				MsClassReference.getMsConfigs().invertValue(par1GuiButton.id);
			}
			else if (mode == 0) // no modifiers held
			{
				MsClassReference.getMsConfigs().resetValue(par1GuiButton.id);
			}
		}
	}

	// Is this button one that changes the menu screen?
	protected boolean isMenuScreenButton(GuiButton btn)
	{
		int size = this.multishotScreenButtons.size();
		for (int i = 0; i < size; i++)
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
		if (btn.id == MsConstants.GUI_BUTTON_ID_SCREEN_GENERIC)
		{
			if (multishotScreenConfigsGeneric == null)
			{
				multishotScreenConfigsGeneric = new MsScreenGeneric(null);
			}
			this.mc.displayGuiScreen(multishotScreenConfigsGeneric);
		}
		else if (btn.id == MsConstants.GUI_BUTTON_ID_SCREEN_MOTION)
		{
			if (multishotScreenConfigsMotion == null)
			{
				multishotScreenConfigsMotion = new MsScreenMotion(null);
			}
			this.mc.displayGuiScreen(multishotScreenConfigsMotion);
		}
	}

	public GuiButton createGuiButton (int id, int x, int y, int w, int h)
	{
		String s;
		s = getButtonDisplayString(id);
		return new GuiButton(id, x, y, w, h, s);
	}

	public void updateGuiButtonString(GuiButton btn, int id)
	{
		btn.displayString = getButtonDisplayString(id);
	}

	public void updateGuiButton(GuiButton btn, int id)
	{
		this.updateGuiButtonString(btn, id);
	}

	public String getButtonDisplayString(int id)
	{
		String s;
		s = getButtonDisplayStringBase(id) + MsClassReference.getMsConfigs().getDisplayString(id);
		return s;
	}

	// FIXME Add localization support
	public String getButtonDisplayStringBase (int id)
	{
		String s = "";
		switch(id)
		{
			case MsConstants.GUI_BUTTON_ID_MULTISHOT_ENABLED:
				s = "Multishot Enabled: ";
				break;
			case MsConstants.GUI_BUTTON_ID_MOTION_ENABLED:
				s = "Motion Enabled: ";
				break;
			case MsConstants.GUI_BUTTON_ID_LOCK_CONTROLS:
				s = "Lock Controls: ";
				break;
			case MsConstants.GUI_BUTTON_ID_HIDE_GUI:
				s = "Hide Multishot GUI: ";
				break;
			case MsConstants.GUI_BUTTON_ID_INTERVAL:
				s = "Shot Interval: ";
				break;
			case MsConstants.GUI_BUTTON_ID_ZOOM:
				s = "Zoom: ";
				break;
			case MsConstants.GUI_BUTTON_ID_TIMER_SELECT:
				s = "Recording Timer: ";
				break;
			case MsConstants.GUI_BUTTON_ID_MOTION_MODE:
				s = "Mode: ";
				break;
			case MsConstants.GUI_BUTTON_ID_MOTION_X:
				s = "X motion: ";
				break;
			case MsConstants.GUI_BUTTON_ID_MOTION_Z:
				s = "Z motion: ";
				break;
			case MsConstants.GUI_BUTTON_ID_MOTION_Y:
				s = "Y motion: ";
				break;
			case MsConstants.GUI_BUTTON_ID_ROTATION_YAW:
				s = "Yaw Rotation: ";
				break;
			case MsConstants.GUI_BUTTON_ID_ROTATION_PITCH:
				s = "Pitch Rotation: ";
				break;
			case MsConstants.GUI_BUTTON_ID_MOTION_SPEED:
				s = "Speed: ";
				break;
			case MsConstants.GUI_BUTTON_ID_BROWSE:
				s = "Paste path";
				break;
			case MsConstants.GUI_BUTTON_ID_IMG_FORMAT:
				s = "";
				break;
			case MsConstants.GUI_BUTTON_ID_GUI_POSITION:
				s = "GUI: ";
				break;
			case MsConstants.GUI_BUTTON_ID_LOAD_DEFAULTS:
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
