package fi.dy.masa.minecraft.mods.multishot.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import fi.dy.masa.minecraft.mods.multishot.config.Configs;
import fi.dy.masa.minecraft.mods.multishot.gui.MsGui;
import fi.dy.masa.minecraft.mods.multishot.handlers.EventHandler;
import fi.dy.masa.minecraft.mods.multishot.motion.Motion;
import fi.dy.masa.minecraft.mods.multishot.worker.RecordingHandler;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        Configs.getConfig().readFromConfiguration();

        new Motion();
        new RecordingHandler();

        FMLCommonHandler.instance().bus().register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(new MsGui());
    }
}
