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
	Minecraft mc;
	protected GuiScreen parent = null;
	protected GuiButton guiButtonScreenGeneric = null;
	protected GuiButton guiButtonScreenMotion = null;
	List<GuiButton> multishotScreenButtons = new ArrayList<GuiButton>();
	protected static MultishotScreenConfigsGeneric multishotScreenConfigsGeneric = null;
	protected static MultishotScreenConfigsMotion multishotScreenConfigsMotion = null;

	long timeLast = 0;
	public MultishotScreenBase (GuiScreen parent)
	{
		this.parent	= parent;
		this.mc		= Minecraft.getMinecraft();
	}

	@Override
	public void drawScreen(int i, int j, float f)
	{
		drawDefaultBackground();	// The default dark background

		String s = "Multishot settings";
		int textWidth = this.fontRenderer.getStringWidth(s);
		this.fontRenderer.drawString(s, (this.width / 2) - (textWidth / 2), 5, 0xffffffff);
		super.drawScreen(i, j, f);

		// FIXME debug:
		//long timeNow;
		//timeNow = System.currentTimeMillis();
		//this.fontRenderer.drawString("time:" + timeNow, 190, this.height - 40, 0xffffffff);
		//this.fontRenderer.drawString("timediff:" + (timeNow - this.timeLast), 190, this.height - 30, 0xffffffff);
		//this.timeLast = timeNow;

		this.fontRenderer.drawString("drawScreen(" + i + ", " + j + ", " + f + ")", 10, this.height - 20, 0xffffffff);
		this.fontRenderer.drawString("w:" + this.width + " h:" + this.height, 190, this.height - 20, 0xffffffff);

		Gui.drawRect((this.width / 2) - 160, (this.height / 2) - 120, (this.width / 2) + 160, (this.height / 2) - 121, 0xffffffff);
		Gui.drawRect((this.width / 2) - 160, (this.height / 2) - 120, (this.width / 2) - 161, (this.height / 2) + 121, 0xffffffff);
		Gui.drawRect((this.width / 2) - 160, (this.height / 2) + 120, (this.width / 2) + 160, (this.height / 2) + 121, 0xffffffff);
		Gui.drawRect((this.width / 2) + 160, (this.height / 2) - 120, (this.width / 2) + 161, (this.height / 2) + 121, 0xffffffff);

	}

	@Override
	public void initGui()
	{
		buttonList.clear();
		multishotScreenButtons.clear();
		// Create the settings screen buttons
		System.out.println("MultishotScreenBase().initGUI()");
		this.guiButtonScreenGeneric = new GuiButton(Constants.GUI_BUTTON_ID_SCREEN_GENERIC,	10, 15, 50, 20, "Generic");
		this.guiButtonScreenMotion = new GuiButton(Constants.GUI_BUTTON_ID_SCREEN_MOTION,	60, 15, 50, 20, "Motion");
		multishotScreenButtons.add(this.guiButtonScreenGeneric);
		multishotScreenButtons.add(this.guiButtonScreenMotion);
		buttonList.add(this.guiButtonScreenGeneric);
		buttonList.add(this.guiButtonScreenMotion);
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

	// Changes the "active" (=disabled) menu screen button
	protected void changeActiveScreenButton(GuiButton btn)
	{
		GuiButton tmp;
		for (int i = 0; i < this.multishotScreenButtons.size(); i++)
		{
			tmp = this.multishotScreenButtons.get(i);
			System.out.println("btn.id: " + btn.id + " i:" + i + " tmp.id:" + tmp.id + " tmp.displayString:" + tmp.displayString + " tmp.enabled:" + tmp.enabled); // FIXME debug
			if (tmp.id == btn.id)
			{
				//System.out.println("MultishotScreenBase().changeActiveScreenButton() if tmp.id == btn.id"); // FIXME debug
				tmp.enabled = false;
			}
			else
			{
				//System.out.println("MultishotScreenBase().changeActiveScreenButton() else"); // FIXME debug
				tmp.enabled = true;
			}
		}
	}

	// Change the active menu screen
	protected void changeActiveScreen(GuiButton btn)
	{
		this.changeActiveScreenButton(btn);
		if (btn.id == Constants.GUI_BUTTON_ID_SCREEN_GENERIC)
		{
			//System.out.println("MultishotScreenBase().changeActiveScreen() if btn.id == GENERIC"); // FIXME debug
			if (multishotScreenConfigsGeneric == null)
			{
				System.out.println("MultishotScreenBase().changeActiveScreen() if generic == null"); // FIXME debug
				multishotScreenConfigsGeneric = new MultishotScreenConfigsGeneric(null);
			}
			this.mc.displayGuiScreen(multishotScreenConfigsGeneric);
		}
		else if (btn.id == Constants.GUI_BUTTON_ID_SCREEN_MOTION)
		{
			//System.out.println("MultishotScreenBase().changeActiveScreen() if btn.id == MOTION"); // FIXME debug
			if (multishotScreenConfigsMotion == null)
			{
				System.out.println("MultishotScreenBase().changeActiveScreen() if motion == null"); // FIXME debug
				multishotScreenConfigsMotion = new MultishotScreenConfigsMotion(null);
			}
			this.mc.displayGuiScreen(multishotScreenConfigsMotion);
		}
		System.out.println("-------------------");
	}
}
