package fi.dy.masa.minecraft.mods.multishot;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.Logger;

import fi.dy.masa.minecraft.mods.multishot.proxy.IProxy;
import fi.dy.masa.minecraft.mods.multishot.reference.Reference;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION,
    acceptableRemoteVersions="*", acceptedMinecraftVersions = "[1.8.8,1.9)")
public class Multishot
{
    @Instance(Reference.MOD_ID)
    public static Multishot instance;

    @SidedProxy(clientSide = Reference.PROXY_CLASS_CLIENT, serverSide = Reference.PROXY_CLASS_SERVER)
    public static IProxy proxy;

    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        instance = this;
        logger = event.getModLog();

        proxy.preInit(event.getModConfigurationDirectory());
    }
}
