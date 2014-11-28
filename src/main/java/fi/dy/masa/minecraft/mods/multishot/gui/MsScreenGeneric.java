package fi.dy.masa.minecraft.mods.multishot.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import fi.dy.masa.minecraft.mods.multishot.reference.MsConstants;
import fi.dy.masa.minecraft.mods.multishot.state.MsClassReference;

@SideOnly(Side.CLIENT)
public class MsScreenGeneric extends MsScreenBase
{
	private GuiButton buttonInterval = null;
	private GuiButton buttonZoom = null;
	private GuiButton buttonBrowse = null;
	private GuiButton buttonImgFormat = null;
	private GuiButton buttonMultishotEnabled = null;
	private GuiButton buttonMotionEnabled = null;
	private GuiButton buttonLockControls = null;
	private GuiButton buttonHideGui = null;
	private GuiButton buttonLoadDefaults = null;
	private GuiButton buttonGuiPosition = null;

	private GuiButton buttonTimerSelect = null;
	private GuiButton buttonTimeVideoHour = null;
	private GuiButton buttonTimeVideoMinute = null;
	private GuiButton buttonTimeVideoSecond = null;
	private GuiButton buttonTimeRealHour = null;
	private GuiButton buttonTimeRealMinute = null;
	private GuiButton buttonTimeRealSecond = null;
	private GuiButton buttonTimeNumShots = null;

	public MsScreenGeneric (GuiScreen parent)
	{
		super(parent);
		MsScreenGeneric.multishotScreenConfigsGeneric = this;
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		super.drawScreen(par1,  par2,  par3);
		int xl = (this.width / 2) - 130;
		int xr = (this.width / 2) + 5;
		int yc = (this.height / 2);
		//GL11.glPushMatrix();
		//float m = 0.5f;
		//GL11.glScalef(m, m, m);
		//int x = (int)(((double)xl + 2) / m);
		//int y = (int)(((double)yc - 2) / m);
		//this.fontRenderer.drawString("Video (@ 24fps):",x, y + 4, 0xffffffff);
		//GL11.glPopMatrix();
		this.fontRendererObj.drawString("Video @24:",	xl + 2,		yc - 0, 0xffffffff);
		this.fontRendererObj.drawString(":",			xl + 76,	yc - 0, 0xffffffff);
		this.fontRendererObj.drawString(":",			xl + 101,	yc - 0, 0xffffffff);
		this.fontRendererObj.drawString("Real time:",	xl + 2,		yc + 21, 0xffffffff);
		this.fontRendererObj.drawString(":",			xl + 76,	yc + 21, 0xffffffff);
		this.fontRendererObj.drawString(":",			xl + 101,	yc + 21, 0xffffffff);
		this.fontRendererObj.drawString("Shots:",		xl + 2,		yc + 42, 0xffffffff);
		// Print information about (estimated) output from a timed recording
		GL11.glPushMatrix();
		float m = 0.5f;
		GL11.glScalef(m, m, m);
		int x = (int)(((double)xr + 2) / m);
		int y = (int)(((double)yc + 17) / m);
		long num = MsClassReference.getMsConfigs().getActiveTimerNumShots();
		this.fontRendererObj.drawString("Screenshots: " + num,	x, y, 0xffffffff);
		long size = num * 1024L * 1024L; // Estimate at 1 MB per screenshot
		this.fontRendererObj.drawString("Size estimate: " + this.formatByteSize(size) + " (@ 1MB/shot)", x, y + 10, 0xffffffff);
		x = (int)(((double)xl + 2) / m);
		y = (int)(((double)yc + 60) / m);
		this.fontRendererObj.drawString("Save path (copy to clipboard & right click the button to change):", x, y, 0xffffffff);
		String s = MsClassReference.getMsConfigs().getSavePath();
		if (s.length() < 65)
		{
			this.fontRendererObj.drawString(s, x, y + 12, 0xffffffff);
		}
		else if (s.length() < 130)
		{
			this.fontRendererObj.drawString(s.substring(0, 65), x, y + 12, 0xffffffff);
			this.fontRendererObj.drawString(s.substring(65, s.length()), x, y + 22, 0xffffffff);
		}
		else
		{
			this.fontRendererObj.drawString("What do you have the path set to?! Seems a bit long...", x, y + 12, 0xffffffff);
		}
		GL11.glPopMatrix();
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
		int yc = (this.height / 2);
		this.buttonInterval			= createGuiButton(MsConstants.GUI_BUTTON_ID_INTERVAL,			xl, yt + 0, 125, 20);
		this.buttonZoom				= createGuiButton(MsConstants.GUI_BUTTON_ID_ZOOM,				xl, yt + 23, 125, 20);
		this.buttonBrowse			= createGuiButton(MsConstants.GUI_BUTTON_ID_BROWSE,				xr + 45, yt + 132, 80, 20);
		this.buttonMultishotEnabled	= createGuiButton(MsConstants.GUI_BUTTON_ID_MULTISHOT_ENABLED,	xr, yt + 0, 125, 20);
		this.buttonMotionEnabled	= createGuiButton(MsConstants.GUI_BUTTON_ID_MOTION_ENABLED,		xr, yt + 23, 125, 20);
		this.buttonLockControls		= createGuiButton(MsConstants.GUI_BUTTON_ID_LOCK_CONTROLS,		xr, yt + 46, 125, 20);
		this.buttonHideGui			= createGuiButton(MsConstants.GUI_BUTTON_ID_HIDE_GUI,			xr, yt + 69, 125, 20);
		this.buttonImgFormat		= createGuiButton(MsConstants.GUI_BUTTON_ID_IMG_FORMAT,			xr, yc + 36, 45, 20);
		this.buttonLoadDefaults		= createGuiButton(MsConstants.GUI_BUTTON_ID_LOAD_DEFAULTS,		xr + 45, yc + 36, 80, 20);
		this.buttonGuiPosition		= createGuiButton(MsConstants.GUI_BUTTON_ID_GUI_POSITION,		xr + 36, yt - 25, 88, 20);

		this.buttonTimerSelect		= createGuiButton(MsConstants.GUI_BUTTON_ID_TIMER_SELECT,		xl + 0, yc - 29, 125, 20);
		this.buttonTimeVideoHour	= createGuiButton(MsConstants.GUI_BUTTON_ID_TIME_VIDEO_HOUR,	xl + 54, yc - 6, 20, 20);
		this.buttonTimeVideoMinute	= createGuiButton(MsConstants.GUI_BUTTON_ID_TIME_VIDEO_MINUTE,	xl + 79, yc - 6, 20, 20);
		this.buttonTimeVideoSecond	= createGuiButton(MsConstants.GUI_BUTTON_ID_TIME_VIDEO_SECOND,	xl + 104, yc - 6, 20, 20);
		this.buttonTimeRealHour		= createGuiButton(MsConstants.GUI_BUTTON_ID_TIME_REAL_HOUR,		xl + 54, yc + 15, 20, 20);
		this.buttonTimeRealMinute	= createGuiButton(MsConstants.GUI_BUTTON_ID_TIME_REAL_MINUTE,	xl + 79, yc + 15, 20, 20);
		this.buttonTimeRealSecond	= createGuiButton(MsConstants.GUI_BUTTON_ID_TIME_REAL_SECOND,	xl + 104, yc + 15, 20, 20);
		this.buttonTimeNumShots		= createGuiButton(MsConstants.GUI_BUTTON_ID_TIME_NUM_SHOTS,		xl + 54, yc + 36, 71, 20);

		buttonList.add(this.buttonInterval);
		buttonList.add(this.buttonZoom);
		buttonList.add(this.buttonBrowse);
		buttonList.add(this.buttonLoadDefaults);
		buttonList.add(this.buttonMultishotEnabled);
		buttonList.add(this.buttonMotionEnabled);
		buttonList.add(this.buttonLockControls);
		buttonList.add(this.buttonHideGui);
		buttonList.add(this.buttonImgFormat);
		buttonList.add(this.buttonGuiPosition);

		buttonList.add(this.buttonTimerSelect);
		buttonList.add(this.buttonTimeVideoHour);
		buttonList.add(this.buttonTimeVideoMinute);
		buttonList.add(this.buttonTimeVideoSecond);
		buttonList.add(this.buttonTimeRealHour);
		buttonList.add(this.buttonTimeRealMinute);
		buttonList.add(this.buttonTimeRealSecond);
		buttonList.add(this.buttonTimeNumShots);
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();
		this.initGui();
	}
}
