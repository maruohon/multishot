package fi.dy.masa.minecraft.mods.multishot;

import java.io.File;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import fi.dy.masa.minecraft.mods.multishot.libs.Reference;

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
		} catch (Exception e)
		{
			// FMLLog.log(Level.ERROR, e,
			// "Multishot has a problem loading it's configuration");
		} finally
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
	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event)
	{

	}
}
