package fi.dy.masa.minecraft.mods.multishot.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.libs.Constants;

@SideOnly(Side.CLIENT)
public abstract class MultishotScreenBase extends GuiScreen
{
	protected Minecraft mc = null;
	protected GuiScreen parent = null;
	protected GuiButton guiButtonScreenGeneric = null;
	protected GuiButton guiButtonScreenMotion = null;
	protected GuiButton guiButtonBackToGame = null;
	protected static MultishotScreenConfigsGeneric multishotScreenConfigsGeneric = null;
	protected static MultishotScreenConfigsMotion multishotScreenConfigsMotion = null;
	protected List<GuiButton> multishotScreenButtons = null;

	public MultishotScreenBase (GuiScreen parent)
	{
		this.parent = parent;
		this.mc = Minecraft.getMinecraft();
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
		this.guiButtonScreenGeneric = new GuiButton(Constants.GUI_BUTTON_ID_SCREEN_GENERIC,	x + 0, y + 0, 55, 20, "Generic");
		this.guiButtonScreenMotion = new GuiButton(Constants.GUI_BUTTON_ID_SCREEN_MOTION,	x + 65, y + 0, 55, 20, "Motion");
		this.guiButtonBackToGame = new GuiButton(Constants.GUI_BUTTON_ID_BACK_TO_GAME,	(this.width / 2) - 60, (this.height / 2) + 80, 120, 20, "Back To Game");
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
			}
			else
			{
				this.mc.displayGuiScreen(this.parent);
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
			System.out.println("Back To Game");
			this.mc.displayGuiScreen((GuiScreen)null);
			this.mc.setIngameFocus();
		}
	}

	// Is this button one that changes the menu screen?
	protected boolean isMenuScreenButton(GuiButton btn)
	{
		for (int i = 0; i < this.multishotScreenButtons.size(); i++)
		{
			if (this.multishotScreenButtons.get(i).id == btn.id)
			{
				//System.out.println("isMenuScreenButton() == true"); // FIXME debug
				return true;
			}
		}
		return false;
	}

	// Change the active menu screen
	protected void changeActiveScreen(GuiButton btn)
	{
		if (btn.id == Constants.GUI_BUTTON_ID_SCREEN_GENERIC)
		{
			System.out.println("MultishotScreenBase().changeActiveScreen() if btn.id == GENERIC"); // FIXME debug
			if (multishotScreenConfigsGeneric == null)
			{
				//System.out.println("MultishotScreenBase().changeActiveScreen() if generic == null"); // FIXME debug
				multishotScreenConfigsGeneric = new MultishotScreenConfigsGeneric(null);
			}
			this.mc.displayGuiScreen(multishotScreenConfigsGeneric);
		}
		else if (btn.id == Constants.GUI_BUTTON_ID_SCREEN_MOTION)
		{
			System.out.println("MultishotScreenBase().changeActiveScreen() if btn.id == MOTION"); // FIXME debug
			if (multishotScreenConfigsMotion == null)
			{
				//System.out.println("MultishotScreenBase().changeActiveScreen() if motion == null"); // FIXME debug
				multishotScreenConfigsMotion = new MultishotScreenConfigsMotion(null);
			}
			this.mc.displayGuiScreen(multishotScreenConfigsMotion);
		}
		System.out.println("-------------------"); // FIXME debug
	}
}
