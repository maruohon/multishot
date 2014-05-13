package fi.dy.masa.minecraft.mods.multishot;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import fi.dy.masa.minecraft.mods.multishot.config.MsConfigs;
import fi.dy.masa.minecraft.mods.multishot.gui.MsGui;
import fi.dy.masa.minecraft.mods.multishot.handlers.MsTickEvent;
import fi.dy.masa.minecraft.mods.multishot.handlers.MsKeyEvent;
import fi.dy.masa.minecraft.mods.multishot.motion.MsMotion;
import fi.dy.masa.minecraft.mods.multishot.reference.MsReference;
import fi.dy.masa.minecraft.mods.multishot.state.MsClassReference;
import fi.dy.masa.minecraft.mods.multishot.state.MsState;

@Mod(modid = MsReference.MOD_ID, name = MsReference.MOD_NAME, version = MsReference.VERSION)

public class Multishot
{
	@Instance(MsReference.MOD_ID)
	public static Multishot instance;
	private MsConfigs multishotConfigs = null;
	private MsMotion multishotMotion = null;
	private MsGui multishotGui = null;
	private MsTickEvent multishotClientTickEvent = null;
	private MsKeyEvent multishotKeyEvent = null;
	private Configuration cfg = null;
	private Minecraft mc;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		if (event.getSide() == Side.CLIENT)
		{
			event.getModMetadata().version = MsReference.VERSION;
			this.mc = Minecraft.getMinecraft();

			this.cfg = new Configuration(event.getSuggestedConfigurationFile());
			MsClassReference.setConfiguration(this.cfg);
			this.cfg.load();
			this.multishotConfigs = new MsConfigs();
			MsClassReference.setMsConfigs(this.multishotConfigs);
			this.multishotConfigs.readFromConfiguration();
			MsState.setStateFromConfigs(this.multishotConfigs);
			if (this.cfg.hasChanged())
			{
				this.cfg.save();
			}

			File multishotBasePath = new File(this.multishotConfigs.getSavePath());
			if (! multishotBasePath.isDirectory())
			{
				if (! multishotBasePath.mkdir())
				{
					// Failed to create the base directory
					logSevere("Could not create multishot base directory ('" +
							Minecraft.getMinecraft().mcDataDir.getAbsolutePath() +
							"/" + MsReference.MULTISHOT_BASE_DIR + "')");
				}
			}
			multishotBasePath = null;
			this.multishotGui				= new MsGui(this.mc);
			this.multishotMotion			= new MsMotion();
			this.multishotClientTickEvent	= new MsTickEvent();
			this.multishotKeyEvent			= new MsKeyEvent(this.mc, this.cfg, this.multishotConfigs, this.multishotMotion);
			MsClassReference.setGui(this.multishotGui);
			MsClassReference.setMotion(this.multishotMotion);
			MsClassReference.setTickEvent(this.multishotClientTickEvent);
			MsClassReference.setKeyEvent(this.multishotKeyEvent);
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		if (event.getSide() == Side.CLIENT)
		{
			logInfo("Initializing " + MsReference.MOD_NAME + " mod");
			FMLCommonHandler.instance().bus().register(this.multishotClientTickEvent);
			FMLCommonHandler.instance().bus().register(this.multishotKeyEvent);
			MinecraftForge.EVENT_BUS.register(this.multishotGui);
		}
	}

/*
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
	}
*/

	public static void logSevere(String s)
	{
		FMLLog.severe(s);
	}

	public static void logWarning(String s)
	{
		FMLLog.warning(s);
	}

	public static void logInfo(String s)
	{
		FMLLog.info(s);
	}
}
