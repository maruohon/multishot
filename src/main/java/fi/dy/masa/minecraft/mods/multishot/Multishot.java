package fi.dy.masa.minecraft.mods.multishot;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import fi.dy.masa.minecraft.mods.multishot.config.MultishotConfigs;
import fi.dy.masa.minecraft.mods.multishot.gui.MultishotGui;
import fi.dy.masa.minecraft.mods.multishot.handlers.MultishotKeys;
import fi.dy.masa.minecraft.mods.multishot.handlers.PlayerTickHandler;
import fi.dy.masa.minecraft.mods.multishot.libs.Reference;
import fi.dy.masa.minecraft.mods.multishot.motion.MultishotMotion;
import fi.dy.masa.minecraft.mods.multishot.state.MultishotState;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)

public class Multishot
{
	@Instance(Reference.MOD_ID)
	public static Multishot instance;
	private MultishotKeys multishotKeys = null;
	private MultishotConfigs multishotConfigs = null;
	private MultishotMotion multishotMotion = null;
	private MultishotGui multishotGui = null;
	private Configuration cfg = null;
	private Minecraft mc;
	public static Logger logger = Logger.getLogger(Reference.MOD_NAME);

	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		if (event.getSide() == Side.CLIENT)
		{
			if(! logger.getParent().equals(FMLLog.getLogger()))
			{
				logger.setParent(FMLLog.getLogger());
			}

			this.mc = Minecraft.getMinecraft();
			this.cfg = new Configuration(event.getSuggestedConfigurationFile());
			try
			{
				this.cfg.load();
			}
			catch (Exception e)
			{
				logSevere(Reference.MOD_NAME + " has a problem loading it's configuration");
			}
			finally
			{
				this.multishotConfigs = new MultishotConfigs(this.cfg);
				this.multishotConfigs.readFromConfiguration();
				MultishotState.setStateFromConfigs(this.multishotConfigs);
				if (this.cfg.hasChanged())
				{
					this.cfg.save();
				}
			}

			File multishotBasePath = new File(this.multishotConfigs.getSavePath());
			if (! multishotBasePath.isDirectory())
			{
				if (! multishotBasePath.mkdir())
				{
					// Failed to create the base directory
					logSevere("Could not create multishot base directory ('" +
							Minecraft.getMinecraft().mcDataDir.getAbsolutePath() +
							"/" + Reference.MULTISHOT_BASE_DIR + "')");
				}
			}
			multishotBasePath = null;
			this.multishotGui = new MultishotGui(this.mc, this.multishotConfigs);
			this.multishotMotion = new MultishotMotion(this.multishotConfigs, this.multishotGui);
			this.multishotGui.setMotionInstance(this.multishotMotion);
			MinecraftForge.EVENT_BUS.register(this.multishotGui);
		}
	}

	@Init
	public void init(FMLInitializationEvent event)
	{
		if (event.getSide() == Side.CLIENT)
		{
			log("Initializing " + Reference.MOD_NAME + " mod");
			// Non-XML version
			//LanguageRegistry.instance().loadLocalization("/lang/en_US.lang", "en_US", false);
			// XML-version
			LanguageRegistry.instance().loadLocalization("/lang/en_US.xml", "en_US", true);
			this.multishotKeys = new MultishotKeys(this.mc, this.cfg, this.multishotConfigs, this.multishotMotion);
			KeyBindingRegistry.registerKeyBinding(multishotKeys);
			TickRegistry.registerTickHandler(new PlayerTickHandler(this.multishotConfigs, this.multishotMotion), Side.CLIENT);
		}
	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event)
	{
	}

    public static void logSevere(String s)
    {
		logger.log(Level.SEVERE, s);
    }

    public static void logWarning(String s)
    {
		logger.log(Level.WARNING, s);
    }

    public static void log(String s)
    {
        logger.log(Level.INFO, s);
    }
}
