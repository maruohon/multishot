package fi.dy.masa.minecraft.mods.multishot.proxy;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import fi.dy.masa.minecraft.mods.multishot.config.Configs;
import fi.dy.masa.minecraft.mods.multishot.gui.MsGui;
import fi.dy.masa.minecraft.mods.multishot.handlers.EventHandler;
import fi.dy.masa.minecraft.mods.multishot.handlers.MotionHandler;
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

        FMLCommonHandler.instance().bus().register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(new MotionHandler());
        MinecraftForge.EVENT_BUS.register(new MsGui());
    }
}
