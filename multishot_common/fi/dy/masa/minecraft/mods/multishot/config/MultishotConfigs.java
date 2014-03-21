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
	private int cfgInterval = 0;
	private int cfgZoom = 0;
	private int cfgMotionX = 0; // In mm/s
	private int cfgMotionZ = 0;
	private int cfgMotionY = 0;
	private int cfgRotationYaw = 0; // In 1/100th of a degree/s
	private int cfgRotationPitch = 0;
	//private int cfgRecordDurationFrames = 0;
	//private int cfgRecordDurationSeconds = 0;
	private String cfgMultishotSavePath;

	public MultishotConfigs ()
	{
		this.mc = Minecraft.getMinecraft();
		this.cfgMultishotSavePath = this.mc.mcDataDir.getAbsolutePath() + this.getPathSeparator() + Reference.MULTISHOT_BASE_DIR;
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
}
