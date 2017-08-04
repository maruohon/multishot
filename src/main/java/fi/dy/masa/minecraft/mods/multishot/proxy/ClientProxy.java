package fi.dy.masa.minecraft.mods.multishot.proxy;

import net.minecraftforge.common.MinecraftForge;
import fi.dy.masa.minecraft.mods.multishot.gui.MsGui;
import fi.dy.masa.minecraft.mods.multishot.handlers.EventHandler;
import fi.dy.masa.minecraft.mods.multishot.handlers.RenderEventHandler;
import fi.dy.masa.minecraft.mods.multishot.motion.Motion;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        Motion.init();

        MinecraftForge.EVENT_BUS.register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(new MsGui());
        MinecraftForge.EVENT_BUS.register(new RenderEventHandler());
    }
}
