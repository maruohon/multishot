package fi.dy.masa.minecraft.mods.multishot.config;

import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumOS;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.gui.MultishotScreenConfigsGeneric;
import fi.dy.masa.minecraft.mods.multishot.libs.Constants;
import fi.dy.masa.minecraft.mods.multishot.libs.Reference;

@SideOnly(Side.CLIENT)
public class MultishotConfigs {
	private Minecraft mc;
	private Configuration configuration = null;
	private static MultishotConfigs multishotConfigs;
	private boolean cfgMultishotEnabled = false;
	private boolean cfgMotionEnabled = false;
	private boolean cfgLockControls = false;
	private boolean cfgHideGui = false;
	private int cfgGuiPosition = 0;
	private int cfgGuiOffsetX = 0;
	private int cfgGuiOffsetY = 0;
	private int cfgInterval = 0; // In 1/10 of a second
	private int cfgZoom = 0;
	private int cfgSelectedTimer = 0;
	private int cfgTimerVideo = 0; // In seconds
	private int cfgTimerRealTime = 0; // In seconds
	private int cfgTimerNumShots = 0;
	private int cfgImgFormat = 0;
	private int cfgMotionX = 0; // In mm/s
	private int cfgMotionZ = 0;
	private int cfgMotionY = 0;
	private int cfgRotationYaw = 0; // In 1/100th of a degree/s
	private int cfgRotationPitch = 0;
	private String cfgMultishotSavePath;

	public MultishotConfigs ()
	{
		this.mc = Minecraft.getMinecraft();
		multishotConfigs = this;
		this.cfgMultishotSavePath = this.getDefaultPath();
		this.fixPath();
	}

	public MultishotConfigs (Configuration cfg)
	{
		this();
		this.configuration = cfg;
	}

	private String getDefaultPath()
	{
		return this.mc.mcDataDir.getAbsolutePath().concat("/").concat(Reference.MULTISHOT_BASE_DIR);
	}

	private void fixPath()
	{
		if (Minecraft.getOs() == EnumOS.WINDOWS)
		{
			this.cfgMultishotSavePath = this.cfgMultishotSavePath.replace('/', '\\').replace("\\.\\", "\\").replace("\\\\", "\\");
		}
		else
		{
			this.cfgMultishotSavePath = this.cfgMultishotSavePath.replace('\\', '/').replace("/./", "/").replace("//", "/");
		}
	}

	public static MultishotConfigs getInstance()
	{
		return multishotConfigs;
	}

	// Read the values from the Forge Configuration handler
	public void readFromConfiguration()
	{
		this.cfgMultishotEnabled	= this.configuration.get("general", "multishotenabled", false, "Multishot enabled override, disables the Multishot hotkey").getBoolean(this.cfgMultishotEnabled);
		this.cfgMotionEnabled		= this.configuration.get("general", "motionenabled", false, "Motion enabled override, disables the Motion hotkey").getBoolean(this.cfgMotionEnabled);
		this.cfgLockControls		= this.configuration.get("general", "lockcontrols", false, "Lock the mouse and keyboard controls while in Multishot mode").getBoolean(this.cfgLockControls);
		this.cfgHideGui				= this.configuration.get("general", "hidegui", false, "Hide the Multishot GUI (don't display the icons or save messages)").getBoolean(this.cfgHideGui);
		this.cfgGuiPosition			= this.configuration.get("general", "guiposition", 0, "Multishot GUI position (0 = Top Right, 1 = Bottom Right, 2 = Bottom Left, 3 = Top Left)").getInt(this.cfgGuiPosition);
		this.cfgGuiOffsetX			= this.configuration.get("general", "guioffsetx", 0, "Multishot GUI horizontal offset").getInt(this.cfgGuiOffsetX);
		this.cfgGuiOffsetY			= this.configuration.get("general", "guioffsety", 0, "Multishot GUI vertical offset").getInt(this.cfgGuiOffsetY);
		this.cfgInterval			= this.configuration.get("general", "interval", 0, "Time between screenshots, in 0.1 seconds").getInt(this.cfgInterval);
		this.cfgZoom				= this.configuration.get("general", "zoom", 0, "Zoom factor while in Multishot mode").getInt(this.cfgZoom);
		this.cfgSelectedTimer		= this.configuration.get("general", "timertype", 0, "Timer type (0 = OFF, 1 = Video time, 2 = Real time, 3 = Number of shots)").getInt(this.cfgSelectedTimer);
		this.cfgTimerVideo			= this.configuration.get("general", "timervideo", 0, "Timer length in video time, in seconds").getInt(this.cfgTimerVideo);
		this.cfgTimerRealTime		= this.configuration.get("general", "timerreal", 0, "Timer length in real time, in seconds").getInt(this.cfgTimerRealTime);
		this.cfgTimerNumShots		= this.configuration.get("general", "timershots", 0, "Timer length in number of screenshots").getInt(this.cfgTimerNumShots);
		this.cfgImgFormat			= this.configuration.get("general", "imgformat", 0, "Screenshot image format (0 = PNG, 1 = JPG with quality 75, 2 = JPG @ 80, 3 = JPG @ 85, 4 = JPG @ 90, 5 = JPG @ 95)").getInt(this.cfgImgFormat);
		this.cfgMultishotSavePath	= this.configuration.get("general", "savepath", "multishot", "The directory where the screenshots will be saved").getString();
		this.cfgMotionX					= this.configuration.get("motion", "motionx", 0, "Motion speed along the x-axis in the Linear mode, in mm/s (=1/1000th of a block)").getInt(this.cfgMotionX);
		this.cfgMotionZ					= this.configuration.get("motion", "motionz", 0, "Motion speed along the z-axis in the Linear mode, in mm/s (=1/1000th of a block)").getInt(this.cfgMotionZ);
		this.cfgMotionY					= this.configuration.get("motion", "motiony", 0, "Motion speed along the y-axis in the Linear mode, in mm/s (=1/1000th of a block)").getInt(this.cfgMotionY);
		this.cfgRotationYaw				= this.configuration.get("motion", "rotationyaw", 0, "Yaw rotation speed, in 1/100th of a degree per second").getInt(this.cfgRotationYaw);
		this.cfgRotationPitch			= this.configuration.get("motion", "rotationpitch", 0, "Pitch rotation speed, in 1/100th of a degree per second").getInt(this.cfgRotationPitch);
		this.fixPath();
		this.validateConfigs();
	}

	// Write the values to the Forge Configuration handler
	public void writeToConfiguration()
	{
		this.configuration.get("general", "multishotenabled", false, "Multishot enabled override, disables the Multishot hotkey").set(this.cfgMultishotEnabled);
		this.configuration.get("general", "motionenabled", false, "Motion enabled override, disables the Motion hotkey").set(this.cfgMotionEnabled);
		this.configuration.get("general", "lockcontrols", false, "Lock the mouse and keyboard controls while in Multishot mode").set(this.cfgLockControls);
		this.configuration.get("general", "hidegui", false, "Hide the Multishot GUI (don't display anything while taking screenshots)").set(this.cfgHideGui);
		this.configuration.get("general", "guiposition", 0, "Multishot GUI position (0 = Top Right, 1 = Bottom Right, 2 = Bottom Left, 3 = Top Left)").set(this.cfgGuiPosition);
		this.configuration.get("general", "guioffsetx", 0, "Multishot GUI horizontal offset").set(this.cfgGuiOffsetX);
		this.configuration.get("general", "guioffsety", 0, "Multishot GUI vertical offset").set(this.cfgGuiOffsetY);
		this.configuration.get("general", "interval", 0, "Time between screenshots, in 0.1 seconds").set(this.cfgInterval);
		this.configuration.get("general", "zoom", 0, "Zoom factor while in Multishot mode").set(this.cfgZoom);
		this.configuration.get("general", "timertype", 0, "Timer type (0 = OFF, 1 = Video time, 2 = Real time, 3 = Number of shots)").set(this.cfgSelectedTimer);
		this.configuration.get("general", "timervideo", 0, "Timer length in video time, in seconds").set(this.cfgTimerVideo);
		this.configuration.get("general", "timerreal", 0, "Timer length in real time, in seconds").set(this.cfgTimerRealTime);
		this.configuration.get("general", "timershots", 0, "Timer length in number of screenshots").set(this.cfgTimerNumShots);
		this.configuration.get("general", "imgformat", 0, "Screenshot image format (0 = PNG, 1 = JPG with quality 75, 2 = JPG @ 80, 3 = JPG @ 85, 4 = JPG @ 90, 5 = JPG @ 95)").set(this.cfgImgFormat);
		this.configuration.get("general", "savepath", "multishot", "The directory where the screenshots will be saved").set(this.cfgMultishotSavePath);
		this.configuration.get("motion", "motionx", 0, "Motion speed along the x-axis in the Linear mode, in mm/s (=1/1000th of a block)").set(this.cfgMotionX);
		this.configuration.get("motion", "motionz", 0, "Motion speed along the z-axis in the Linear mode, in mm/s (=1/1000th of a block)").set(this.cfgMotionZ);
		this.configuration.get("motion", "motiony", 0, "Motion speed along the y-axis in the Linear mode, in mm/s (=1/1000th of a block)").set(this.cfgMotionY);
		this.configuration.get("motion", "rotationyaw", 0, "Yaw rotation speed, in 1/100th of a degree per second").set(this.cfgRotationYaw);
		this.configuration.get("motion", "rotationpitch", 0, "Pitch rotation speed, in 1/100th of a degree per second").set(this.cfgRotationPitch);
		this.fixPath();
	}

	public void validateConfigs()
	{
		if (this.cfgGuiPosition < 0 || this.cfgGuiPosition > 3) { this.cfgGuiPosition = 0; } // Multishot GUI position (0 = Top Right, 1 = Bottom Right, 2 = Bottom Left, 3 = Top Left)
		if (this.cfgGuiOffsetX < -500 || this.cfgGuiOffsetX > 500) { this.cfgGuiOffsetX = 0; } // Limit the offsets somewhat
		if (this.cfgGuiOffsetY < -500 || this.cfgGuiOffsetY > 500) { this.cfgGuiOffsetY = 0; }
		if (this.cfgInterval < 0) { this.cfgInterval = 0; } // Negative intervals are not allowed, doh
		if (this.cfgZoom < 0 || this.cfgZoom > 100) { this.cfgZoom = 0; }
		if (this.cfgSelectedTimer < 0 || this.cfgSelectedTimer > 3) { this.cfgSelectedTimer = 0; } // Timer type (0 = OFF, 1 = Video time, 2 = Real time, 3 = Number of shots)
		if (this.cfgTimerVideo < 0) { this.cfgTimerVideo = 0; }
		if (this.cfgTimerRealTime < 0) { this.cfgTimerRealTime = 0; }
		if (this.cfgTimerNumShots < 0) { this.cfgTimerNumShots = 0; }
		if (this.cfgImgFormat < 0 || this.cfgImgFormat > 5) { this.cfgImgFormat = 0; } // Screenshot image format (0 = PNG, 1 = JPG with quality 75, 2 = JPG @ 80, 3 = JPG @ 85, 4 = JPG @ 90, 5 = JPG @ 95)
		File dir = new File(this.cfgMultishotSavePath);
		if (dir.isDirectory() == false)
		{
			this.cfgMultishotSavePath = this.getDefaultPath();
		}
		dir = null;
		this.fixPath();
		this.writeToConfiguration();
	}

	public void resetAllConfigs()
	{
		this.cfgMultishotEnabled = false;
		this.cfgMotionEnabled = false;
		this.cfgLockControls = false;
		this.cfgHideGui = false;
		this.cfgGuiPosition = 0;
		// We don't reset the hidden configs here, that would probably get annoying
		this.cfgInterval = 0;
		this.cfgZoom = 0;
		this.cfgSelectedTimer = 0;
		this.cfgTimerVideo = 0;
		this.cfgTimerRealTime = 0;
		this.cfgTimerNumShots = 0;
		this.cfgImgFormat = 0;
		this.cfgMotionX = 0; // In mm/s
		this.cfgMotionZ = 0;
		this.cfgMotionY = 0;
		this.cfgRotationYaw = 0; // In 1/100th of a degree/s
		this.cfgRotationPitch = 0;
		this.cfgMultishotSavePath = this.getDefaultPath();
		this.fixPath();
		this.writeToConfiguration();
	}

	// Change a config value (mode 1: regular click without modifiers)
	public void changeValue(int id, int mode, int btn)
	{
		int increment = 1;
		if (btn == 1)
		{
			increment = -1;
		}
		if (mode == 1)
		{
			increment *= 10;
		}
		else if (mode == 2)
		{
			increment *= 100;
		}
		else if (mode == 3)
		{
			increment *= 1000;
		}
		switch(id)
		{
			case Constants.GUI_BUTTON_ID_MULTISHOT_ENABLED:
				this.cfgMultishotEnabled = ! this.cfgMultishotEnabled;
				break;
			case Constants.GUI_BUTTON_ID_MOTION_ENABLED:
				this.cfgMotionEnabled = ! this.cfgMotionEnabled;
				break;
			case Constants.GUI_BUTTON_ID_LOCK_CONTROLS:
				this.cfgLockControls = ! this.cfgLockControls;
				break;
			case Constants.GUI_BUTTON_ID_HIDE_GUI:
				this.cfgHideGui = ! this.cfgHideGui;
				break;
			case Constants.GUI_BUTTON_ID_INTERVAL:
				this.cfgInterval = this.normalizeInt(this.cfgInterval, increment, 0, 72000); // max 2h = 7200s
				break;
			case Constants.GUI_BUTTON_ID_ZOOM:
				this.cfgZoom = this.normalizeInt(this.cfgZoom, increment, 0, 100);
				break;
			case Constants.GUI_BUTTON_ID_TIMER_SELECT:
				if (increment > 0) { increment = 1; } else { increment = -1; }
				// 0 = Off, 1 = Video time, 2 = In-Game time, 3 = Number of shots
				this.cfgSelectedTimer = this.normalizeIntWrap(this.cfgSelectedTimer, increment, 0, 3);
				break;
			case Constants.GUI_BUTTON_ID_TIME_VIDEO_HOUR:
				increment *= 60;
			case Constants.GUI_BUTTON_ID_TIME_VIDEO_MINUTE:
				increment *= 60;
			case Constants.GUI_BUTTON_ID_TIME_VIDEO_SECOND:
				this.cfgTimerVideo = this.normalizeInt(this.cfgTimerVideo, increment, 0, 99 * 3600 + 59 * 60 + 59);
				break;
			case Constants.GUI_BUTTON_ID_TIME_REAL_HOUR:
				increment *= 60;
			case Constants.GUI_BUTTON_ID_TIME_REAL_MINUTE:
				increment *= 60;
			case Constants.GUI_BUTTON_ID_TIME_REAL_SECOND:
				this.cfgTimerRealTime = this.normalizeInt(this.cfgTimerRealTime, increment, 0, 99 * 3600 + 59 * 60 + 59);
				break;
			case Constants.GUI_BUTTON_ID_TIME_NUM_SHOTS:
				this.cfgTimerNumShots = this.normalizeInt(this.cfgTimerNumShots, increment, 0, 10000000);
				break;
			case Constants.GUI_BUTTON_ID_BROWSE: // FIXME We re-purpose the Browse button as a "Paste path from clipboard" button for now
				if (btn == 1) // with right click
				{
					this.cfgMultishotSavePath = MultishotScreenConfigsGeneric.getClipboardString();
					this.fixPath();
				}
				break;
			case Constants.GUI_BUTTON_ID_IMG_FORMAT:
				if (increment > 0) { increment = 1; } else { increment = -1; }
				// 0 = PNG, 1 = JPG with quality 75, 2 = JPG @ 80, 3 = JPG @ 85, 4 = JPG @ 90, 5 = JPG @ 95
				this.cfgImgFormat = this.normalizeIntWrap(this.cfgImgFormat, increment, 0, 5);
				break;
			case Constants.GUI_BUTTON_ID_GUI_POSITION:
				if (increment > 0) { increment = 1; } else { increment = -1; }
				// 0 = Top Right, 1 = Bottom Right, 2 = Bottom Left, 3 = Top Left
				this.cfgGuiPosition = this.normalizeIntWrap(this.cfgGuiPosition, increment, 0, 3);
				break;
			case Constants.GUI_BUTTON_ID_MOTION_X:
				this.cfgMotionX = this.normalizeInt(this.cfgMotionX, increment, -1000000, 1000000); // max 1000m/s :p
				break;
			case Constants.GUI_BUTTON_ID_MOTION_Z:
				this.cfgMotionZ = this.normalizeInt(this.cfgMotionZ, increment, -1000000, 1000000); // max 1000m/s :p
				break;
			case Constants.GUI_BUTTON_ID_MOTION_Y:
				this.cfgMotionY = this.normalizeInt(this.cfgMotionY, increment, -1000000, 1000000); // max 1000m/s :p
				break;
			case Constants.GUI_BUTTON_ID_ROTATION_YAW:
				this.cfgRotationYaw = this.normalizeInt(this.cfgRotationYaw, increment, -360000, 360000); // max 10 rotations/s :p
				break;
			case Constants.GUI_BUTTON_ID_ROTATION_PITCH:
				this.cfgRotationPitch = this.normalizeInt(this.cfgRotationPitch, increment, -360000, 360000); // max 10 rotations/s :p
				break;
			default:
				break;
		}
		this.writeToConfiguration();
	}

	// Reset a config's value
	public void resetValue(int id)
	{
		int tmp;
		switch(id)
		{
			case Constants.GUI_BUTTON_ID_MULTISHOT_ENABLED:
				this.cfgMultishotEnabled = false;
				break;
			case Constants.GUI_BUTTON_ID_MOTION_ENABLED:
				this.cfgMotionEnabled = false;
				break;
			case Constants.GUI_BUTTON_ID_LOCK_CONTROLS:
				this.cfgLockControls = false;
				break;
			case Constants.GUI_BUTTON_ID_HIDE_GUI:
				this.cfgHideGui = false;
				break;
			case Constants.GUI_BUTTON_ID_INTERVAL:
				this.cfgInterval = 0;
				break;
			case Constants.GUI_BUTTON_ID_ZOOM:
				this.cfgZoom = 0;
				break;
			case Constants.GUI_BUTTON_ID_TIMER_SELECT:
				this.cfgSelectedTimer = 0;
				break;
			case Constants.GUI_BUTTON_ID_TIME_VIDEO_HOUR:
				this.cfgTimerVideo = this.cfgTimerVideo % 3600;
				break;
			case Constants.GUI_BUTTON_ID_TIME_VIDEO_MINUTE:
				tmp = this.cfgTimerVideo - (this.cfgTimerVideo % 3600);
				this.cfgTimerVideo = tmp + this.cfgTimerVideo % 60;
				break;
			case Constants.GUI_BUTTON_ID_TIME_VIDEO_SECOND:
				this.cfgTimerVideo = this.cfgTimerVideo - (this.cfgTimerVideo % 60);
				break;
			case Constants.GUI_BUTTON_ID_TIME_REAL_HOUR:
				this.cfgTimerRealTime = this.cfgTimerRealTime % 3600;
				break;
			case Constants.GUI_BUTTON_ID_TIME_REAL_MINUTE:
				tmp = this.cfgTimerRealTime - (this.cfgTimerRealTime % 3600);
				this.cfgTimerRealTime = tmp + this.cfgTimerRealTime % 60;
				break;
			case Constants.GUI_BUTTON_ID_TIME_REAL_SECOND:
				this.cfgTimerRealTime = this.cfgTimerRealTime - (this.cfgTimerRealTime % 60);
				break;
			case Constants.GUI_BUTTON_ID_TIME_NUM_SHOTS:
				this.cfgTimerNumShots = 0;
				break;
			case Constants.GUI_BUTTON_ID_BROWSE:
				this.cfgMultishotSavePath = this.getDefaultPath();
				break;
			case Constants.GUI_BUTTON_ID_IMG_FORMAT:
				this.cfgImgFormat = 0;
				break;
			case Constants.GUI_BUTTON_ID_GUI_POSITION:
				this.cfgGuiPosition = 0;
				break;
			case Constants.GUI_BUTTON_ID_MOTION_X:
				this.cfgMotionX = 0;
				break;
			case Constants.GUI_BUTTON_ID_MOTION_Z:
				this.cfgMotionZ = 0;
				break;
			case Constants.GUI_BUTTON_ID_MOTION_Y:
				this.cfgMotionY = 0;
				break;
			case Constants.GUI_BUTTON_ID_ROTATION_YAW:
				this.cfgRotationYaw = 0;
				break;
			case Constants.GUI_BUTTON_ID_ROTATION_PITCH:
				this.cfgRotationPitch = 0;
				break;
			default:
				break;
		}
		this.writeToConfiguration();
	}

	private int normalizeInt (int val, int inc, int min, int max)
	{
		val += inc;
		if (val < min)
		{
			val = min;
		}
		else if (val > max)
		{
			val = max;
		}
		return val;
	}

	private int normalizeIntWrap (int val, int inc, int min, int max)
	{
		val += inc;
		if (val < min)
		{
			val = max;
		}
		else if (val > max)
		{
			val = min;
		}
		return val;
	}

	public String getDisplayString (int id)
	{
		String s = "";
		switch(id)
		{
			case Constants.GUI_BUTTON_ID_MULTISHOT_ENABLED:
				s = getDisplayStringBoolean(this.cfgMultishotEnabled);
				break;
			case Constants.GUI_BUTTON_ID_MOTION_ENABLED:
				s = getDisplayStringBoolean(this.cfgMotionEnabled);
				break;
			case Constants.GUI_BUTTON_ID_LOCK_CONTROLS:
				s = getDisplayStringBoolean(this.cfgLockControls);
				break;
			case Constants.GUI_BUTTON_ID_HIDE_GUI:
				s = getDisplayStringBoolean(this.cfgHideGui);
				break;
			case Constants.GUI_BUTTON_ID_INTERVAL:
				if (this.cfgInterval == 0) { s = "OFF"; }
				else { s = String.format("%.1fs", ((float)this.cfgInterval / 10)); }
				break;
			case Constants.GUI_BUTTON_ID_ZOOM:
				if (this.cfgZoom == 0) { s = "OFF"; }
				else { s = String.format("%.1fx", (float)cfgZoom / 10.0f); }
				break;
			case Constants.GUI_BUTTON_ID_TIMER_SELECT:
				if (this.cfgSelectedTimer == 0) { s = "OFF"; }
				else if (this.cfgSelectedTimer == 1) { s = "Video"; }
				else if (this.cfgSelectedTimer == 2) { s = "Real"; }
				else if (this.cfgSelectedTimer == 3) { s = "Shots"; }
				break;
			case Constants.GUI_BUTTON_ID_TIME_VIDEO_HOUR:
				s = String.format("%02d",  this.cfgTimerVideo / 3600);
				break;
			case Constants.GUI_BUTTON_ID_TIME_VIDEO_MINUTE:
				s = String.format("%02d",  (this.cfgTimerVideo % 3600) / 60);
				break;
			case Constants.GUI_BUTTON_ID_TIME_VIDEO_SECOND:
				s = String.format("%02d",  this.cfgTimerVideo % 60);
				break;
			case Constants.GUI_BUTTON_ID_TIME_REAL_HOUR:
				s = String.format("%02d",  this.cfgTimerRealTime / 3600);
				break;
			case Constants.GUI_BUTTON_ID_TIME_REAL_MINUTE:
				s = String.format("%02d",  (this.cfgTimerRealTime % 3600) / 60);
				break;
			case Constants.GUI_BUTTON_ID_TIME_REAL_SECOND:
				s = String.format("%02d",  this.cfgTimerRealTime % 60);
				break;
			case Constants.GUI_BUTTON_ID_TIME_NUM_SHOTS:
				s = String.format("%010d", this.cfgTimerNumShots);
				break;
			case Constants.GUI_BUTTON_ID_IMG_FORMAT:
				if (this.cfgImgFormat == 0) { s = "PNG"; }
				else if (this.cfgImgFormat == 1) { s = "JPG, 75"; }
				else if (this.cfgImgFormat == 2) { s = "JPG, 80"; }
				else if (this.cfgImgFormat == 3) { s = "JPG, 85"; }
				else if (this.cfgImgFormat == 4) { s = "JPG, 90"; }
				else if (this.cfgImgFormat == 5) { s = "JPG, 95"; }
				break;
			case Constants.GUI_BUTTON_ID_GUI_POSITION:
				if (this.cfgGuiPosition == 0) { s = "Top Right"; }
				else if (this.cfgGuiPosition == 1) { s = "Bottom Right"; }
				else if (this.cfgGuiPosition == 2) { s = "Bottom Left"; }
				else if (this.cfgGuiPosition == 3) { s = "Top Left"; }
				break;
			case Constants.GUI_BUTTON_ID_MOTION_X:
				s = getDisplayStringSpeed(this.cfgMotionX);
				break;
			case Constants.GUI_BUTTON_ID_MOTION_Z:
				s = getDisplayStringSpeed(this.cfgMotionZ);
				break;
			case Constants.GUI_BUTTON_ID_MOTION_Y:
				s = getDisplayStringSpeed(this.cfgMotionY);
				break;
			case Constants.GUI_BUTTON_ID_ROTATION_YAW:
				s = getDisplayStringRotation(this.cfgRotationYaw);
				break;
			case Constants.GUI_BUTTON_ID_ROTATION_PITCH:
				s = getDisplayStringRotation(this.cfgRotationPitch);
				break;
			case Constants.GUI_FIELD_ID_SAVE_PATH:
				s = this.cfgMultishotSavePath;
					break;
			default:
				break;
		}
		return s;
	}

	public String getDisplayStringBoolean (boolean val)
	{
		if (val == true)
		{
			return "ON";
		}
		return "OFF";
	}

	public String getDisplayStringSpeed (int val)
	{
		String s;
		if (val == 0)
		{
			s = "OFF";
		}
		else
		{
			s = String.format("%.3f m/s", ((float)val / 1000));
		}
		return s;
	}

	public String getDisplayStringRotation (int val)
	{
		String s;
		if (val == 0)
		{
			s = "OFF";
		}
		else
		{
			s = String.format("%.2f\u00b0/s", ((float)val / 100));
		}
		return s;
	}

	public int getActiveTimer()
	{
		return this.cfgSelectedTimer;
	}

	public int getActiveTimerNumShots()
	{
		if (this.cfgInterval > 0)
		{
			int t = this.getActiveTimer();
			if (t == 1)	// Video time
			{
				return (this.cfgTimerVideo * 24); // Assume 24 FPS video
			}
			else if (t == 2) // Real time
			{
				return (this.cfgTimerRealTime * 10) / this.cfgInterval; // Interval is in 1/10 of a second
			}
			else if (t == 3) // Number of shots
			{
				return this.cfgTimerNumShots;
			}
		}
		return 0;
	}

	public boolean getMultishotEnabled()
	{
		return this.cfgMultishotEnabled;
	}

	public boolean getMotionEnabled()
	{
		return this.cfgMotionEnabled;
	}

	public boolean getControlsLocked()
	{
		return this.cfgLockControls;
	}

	public boolean getHideGui()
	{
		return this.cfgHideGui;
	}

	public int getGuiPosition()
	{
		return this.cfgGuiPosition;
	}

	public int getGuiOffsetX()
	{
		return this.cfgGuiOffsetX;
	}

	public int getGuiOffsetY()
	{
		return this.cfgGuiOffsetY;
	}

	public int getZoom()
	{
		return this.cfgZoom;
	}

	public String getSavePath()
	{
		return this.cfgMultishotSavePath;
	}

	public int getImgFormat()
	{
		return this.cfgImgFormat;
	}

	public int getInterval()
	{
		return this.cfgInterval;
	}

	public double getMotionX()
	{
		return (double)this.cfgMotionX / 1000.0 / 20.0;
	}

	public double getMotionZ()
	{
		return (double)this.cfgMotionZ / 1000.0 / 20.0;
	}

	public double getMotionY()
	{
		return (double)this.cfgMotionY / 1000.0 / 20.0;
	}

	public float getRotationYaw()
	{
		return (float)this.cfgRotationYaw / 100.0f / 20.0f;
	}

	public float getRotationPitch()
	{
		return (float)this.cfgRotationPitch / 100.0f / 20.0f;
	}
}
