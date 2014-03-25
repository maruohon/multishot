package fi.dy.masa.minecraft.mods.multishot.config;

import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.Configuration;
import fi.dy.masa.minecraft.mods.multishot.libs.Constants;
import fi.dy.masa.minecraft.mods.multishot.libs.Reference;

public class MultishotConfigs {
	private Minecraft mc;
	private Configuration configuration = null;
	private static MultishotConfigs multishotConfigs;
	private boolean cfgMultishotEnabled = false;
	private boolean cfgMotionEnabled = false;
	private boolean cfgLockControls = false;
	private boolean cfgHideGui = false;
	private int cfgInterval = 0; // In 1/10 of a second
	private int cfgZoom = 0;
	private int cfgTimerSelect = 0;
	private int cfgTimerVideo = 0; // In seconds
	private int cfgTimerRealTime = 0; // In seconds
	private int cfgTimerNumShots = 0;
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
		this.cfgMultishotSavePath = this.mc.mcDataDir.getAbsolutePath().concat(File.pathSeparator).concat(Reference.MULTISHOT_BASE_DIR);
		this.cfgMultishotSavePath = this.cfgMultishotSavePath.replace(File.separatorChar, '/').replace("/./", "/");
	}

	public MultishotConfigs (Configuration cfg)
	{
		this();
		this.configuration = cfg;
	}

	public static MultishotConfigs getInstance()
	{
		return multishotConfigs;
	}

	// Read the values from the Forge Configuration handler
	public void readFromConfiguration()
	{
		this.cfgMultishotEnabled = this.configuration.get("general", "multishotenabled", false, "Multishot enabled override, disables the Multishot hotkey").getBoolean(this.cfgMultishotEnabled);
		this.cfgMotionEnabled = this.configuration.get("general", "motionenabled", false, "Motion enabled override, disables the Motion hotkey").getBoolean(this.cfgMotionEnabled);
		this.cfgLockControls = this.configuration.get("general", "lockcontrols", false, "Lock the mouse and keyboard controls while in Multishot mode").getBoolean(this.cfgLockControls);
		this.cfgHideGui = this.configuration.get("general", "hidegui", false, "Hide the Multishot GUI (don't display anything while taking screenshots)").getBoolean(this.cfgHideGui);
		this.cfgInterval = this.configuration.get("general", "interval", 0, "Time between screenshots, in 0.1 seconds").getInt(this.cfgInterval);
		this.cfgZoom = this.configuration.get("general", "zoom", 0, "Zoom factor while in Multishot mode").getInt(this.cfgZoom);
		this.cfgTimerSelect = this.configuration.get("general", "timertype", 0, "Timer type (0 = OFF, 1 = Video time, 2 = Real time, 3 = Number of shots)").getInt(this.cfgTimerSelect);
		this.cfgTimerVideo = this.configuration.get("general", "timervideo", 0, "Timer length in video time, in seconds").getInt(this.cfgTimerVideo);
		this.cfgTimerRealTime = this.configuration.get("general", "timerreal", 0, "Timer length in real time, in seconds").getInt(this.cfgTimerRealTime);
		this.cfgTimerNumShots = this.configuration.get("general", "timershots", 0, "Timer length in number of screenshots").getInt(this.cfgTimerNumShots);
		this.cfgMotionX = this.configuration.get("general", "motionx", 0, "Motion speed along the x-axis, in mm/s (=1/1000th of a block)").getInt(this.cfgMotionX);
		this.cfgMotionZ = this.configuration.get("general", "motionz", 0, "Motion speed along the z-axis, in mm/s (=1/1000th of a block)").getInt(this.cfgMotionZ);
		this.cfgMotionY = this.configuration.get("general", "motiony", 0, "Motion speed along the y-axis, in mm/s (=1/1000th of a block)").getInt(this.cfgMotionY);
		this.cfgRotationYaw = this.configuration.get("general", "rotationyaw", 0, "Yaw rotation speed, in 1/100th of a degree per second").getInt(this.cfgRotationYaw);
		this.cfgRotationPitch = this.configuration.get("general", "rotationpitch", 0, "Pitch rotation speed, in 1/100th of a degree per second").getInt(this.cfgRotationPitch);
		this.cfgMultishotSavePath = this.configuration.get("general", "savepath", "multishot", "The directory where the screenshots will be saved").getString();
		this.cfgMultishotSavePath = this.cfgMultishotSavePath.replace(File.separatorChar, '/').replace("/./", "/");
	}

	// Write the values to the Forge Configuration handler
	public void writeToConfiguration()
	{
		this.configuration.get("general", "multishotenabled", false, "Multishot enabled override, disables the Multishot hotkey").set(this.cfgMultishotEnabled);
		this.configuration.get("general", "motionenabled", false, "Motion enabled override, disables the Motion hotkey").set(this.cfgMotionEnabled);
		this.configuration.get("general", "lockcontrols", false, "Lock the mouse and keyboard controls while in Multishot mode").set(this.cfgLockControls);
		this.configuration.get("general", "hidegui", false, "Hide the Multishot GUI (don't display anything while taking screenshots)").set(this.cfgHideGui);
		this.configuration.get("general", "interval", 0, "Time between screenshots, in 0.1 seconds").set(this.cfgInterval);
		this.configuration.get("general", "zoom", 0, "Zoom factor while in Multishot mode").set(this.cfgZoom);
		this.configuration.get("general", "timertype", 0, "Timer type (0 = OFF, 1 = Video time, 2 = Real time, 3 = Number of shots)").set(this.cfgTimerSelect);
		this.configuration.get("general", "timervideo", 0, "Timer length in video time, in seconds").set(this.cfgTimerVideo);
		this.configuration.get("general", "timerreal", 0, "Timer length in real time, in seconds").set(this.cfgTimerRealTime);
		this.configuration.get("general", "timershots", 0, "Timer length in number of screenshots").set(this.cfgTimerNumShots);
		this.configuration.get("general", "motionx", 0, "Motion speed along the x-axis, in mm/s (=1/1000th of a block)").set(this.cfgMotionX);
		this.configuration.get("general", "motionz", 0, "Motion speed along the z-axis, in mm/s (=1/1000th of a block)").set(this.cfgMotionZ);
		this.configuration.get("general", "motiony", 0, "Motion speed along the y-axis, in mm/s (=1/1000th of a block)").set(this.cfgMotionY);
		this.configuration.get("general", "rotationyaw", 0, "Yaw rotation speed, in 1/100th of a degree per second").set(this.cfgRotationYaw);
		this.configuration.get("general", "rotationpitch", 0, "Pitch rotation speed, in 1/100th of a degree per second").set(this.cfgRotationPitch);
		this.cfgMultishotSavePath = this.cfgMultishotSavePath.replace(File.separatorChar, '/').replace("/./", "/");
		this.configuration.get("general", "savepath", "multishot", "The directory where the screenshots will be saved").set(this.cfgMultishotSavePath);
	}

	public void resetAllConfigs()
	{
		this.cfgMultishotEnabled = false;
		this.cfgMotionEnabled = false;
		this.cfgLockControls = false;
		this.cfgHideGui = false;
		this.cfgInterval = 0;
		this.cfgZoom = 0;
		this.cfgTimerSelect = 0;
		this.cfgTimerVideo = 0;
		this.cfgTimerRealTime = 0;
		this.cfgTimerNumShots = 0;
		this.cfgMotionX = 0; // In mm/s
		this.cfgMotionZ = 0;
		this.cfgMotionY = 0;
		this.cfgRotationYaw = 0; // In 1/100th of a degree/s
		this.cfgRotationPitch = 0;
		//this.cfgRecordDurationFrames = 0;
		//this.cfgRecordDurationSeconds = 0;
		this.cfgMultishotSavePath = this.mc.mcDataDir.getAbsolutePath().concat(File.pathSeparator).concat(Reference.MULTISHOT_BASE_DIR);
		this.cfgMultishotSavePath = this.cfgMultishotSavePath.replace(File.pathSeparator, "/").replace("/./", "/");
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
				this.cfgInterval = this.normalise(this.cfgInterval, increment, 0, 72000); // max 2h = 7200s
				break;
			case Constants.GUI_BUTTON_ID_ZOOM:
				this.cfgZoom = this.normalise(this.cfgZoom, increment, 0, 100);
				break;
			case Constants.GUI_BUTTON_ID_TIMER_SELECT:
				// 0 = Off, 1 = Video time, 2 = In-Game time, 3 = Number of shots
				if (increment > 0)
				{
					if (++this.cfgTimerSelect > 3)
					{
						this.cfgTimerSelect = 0;
					}
				}
				else
				{
					if (--this.cfgTimerSelect < 0)
					{
						this.cfgTimerSelect = 3;
					}
				}
				break;
			case Constants.GUI_BUTTON_ID_TIME_VIDEO_HOUR:
				increment *= 60;
			case Constants.GUI_BUTTON_ID_TIME_VIDEO_MINUTE:
				increment *= 60;
			case Constants.GUI_BUTTON_ID_TIME_VIDEO_SECOND:
				this.cfgTimerVideo = this.normalise(this.cfgTimerVideo, increment, 0, 99 * 3600 + 59 * 60 + 59);
				break;
			case Constants.GUI_BUTTON_ID_TIME_REAL_HOUR:
				increment *= 60;
			case Constants.GUI_BUTTON_ID_TIME_REAL_MINUTE:
				increment *= 60;
			case Constants.GUI_BUTTON_ID_TIME_REAL_SECOND:
				this.cfgTimerRealTime = this.normalise(this.cfgTimerRealTime, increment, 0, 99 * 3600 + 59 * 60 + 59);
				break;
			case Constants.GUI_BUTTON_ID_TIME_NUM_SHOTS:
				this.cfgTimerNumShots = this.normalise(this.cfgTimerNumShots, increment, 0, 10000000);
				break;
			case Constants.GUI_BUTTON_ID_MOTION_X:
				this.cfgMotionX = this.normalise(this.cfgMotionX, increment, -1000000, 1000000); // max 1000m/s :p
				break;
			case Constants.GUI_BUTTON_ID_MOTION_Z:
				this.cfgMotionZ = this.normalise(this.cfgMotionZ, increment, -1000000, 1000000); // max 1000m/s :p
				break;
			case Constants.GUI_BUTTON_ID_MOTION_Y:
				this.cfgMotionY = this.normalise(this.cfgMotionY, increment, -1000000, 1000000); // max 1000m/s :p
				break;
			case Constants.GUI_BUTTON_ID_ROTATION_YAW:
				this.cfgRotationYaw = this.normalise(this.cfgRotationYaw, increment, -360000, 360000); // max 10 rotations/s :p
				break;
			case Constants.GUI_BUTTON_ID_ROTATION_PITCH:
				this.cfgRotationPitch = this.normalise(this.cfgRotationPitch, increment, -360000, 360000); // max 10 rotations/s :p
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
				this.cfgTimerSelect = 0;
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

	private int normalise (int val, int inc, int min, int max)
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
				if (this.cfgInterval == 0)
				{
					s = "OFF";
				}
				else
				{
					s = String.format("%.1fs", ((float)this.cfgInterval / 10));
				}
				break;
			case Constants.GUI_BUTTON_ID_ZOOM:
				if (this.cfgZoom == 0)
				{
					s = "OFF";
				}
				else
				{
					s = cfgZoom + "x";
				}
				break;
			case Constants.GUI_BUTTON_ID_TIMER_SELECT:
				if (this.cfgTimerSelect == 0) { s = "OFF"; }
				else if (this.cfgTimerSelect == 1) { s = "Video"; }
				else if (this.cfgTimerSelect == 2) { s = "Real"; }
				else if (this.cfgTimerSelect == 3) { s = "Shots"; }
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
				s = String.format("%d", this.cfgTimerNumShots);
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
			s = String.format("%.3fm/s", ((float)val / 1000));
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
		return this.cfgTimerSelect;
	}

	public int getActiveTimerNumShots()
	{
		int t = this.getActiveTimer();
		if (t == 1)	// Video time
		{
			return (this.cfgTimerVideo * 24); // Assume 24 FPS video
		}
		else if (t == 2) // Real time
		{
			if (this.cfgInterval > 0)
			{
				return (this.cfgTimerRealTime * 10) / this.cfgInterval; // Interval is in 1/10 of a second
			}
		}
		else if (t == 3) // Number of shots
		{
			return this.cfgTimerNumShots;
		}
		return 0;
	}

	public String getSavePath()
	{
		return this.cfgMultishotSavePath;
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
