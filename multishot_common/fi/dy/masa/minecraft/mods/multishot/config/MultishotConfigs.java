package fi.dy.masa.minecraft.mods.multishot.config;

import fi.dy.masa.minecraft.mods.multishot.libs.Constants;
import fi.dy.masa.minecraft.mods.multishot.libs.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumOS;

public class MultishotConfigs {
	private Minecraft mc;
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
		this.cfgMultishotSavePath = this.mc.mcDataDir.getAbsolutePath() + this.getPathSeparator() + Reference.MULTISHOT_BASE_DIR; // FIXME
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
		this.cfgMultishotSavePath = this.mc.mcDataDir.getAbsolutePath() + this.getPathSeparator() + Reference.MULTISHOT_BASE_DIR; // FIXME
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

	public String getPathSeparator ()
	{
		String s = "/"; // default, works for Linux, Unix, SOlaris(?) and MacOS
		if (Minecraft.getOs() == EnumOS.WINDOWS)
		{
			s = "\\";
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
}
