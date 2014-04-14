package fi.dy.masa.minecraft.mods.multishot.state;

import net.minecraft.client.Minecraft;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.config.MsConfigs;
import fi.dy.masa.minecraft.mods.multishot.gui.MsGui;
import fi.dy.masa.minecraft.mods.multishot.gui.MsScreenGeneric;
import fi.dy.masa.minecraft.mods.multishot.gui.MsScreenMotion;
import fi.dy.masa.minecraft.mods.multishot.handlers.MsKeyHandler;
import fi.dy.masa.minecraft.mods.multishot.handlers.MsPlayerTickHandler;
import fi.dy.masa.minecraft.mods.multishot.motion.MsMotion;
import fi.dy.masa.minecraft.mods.multishot.worker.MsRecordingHandler;
import fi.dy.masa.minecraft.mods.multishot.worker.MsSaveScreenshot;
import fi.dy.masa.minecraft.mods.multishot.worker.MsThread;

@SideOnly(Side.CLIENT)
public class MsClassReference
{
	private static MsConfigs multishotConfigs = null;
	private static MsGui multishotGui = null;
	private static MsScreenGeneric screenGeneric = null;
	private static MsScreenMotion screenMotion = null;
	private static MsKeyHandler multishotKeyHandler = null;
	private static MsPlayerTickHandler playerTickHandler = null;
	private static MsMotion motion = null;
	private static MsRecordingHandler recordingHandler = null;
	private static MsThread multishotThread = null;
	private static MsSaveScreenshot saveScreenshot = null;

/*
 * Setters
 */
	public static void setMultishotConfigs(MsConfigs par1)
	{
		multishotConfigs = par1;
	}

	public static void setMultishotGui(MsGui par1)
	{
		multishotGui = par1;
	}

	public static void setScreenGeneric(MsScreenGeneric par1)
	{
		screenGeneric = par1;
	}

	public static void setScreenMotion(MsScreenMotion par1)
	{
		screenMotion = par1;
	}

	public static void setMultishotKeyHandler(MsKeyHandler par1)
	{
		multishotKeyHandler = par1;
	}

	public static void setPlayerTickHandler(MsPlayerTickHandler par1)
	{
		playerTickHandler = par1;
	}

	public static void setMotion(MsMotion par1)
	{
		motion = par1;
	}

	public static void setRecordingHandler(MsRecordingHandler par1)
	{
		recordingHandler = par1;
	}

	public static void setMultishotThread(MsThread par1)
	{
		multishotThread = par1;
	}

	public static void setSaveScreenshot(MsSaveScreenshot par1)
	{
		saveScreenshot = par1;
	}
/*
 * Getters
 */
	public static MsConfigs getMultishotConfigs()
	{
		return multishotConfigs;
	}

	public static MsGui getMultishotGui()
	{
		return multishotGui;
	}

	public static MsScreenGeneric getScreenGeneric()
	{
		return screenGeneric;
	}

	public static MsScreenMotion getScreenMotion()
	{
		return screenMotion;
	}

	public static MsKeyHandler getMultishotKeyHandler()
	{
		return multishotKeyHandler;
	}

	public static MsPlayerTickHandler getPlayerTickHandler()
	{
		return playerTickHandler;
	}

	public static MsMotion getMotion()
	{
		return motion;
	}

	public static MsRecordingHandler getRecordingHandler()
	{
		return recordingHandler;
	}

	public static MsThread getMultishotThread()
	{
		return multishotThread;
	}

	public static MsSaveScreenshot getSaveScreenshot()
	{
		return saveScreenshot;
	}

	public static Minecraft getMinecraft()
	{
		return Minecraft.getMinecraft();
	}
}
