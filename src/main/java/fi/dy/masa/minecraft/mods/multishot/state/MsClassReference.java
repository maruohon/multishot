package fi.dy.masa.minecraft.mods.multishot.state;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.config.MsConfigs;
import fi.dy.masa.minecraft.mods.multishot.gui.MsGui;
import fi.dy.masa.minecraft.mods.multishot.gui.MsScreenGeneric;
import fi.dy.masa.minecraft.mods.multishot.gui.MsScreenMotion;
import fi.dy.masa.minecraft.mods.multishot.handlers.MsKeyEvent;
import fi.dy.masa.minecraft.mods.multishot.handlers.MsTickEvent;
import fi.dy.masa.minecraft.mods.multishot.motion.MsMotion;
import fi.dy.masa.minecraft.mods.multishot.worker.MsRecordingHandler;
import fi.dy.masa.minecraft.mods.multishot.worker.MsSaveScreenshot;
import fi.dy.masa.minecraft.mods.multishot.worker.MsThread;

@SideOnly(Side.CLIENT)
public class MsClassReference
{
	private static Configuration configuration = null;
	private static MsConfigs multishotConfigs = null;
	private static MsGui multishotGui = null;
	private static MsScreenGeneric screenGeneric = null;
	private static MsScreenMotion screenMotion = null;
	private static MsKeyEvent multishotKeyHandler = null;
	private static MsTickEvent msClientTickEvent = null;
	private static MsMotion motion = null;
	private static MsRecordingHandler recordingHandler = null;
	private static MsThread multishotThread = null;
	private static MsSaveScreenshot saveScreenshot = null;

/*
 * Setters
 */
	public static void setConfiguration(Configuration par1)
	{
		configuration = par1;
	}

	public static void setMsConfigs(MsConfigs par1)
	{
		multishotConfigs = par1;
	}

	public static void setGui(MsGui par1)
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

	public static void setKeyEvent(MsKeyEvent par1)
	{
		multishotKeyHandler = par1;
	}

	public static void setTickEvent(MsTickEvent par1)
	{
		msClientTickEvent = par1;
	}

	public static void setMotion(MsMotion par1)
	{
		motion = par1;
	}

	public static void setRecordingHandler(MsRecordingHandler par1)
	{
		recordingHandler = par1;
	}

	public static void setThread(MsThread par1)
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
	public static Configuration getConfiguration()
	{
		return configuration;
	}

	public static MsConfigs getMsConfigs()
	{
		return multishotConfigs;
	}

	public static MsGui getGui()
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

	public static MsKeyEvent getKeyEvent()
	{
		return multishotKeyHandler;
	}

	public static MsTickEvent getTickEvent()
	{
		return msClientTickEvent;
	}

	public static MsMotion getMotion()
	{
		return motion;
	}

	public static MsRecordingHandler getRecordingHandler()
	{
		return recordingHandler;
	}

	public static MsThread getThread()
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
