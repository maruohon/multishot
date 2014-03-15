package fi.dy.masa.minecraft.mods.multishot;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraftforge.common.Configuration;
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
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.handlers.MultishotKeys;
import fi.dy.masa.minecraft.mods.multishot.libs.Reference;

@SideOnly(Side.CLIENT)
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class Multishot
{
	@Instance(Reference.MOD_ID)
	public static Multishot instance;

	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
		try
		{
			cfg.load();
		}
		catch (Exception e)
		{
			// FMLLog.log(Level.ERROR, e,
			// "Multishot has a problem loading it's configuration");
		}
		finally
		{
			if (cfg.hasChanged())
			{
				cfg.save();
			}
		}

		File multishotBasePath = new File(Reference.MULTISHOT_BASE_DIR);
		if (! multishotBasePath.isDirectory())
		{
			if (! multishotBasePath.mkdir())
			{
				// Failed to create the base directory
				System.out.print("Error: Could not create multishot base directory ('");
				System.out.print(Reference.MULTISHOT_BASE_DIR + "')\n");
			}
		}
		multishotBasePath = null;
	}

	@Init
	public void init(FMLInitializationEvent event)
	{
		if (event.getSide() == Side.CLIENT)
		{
			log("Initializing " + Reference.MOD_NAME + " mod");
		}
		KeyBindingRegistry.registerKeyBinding(new MultishotKeys());
	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event)
	{

	}

	public static Logger logger = Logger.getLogger(Reference.MOD_NAME);
    static
    {
    	logger.setParent(FMLLog.getLogger());
    }
    public static void log(String s, boolean warning)
    {
        logger.log(warning ? Level.WARNING : Level.INFO, s);
    }

    public static void log(String s)
    {
        log(s, false);
    }
}
