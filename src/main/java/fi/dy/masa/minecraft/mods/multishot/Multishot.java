package fi.dy.masa.minecraft.mods.multishot;

import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import fi.dy.masa.minecraft.mods.multishot.config.Configs;
import fi.dy.masa.minecraft.mods.multishot.proxy.CommonProxy;
import fi.dy.masa.minecraft.mods.multishot.reference.Reference;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION,
    guiFactory = "fi.dy.masa.minecraft.mods.multishot.config.MultishotGuiFactory",
    clientSideOnly=true, acceptableRemoteVersions = "*", acceptedMinecraftVersions = "[1.12]")
public class Multishot
{
    @Mod.Instance(Reference.MOD_ID)
    public static Multishot instance;

    @SidedProxy(clientSide = Reference.PROXY_CLASS_CLIENT, serverSide = Reference.PROXY_CLASS_SERVER)
    public static CommonProxy proxy;

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        Configs.loadConfigsFromFile(event.getModConfigurationDirectory());

        proxy.preInit();
    }
}
