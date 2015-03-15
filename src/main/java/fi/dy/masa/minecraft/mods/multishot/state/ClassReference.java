package fi.dy.masa.minecraft.mods.multishot.state;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.worker.MsThread;

@SideOnly(Side.CLIENT)
public class ClassReference
{
    private static MsThread multishotThread = null;

    public static void setThread(MsThread par1)
    {
        multishotThread = par1;
    }

    public static MsThread getThread()
    {
        return multishotThread;
    }
}
