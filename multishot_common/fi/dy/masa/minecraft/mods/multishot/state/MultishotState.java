package fi.dy.masa.minecraft.mods.multishot.state;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.config.MultishotConfigs;
import fi.dy.masa.minecraft.mods.multishot.output.MultishotThread;


@SideOnly(Side.CLIENT)
public class MultishotState {
	//private MultishotConfigs multishotConfigs = null;
	private static MultishotThread multishotThread = null;
	private static boolean stateRecording = false;
	private static boolean stateMotion = false;
	private static boolean statePaused = false;
	private static boolean stateGuiHidden = false;
	private static boolean stateControlsLocked = false;
	private static int shotCounter = 1;
	private static float normalFov = 0.0f;
/*
	public MultishotStatus (MultishotConfigs cfg)
	{
		this.multishotConfigs = cfg;
	}
*/
	public static void setStateFromConfigs(MultishotConfigs cfg)
	{
		setControlsLocked(cfg.getControlsLocked());
		setHideGui(cfg.getHideGui());
	}

	public static void setMultishotThread(MultishotThread t)
	{
		multishotThread = t;
	}

	public static MultishotThread getMultishotThread()
	{
		return multishotThread;
	}

	public static boolean getRecording()
	{
		return stateRecording;
	}

	public static boolean getMotion()
	{
		return stateMotion;
	}

	public static boolean getPaused()
	{
		return statePaused;
	}

	public static boolean getHideGui()
	{
		return stateGuiHidden;
	}

	public static boolean getControlsLocked()
	{
		return stateControlsLocked;
	}

	public static void setRecording(boolean par1)
	{
		stateRecording = par1;
	}

	public static void setMotion(boolean par1)
	{
		stateMotion = par1;
	}

	public static void setPaused(boolean par1)
	{
		statePaused = par1;
	}

	public static void setHideGui(boolean par1)
	{
		stateGuiHidden = par1;
	}

	public static void setControlsLocked(boolean par1)
	{
		stateControlsLocked = par1;
	}

	public static void toggleRecording()
	{
		stateRecording = ! stateRecording;
	}

	public static void toggleMotion()
	{
		stateMotion = ! stateMotion;
	}

	public static void togglePaused()
	{
		statePaused = ! statePaused;
	}

	public static void toggleHideGui()
	{
		stateGuiHidden = ! stateGuiHidden;
	}

	public static void toggleControlsLocked()
	{
		stateControlsLocked = ! stateControlsLocked;
	}

	public static void setShotCounter(int c)
	{
		shotCounter = c;
	}

	public static void resetShotCounter()
	{
		shotCounter = 1;
	}

	public static int getShotCounter()
	{
		return shotCounter;
	}

	public static void incrementShotCounter()
	{
		shotCounter++;
	}

	public static void storeFov(float fov)
	{
		normalFov = fov;
	}

	public static float getFov()
	{
		return normalFov;
	}
}
