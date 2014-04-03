package fi.dy.masa.minecraft.mods.multishot.libs;

public class MsMathHelper
{
	public static double distance2D(double x1, double z1, double x2, double z2)
	{
		return Math.sqrt(((x1 - x2) * (x1 - x2)) + ((z1 - z2) * (z1 - z2)));
	}
}
