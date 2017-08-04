package fi.dy.masa.minecraft.mods.multishot.reference;

import net.minecraft.util.ResourceLocation;

public class Reference
{
    public static final String MOD_ID = "multishot";
    public static final String MOD_NAME = "Multishot";
    public static final String VERSION = "@MOD_VERSION@";
    public static final String FINGERPRINT = "2b03e1423915a189b8094816baa18f239d576dff";

    public static final String PROXY_CLASS_CLIENT = "fi.dy.masa.minecraft.mods.multishot.proxy.ClientProxy";
    public static final String PROXY_CLASS_SERVER = "fi.dy.masa.minecraft.mods.multishot.proxy.CommonProxy";


    public static final String RESOURCE_PREFIX = Reference.MOD_ID.toLowerCase() + ":";
    public static final String GUI_TEXTURE_LOCATION = "textures/gui/";
    public static final ResourceLocation GUI_HUD = new ResourceLocation(Reference.MOD_ID, GUI_TEXTURE_LOCATION + "hud.png");
}
