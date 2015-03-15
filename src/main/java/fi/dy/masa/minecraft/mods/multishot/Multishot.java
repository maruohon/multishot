package fi.dy.masa.minecraft.mods.multishot;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.logging.log4j.Logger;

import fi.dy.masa.minecraft.mods.multishot.config.MsConfigs;
import fi.dy.masa.minecraft.mods.multishot.gui.MsGui;
import fi.dy.masa.minecraft.mods.multishot.handlers.MsKeyEvent;
import fi.dy.masa.minecraft.mods.multishot.handlers.MsTickEvent;
import fi.dy.masa.minecraft.mods.multishot.libs.MsStringHelper;
import fi.dy.masa.minecraft.mods.multishot.motion.MsMotion;
import fi.dy.masa.minecraft.mods.multishot.reference.MsConstants;
import fi.dy.masa.minecraft.mods.multishot.reference.MsReference;
import fi.dy.masa.minecraft.mods.multishot.state.MsClassReference;
import fi.dy.masa.minecraft.mods.multishot.state.MsState;

@Mod(modid = MsReference.MOD_ID, name = MsReference.MOD_NAME, version = MsReference.VERSION)

public class Multishot
{
    @Instance(MsReference.MOD_ID)
    public static Multishot instance;
    private MsConfigs multishotConfigs = null;
    private MsMotion multishotMotion = null;
    private MsGui multishotGui = null;
    private MsTickEvent multishotClientTickEvent = null;
    private MsKeyEvent multishotKeyEvent = null;
    private Configuration cfg = null;
    private String pointsDir = null;
    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        instance = this;
        logger = event.getModLog();
        event.getModMetadata().version = MsReference.VERSION;

        if (event.getSide() == Side.CLIENT)
        {
            this.cfg = new Configuration(event.getSuggestedConfigurationFile());
            this.pointsDir = MsStringHelper.fixPath(event.getModConfigurationDirectory().getAbsolutePath().concat("/").concat(MsReference.MOD_ID));
            MsClassReference.setConfiguration(this.cfg);
            this.cfg.load();
            this.multishotConfigs = new MsConfigs();
            MsClassReference.setMsConfigs(this.multishotConfigs);
            this.multishotConfigs.readFromConfiguration();
            MsState.setStateFromConfigs(this.multishotConfigs);
            if (this.cfg.hasChanged())
            {
                this.cfg.save();
            }

            File multishotBasePath = new File(this.multishotConfigs.getSavePath());
            if (! multishotBasePath.isDirectory())
            {
                if (! multishotBasePath.mkdir())
                {
                    // Failed to create the base directory
                    logger.fatal("Could not create multishot base directory ('" + Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + "/" + MsConstants.MULTISHOT_BASE_DIR + "')");
                }
            }
            multishotBasePath = null;
            this.multishotGui               = new MsGui();
            this.multishotMotion            = new MsMotion(this.pointsDir);
            this.multishotClientTickEvent   = new MsTickEvent();
            this.multishotKeyEvent          = new MsKeyEvent(this.cfg, this.multishotConfigs, this.multishotMotion);
            MsClassReference.setGui(this.multishotGui);
            MsClassReference.setMotion(this.multishotMotion);
            MsClassReference.setTickEvent(this.multishotClientTickEvent);
            MsClassReference.setKeyEvent(this.multishotKeyEvent);

            FMLCommonHandler.instance().bus().register(this.multishotClientTickEvent);
            FMLCommonHandler.instance().bus().register(this.multishotKeyEvent);
            MinecraftForge.EVENT_BUS.register(this.multishotGui);
        }
    }
}
