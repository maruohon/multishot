package fi.dy.masa.minecraft.mods.multishot.proxy;

import java.io.File;
import net.minecraftforge.common.MinecraftForge;
import fi.dy.masa.minecraft.mods.multishot.config.Configs;
import fi.dy.masa.minecraft.mods.multishot.gui.MsGui;
import fi.dy.masa.minecraft.mods.multishot.handlers.EventHandler;
import fi.dy.masa.minecraft.mods.multishot.handlers.RenderEventHandler;
import fi.dy.masa.minecraft.mods.multishot.motion.Motion;
import fi.dy.masa.minecraft.mods.multishot.worker.RecordingHandler;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit(File configDir)
    {
        new Configs(configDir).readFromConfiguration();

        new Motion();
        new RecordingHandler();

        MinecraftForge.EVENT_BUS.register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(new MsGui());
        MinecraftForge.EVENT_BUS.register(new RenderEventHandler());
    }
}
