package fi.dy.masa.minecraft.mods.multishot.util;

import net.minecraft.util.Util;
import net.minecraft.util.Util.EnumOS;

public class StringHelper
{
    public static String fixPath(String str)
    {
        if (Util.getOSType() == EnumOS.WINDOWS)
        {
            return str.replace('/', '\\').replace("\\.\\", "\\").replace("\\\\", "\\");
        }

        return str.replace('\\', '/').replace("/./", "/").replace("//", "/");
    }
}
