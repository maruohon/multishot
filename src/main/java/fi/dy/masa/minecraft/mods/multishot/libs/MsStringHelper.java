package fi.dy.masa.minecraft.mods.multishot.libs;

import net.minecraft.util.Util;
import net.minecraft.util.Util.EnumOS;

public class MsStringHelper
{
	public static String fixPath(String str)
	{
		if (Util.getOSType() == EnumOS.WINDOWS)
		{
			str = str.replace('/', '\\').replace("\\.\\", "\\").replace("\\\\", "\\");
		}
		else
		{
			str = str.replace('\\', '/').replace("/./", "/").replace("//", "/");
		}
		return str;
	}
}
