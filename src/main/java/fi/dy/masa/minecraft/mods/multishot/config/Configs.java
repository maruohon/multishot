package fi.dy.masa.minecraft.mods.multishot.config;

import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import fi.dy.masa.minecraft.mods.multishot.Multishot;
import fi.dy.masa.minecraft.mods.multishot.gui.ScreenGeneric;
import fi.dy.masa.minecraft.mods.multishot.reference.Constants;
import fi.dy.masa.minecraft.mods.multishot.reference.Reference;
import fi.dy.masa.minecraft.mods.multishot.state.State;
import fi.dy.masa.minecraft.mods.multishot.util.StringHelper;

@Mod.EventBusSubscriber(Side.CLIENT)
public class Configs {
    public static boolean freeCameraRenderClouds;
    public static boolean freeCameraRenderFog;
    public static boolean freeCameraRenderSpecialParticles;
    public static boolean freeCameraRenderWeather;
    public static boolean freeCameraUseCustomRenderer;

    private static boolean cfgMultishotEnabled;
    private static boolean cfgMotionEnabled;
    private static boolean cfgLockControls;
    private static boolean cfgHideGui;
    private static boolean useFreeCamera;
    private static int cfgGuiPosition;
    private static int cfgGuiOffsetX;
    private static int cfgGuiOffsetY;
    private static int cfgInterval; // In 1/10 of a second
    private static int cfgZoom;
    private static int cfgSelectedTimer;
    private static int cfgTimerVideo; // In seconds
    private static int cfgTimerRealTime; // In seconds
    private static int cfgTimerNumShots;
    private static int cfgImgFormat;
    private static int cfgMotionMode;
    private static int cfgMotionSpeed; // Speed in the non-linear modes
    private static int cfgMotionX; // In mm/s
    private static int cfgMotionZ;
    private static int cfgMotionY;
    private static int cfgRotationYaw; // In 1/100th of a degree/s
    private static int cfgRotationPitch;
    private static int freeCameraResolutionWidth = 1280;
    private static int freeCameraResolutionHeight = 720;

    public static final String CATEGORY_FREECAMERA = "FreeCamera";
    public static final String CATEGORY_GENERIC = "Generic";
    public static final String CATEGORY_MOTION = "Motion";

    private static File configurationFile;
    private static File pointsDir;
    private static Configuration config;
    private static String cfgMultishotSavePath;

    public static void loadConfigsFromFile(File configDir)
    {
        configurationFile = new File(configDir, Reference.MOD_ID + ".cfg");
        pointsDir = new File(configDir, Reference.MOD_ID);
        config = new Configuration(configurationFile, null, true);
        config.load();

        reLoadAllConfigs(false);
    }

    public static Configuration getConfig()
    {
        return config;
    }

    private static void reLoadAllConfigs(boolean reloadFromFile)
    {
        if (reloadFromFile)
        {
            config.load();
        }

        loadConfigGeneric(config);

        if (config.hasChanged())
        {
            config.save();
        }
    }

    public static File getConfigFile()
    {
        return configurationFile;
    }

    private static String getDefaultMultishotPath()
    {
        return (new File(Minecraft.getMinecraft().mcDataDir, Constants.MULTISHOT_BASE_DIR)).getAbsolutePath();
    }

    @SubscribeEvent
    public static void onConfigChangedEvent(OnConfigChangedEvent event)
    {
        if (Reference.MOD_ID.equals(event.getModID()))
        {
            reLoadAllConfigs(false);
        }
    }

    private static void loadConfigGeneric(Configuration cfg)
    {
        Property prop;

        prop = cfg.get(CATEGORY_FREECAMERA, "freeCameraRenderClouds", true);
        prop.setComment("Whether to render clouds in the Free Camera mode.\nNOTE: This only works if freeCameraUseCustomRender = true");
        freeCameraRenderClouds = prop.getBoolean();

        prop = cfg.get(CATEGORY_FREECAMERA, "freeCameraRenderFog", true);
        prop.setComment("Whether to render fog in the Free Camera mode.\nNOTE: This only works if freeCameraUseCustomRender = true");
        freeCameraRenderFog = prop.getBoolean();

        prop = cfg.get(CATEGORY_FREECAMERA, "freeCameraRenderSpecialParticles", false);
        prop.setComment("Whether to render certain particles in the Free Camera mode (ones using FXLayer 3).\n" +
                        "If this is enabled, then at least the item pickup animation gets rendered in the free camera view.\n" +
                        "NOTE: This only works if freeCameraUseCustomRenderer = true");
        freeCameraRenderSpecialParticles = prop.getBoolean();

        prop = cfg.get(CATEGORY_FREECAMERA, "freeCameraRenderWeather", false);
        prop.setComment("Whether to render rain/snow in the Free Camera mode.\nNOTE: This only works if freeCameraUseCustomRender = true");
        freeCameraRenderWeather = prop.getBoolean();

        prop = cfg.get(CATEGORY_FREECAMERA, "freeCameraUseCustomRenderer", true);
        prop.setComment("Whether to enable the custom rendering method in the Free Camera mode.\n" +
                        "This is recommended to leave enabled, unless you are experiencing problems that are solved by disabling this.");
        freeCameraUseCustomRenderer = prop.getBoolean();

        prop = cfg.get(CATEGORY_FREECAMERA, "freeCameraResolutionWidth", 1280);
        prop.setComment("The render width in the free camera mode");
        freeCameraResolutionWidth = prop.getInt();

        prop = cfg.get(CATEGORY_FREECAMERA, "freeCameraResolutionHeight", 720);
        prop.setComment("The render height in the free camera mode");
        freeCameraResolutionHeight = prop.getInt();

        // Generic

        prop = cfg.get(CATEGORY_GENERIC, "multishotEnabled", false);
        prop.setComment("Multishot enabled override, disables the Multishot hotkey");
        cfgMultishotEnabled = prop.getBoolean();

        prop = cfg.get(CATEGORY_GENERIC, "motionEnabled", false);
        prop.setComment("Motion enabled override, disables the Motion hotkey");
        cfgMotionEnabled = prop.getBoolean();

        prop = cfg.get(CATEGORY_GENERIC, "lockControlsEnabled", false);
        prop.setComment("Lock the mouse and keyboard controls while in recording or motion mode");
        cfgLockControls = prop.getBoolean();

        prop = cfg.get(CATEGORY_GENERIC, "hideGuiEnabled", false);
        prop.setComment("Hide the Multishot GUI (don't display the icons or save messages)");
        cfgHideGui = prop.getBoolean();

        prop = cfg.get(CATEGORY_GENERIC, "useFreeCamera", false);
        prop.setComment("Render using a free moving virtual camera");
        useFreeCamera = prop.getBoolean();

        prop = cfg.get(CATEGORY_GENERIC, "guiPosition", 0);
        prop.setComment("Multishot GUI position (0 = Top Right, 1 = Bottom Right, 2 = Bottom Left, 3 = Top Left)");
        cfgGuiPosition = prop.getInt();

        prop = cfg.get(CATEGORY_GENERIC, "guiOffsetX", 0);
        prop.setComment("Multishot GUI horizontal offset");
        cfgGuiOffsetX = prop.getInt();

        prop = cfg.get(CATEGORY_GENERIC, "guiOffsetY", 0);
        prop.setComment("Multishot GUI vertical offset");
        cfgGuiOffsetY = prop.getInt();

        prop = cfg.get(CATEGORY_GENERIC, "interval", 0);
        prop.setComment("Time between screenshots, in multiples of 0.1 seconds");
        cfgInterval = prop.getInt();

        prop = cfg.get(CATEGORY_GENERIC, "zoom", 0);
        prop.setComment("Zoom factor while in Multishot mode");
        cfgZoom = prop.getInt();

        prop = cfg.get(CATEGORY_GENERIC, "timerType", 0);
        prop.setComment("Timer type (0 = OFF, 1 = Video time, 2 = Real time, 3 = Number of shots)");
        cfgSelectedTimer = prop.getInt();

        prop = cfg.get(CATEGORY_GENERIC, "timerVideo", 0);
        prop.setComment("Timer length in video time, in seconds");
        cfgTimerVideo = prop.getInt();

        prop = cfg.get(CATEGORY_GENERIC, "timerReal", 0);
        prop.setComment("Timer length in real time, in seconds");
        cfgTimerRealTime = prop.getInt();

        prop = cfg.get(CATEGORY_GENERIC, "timerShots", 0);
        prop.setComment("Timer length in number of screenshots");
        cfgTimerNumShots = prop.getInt();

        prop = cfg.get(CATEGORY_GENERIC, "imgFormat", 0);
        prop.setComment("Screenshot image format (0 = PNG, 1 = JPG with quality 75, 2 = JPG @ 80, 3 = JPG @ 85, 4 = JPG @ 90, 5 = JPG @ 95)");
        cfgImgFormat = prop.getInt();

        prop = cfg.get(CATEGORY_GENERIC, "savePath", "multishot");
        prop.setComment( "The directory where the screenshots will be saved");
        cfgMultishotSavePath = prop.getString();

        // Motion

        prop = cfg.get(CATEGORY_MOTION, "motionMode", 0);
        prop.setComment("Motion mode (0 = Linear, 1 = Circular, 2 = Elliptical, 3 = Path (linear segments), 4 = Path (smooth))");
        cfgMotionMode = prop.getInt();

        prop = cfg.get(CATEGORY_MOTION, "motionSpeed", 0);
        prop.setComment("The movement speed in non-linear modes, in mm/s (=1/1000th of a block)");
        cfgMotionSpeed = prop.getInt();

        prop = cfg.get(CATEGORY_MOTION, "motionX", 0);
        prop.setComment("Motion speed along the x-axis in the Linear mode, in mm/s (=1/1000th of a block)");
        cfgMotionX = prop.getInt();

        prop = cfg.get(CATEGORY_MOTION, "motionY", 0);
        prop.setComment("Motion speed along the y-axis in the Linear mode, in mm/s (=1/1000th of a block)");
        cfgMotionY = prop.getInt();

        prop = cfg.get(CATEGORY_MOTION, "motionZ", 0);
        prop.setComment("Motion speed along the z-axis in the Linear mode, in mm/s (=1/1000th of a block)");
        cfgMotionZ = prop.getInt();

        prop = cfg.get(CATEGORY_MOTION, "rotationYaw", 0);
        prop.setComment("Yaw rotation speed, in 1/100th of a degree per second");
        cfgRotationYaw = prop.getInt();

        prop = cfg.get(CATEGORY_MOTION, "rotationPitch", 0);
        prop.setComment("Pitch rotation speed, in 1/100th of a degree per second");
        cfgRotationPitch = prop.getInt();

        validateConfigs();

        File multishotBasePath = new File(getSavePath());

        if (multishotBasePath.isDirectory() == false && multishotBasePath.mkdirs() == false)
        {
            // Failed to create the base directory
            Multishot.logger.error("Could not create multishot base directory ('{}')", multishotBasePath.getAbsolutePath());
        }

        State.setStateFromConfigs();
    }

    // Write the values to the Forge Configuration handler
    public static void writeToConfiguration()
    {
        Property prop;

        prop = config.get(CATEGORY_GENERIC, "multishotEnabled", false);
        prop.setComment("Multishot enabled override, disables the Multishot hotkey");
        prop.set(cfgMultishotEnabled);

        prop = config.get(CATEGORY_GENERIC, "motionEnabled", false);
        prop.setComment("Motion enabled override, disables the Motion hotkey");
        prop.set(cfgMotionEnabled);

        prop = config.get(CATEGORY_GENERIC, "lockControlsEnabled", false);
        prop.setComment("Lock the mouse and keyboard controls while in recording or motion mode");
        prop.set(cfgLockControls);

        prop = config.get(CATEGORY_GENERIC, "hideGuiEnabled", false);
        prop.setComment("Hide the Multishot GUI (don't display the icons or save messages)");
        prop.set(cfgHideGui);

        prop = config.get(CATEGORY_GENERIC, "useFreeCamera", false);
        prop.setComment("Render using a free moving virtual camera");
        prop.set(useFreeCamera);

        prop = config.get(CATEGORY_GENERIC, "guiPosition", 0);
        prop.setComment("Multishot GUI position (0 = Top Right, 1 = Bottom Right, 2 = Bottom Left, 3 = Top Left)");
        prop.set(cfgGuiPosition);

        prop = config.get(CATEGORY_GENERIC, "guiOffsetX", 0);
        prop.setComment("Multishot GUI horizontal offset");
        prop.set(cfgGuiOffsetX);

        prop = config.get(CATEGORY_GENERIC, "guiOffsetY", 0);
        prop.setComment("Multishot GUI vertical offset");
        prop.set(cfgGuiOffsetY);

        prop = config.get(CATEGORY_GENERIC, "interval", 0);
        prop.setComment("Time between screenshots, in multiples of 0.1 seconds");
        prop.set(cfgInterval);

        prop = config.get(CATEGORY_GENERIC, "zoom", 0);
        prop.setComment("Zoom factor while in Multishot mode");
        prop.set(cfgZoom);

        prop = config.get(CATEGORY_GENERIC, "timerType", 0);
        prop.setComment("Timer type (0 = OFF, 1 = Video time, 2 = Real time, 3 = Number of shots)");
        prop.set(cfgSelectedTimer);

        prop = config.get(CATEGORY_GENERIC, "timerVideo", 0);
        prop.setComment("Timer length in video time, in seconds");
        prop.set(cfgTimerVideo);

        prop = config.get(CATEGORY_GENERIC, "timerReal", 0);
        prop.setComment("Timer length in real time, in seconds");
        prop.set(cfgTimerRealTime);

        prop = config.get(CATEGORY_GENERIC, "timerShots", 0);
        prop.setComment("Timer length in number of screenshots");
        prop.set(cfgTimerNumShots);

        prop = config.get(CATEGORY_GENERIC, "imgFormat", 0);
        prop.setComment("Screenshot image format (0 = PNG, 1 = JPG with quality 75, 2 = JPG @ 80, 3 = JPG @ 85, 4 = JPG @ 90, 5 = JPG @ 95)");
        prop.set(cfgImgFormat);

        prop = config.get(CATEGORY_GENERIC, "freeCameraResolutionWidth", 1280);
        prop.setComment("The render width in the free camera mode");
        prop.set(freeCameraResolutionWidth);

        prop = config.get(CATEGORY_GENERIC, "freeCameraResolutionHeight", 720);
        prop.setComment("The render height in the free camera mode");
        prop.set(freeCameraResolutionHeight);

        prop = config.get(CATEGORY_GENERIC, "savePath", "multishot");
        prop.setComment("The directory where the screenshots will be saved");
        prop.set(cfgMultishotSavePath);

        prop = config.get(CATEGORY_MOTION, "motionMode", 0);
        prop.setComment("Motion mode (0 = Linear, 1 = Circular, 2 = Elliptical, 3 = Path (linear segments), 4 = Path (smooth))");
        prop.set(cfgMotionMode);

        prop = config.get(CATEGORY_MOTION, "motionSpeed", 0);
        prop.setComment("The movement speed in non-linear modes, in mm/s (=1/1000th of a block)");
        prop.set(cfgMotionSpeed);

        prop = config.get(CATEGORY_MOTION, "motionX", 0);
        prop.setComment("Motion speed along the x-axis in the Linear mode, in mm/s (=1/1000th of a block)");
        prop.set(cfgMotionX);

        prop = config.get(CATEGORY_MOTION, "motionY", 0);
        prop.setComment("Motion speed along the y-axis in the Linear mode, in mm/s (=1/1000th of a block)");
        prop.set(cfgMotionY);

        prop = config.get(CATEGORY_MOTION, "motionZ", 0);
        prop.setComment("Motion speed along the z-axis in the Linear mode, in mm/s (=1/1000th of a block)");
        prop.set(cfgMotionZ);

        prop = config.get(CATEGORY_MOTION, "rotationYaw", 0);
        prop.setComment("Yaw rotation speed, in 1/100th of a degree per second");
        prop.set(cfgRotationYaw);

        prop = config.get(CATEGORY_MOTION, "rotationPitch", 0);
        prop.setComment("Pitch rotation speed, in 1/100th of a degree per second");
        prop.set(cfgRotationPitch);

        config.save();
    }

    private static void validateConfigs()
    {
        if (cfgGuiPosition < 0 || cfgGuiPosition > 3) { cfgGuiPosition = 0; } // Multishot GUI position (0 = Top Right, 1 = Bottom Right, 2 = Bottom Left, 3 = Top Left)
        if (cfgGuiOffsetX < -500 || cfgGuiOffsetX > 500) { cfgGuiOffsetX = 0; } // Limit the offsets somewhat
        if (cfgGuiOffsetY < -500 || cfgGuiOffsetY > 500) { cfgGuiOffsetY = 0; }
        if (cfgInterval < 0) { cfgInterval = 0; } // Negative intervals are not allowed, doh
        if (cfgZoom < -100 || cfgZoom > 100) { cfgZoom = 0; }
        if (cfgSelectedTimer < 0 || cfgSelectedTimer > 3) { cfgSelectedTimer = 0; } // Timer type (0 = OFF, 1 = Video time, 2 = Real time, 3 = Number of shots)
        if (cfgTimerVideo < 0) { cfgTimerVideo = 0; }
        if (cfgTimerRealTime < 0) { cfgTimerRealTime = 0; }
        if (cfgTimerNumShots < 0) { cfgTimerNumShots = 0; }
        if (cfgImgFormat < 0 || cfgImgFormat > 5) { cfgImgFormat = 0; } // Screenshot image format (0 = PNG, 1 = JPG with quality 75, 2 = JPG @ 80, 3 = JPG @ 85, 4 = JPG @ 90, 5 = JPG @ 95)
        if (cfgMotionMode < 0 || cfgMotionMode > 4) { cfgMotionMode = 0; } // Motion mode (0 = Linear, 1 = Circular, 2 = Elliptical, 3 = Path (linear segments), 4 = Path (smooth))
        if (cfgMotionSpeed < -1000000 || cfgMotionSpeed > 1000000) { cfgMotionSpeed = 0; } // max 1000m/s :p
        if (freeCameraResolutionWidth > 8192 || freeCameraResolutionWidth < 1) { freeCameraResolutionWidth = 1280; }
        if (freeCameraResolutionHeight > 8192 || freeCameraResolutionHeight < 1) { freeCameraResolutionHeight = 720; }

        File dir = new File(cfgMultishotSavePath);

        if (dir.isDirectory() == false)
        {
            cfgMultishotSavePath = getDefaultMultishotPath();
        }

        writeToConfiguration();
    }

    public static void resetAllConfigs()
    {
        cfgMultishotEnabled = false;
        cfgMotionEnabled = false;
        cfgLockControls = false;
        cfgHideGui = false;
        useFreeCamera = false;
        cfgGuiPosition = 0;
        // We don't reset the hidden configs here, that would probably get annoying
        cfgInterval = 0;
        cfgZoom = 0;
        cfgSelectedTimer = 0;
        cfgTimerVideo = 0;
        cfgTimerRealTime = 0;
        cfgTimerNumShots = 0;
        cfgImgFormat = 0;
        cfgMotionMode = 0;
        cfgMotionSpeed = 0;
        cfgMotionX = 0; // In mm/s
        cfgMotionZ = 0;
        cfgMotionY = 0;
        cfgRotationYaw = 0; // In 1/100th of a degree/s
        cfgRotationPitch = 0;
        freeCameraResolutionWidth = 1280;
        freeCameraResolutionHeight = 720;
        cfgMultishotSavePath = getDefaultMultishotPath();

        writeToConfiguration();
    }

    // Change a config value (mode 1: regular click without modifiers)
    public static void changeValue(int id, int mode, int btn)
    {
        changeValue(id, mode, btn, 1);
    }

    public static void changeValue(int id, int mode, int btn, int multiplier)
    {
        int increment = 1;

        if (btn == 1)
        {
            increment = -1;
        }

        if (mode == 1)
        {
            increment *= 10;
        }
        else if (mode == 2)
        {
            increment *= 100;
        }
        else if (mode == 3)
        {
            increment *= 1000;
        }

        increment *= multiplier;
        changeValue(id, increment);
    }

    private static void changeValue(int id, int increment)
    {
        switch (id)
        {
            case Constants.GUI_BUTTON_ID_MULTISHOT_ENABLED:
                cfgMultishotEnabled = ! cfgMultishotEnabled;
                break;
            case Constants.GUI_BUTTON_ID_MOTION_ENABLED:
                cfgMotionEnabled = ! cfgMotionEnabled;
                break;
            case Constants.GUI_BUTTON_ID_LOCK_CONTROLS:
                cfgLockControls = ! cfgLockControls;
                break;
            case Constants.GUI_BUTTON_ID_HIDE_GUI:
                cfgHideGui = ! cfgHideGui;
                break;
            case Constants.GUI_BUTTON_ID_USE_FREE_CAMERA:
                useFreeCamera = ! useFreeCamera;
                break;
            case Constants.GUI_BUTTON_ID_FREE_CAMERA_WIDTH:
                freeCameraResolutionWidth = clampInt(freeCameraResolutionWidth, increment, 1, 8192);
                break;
            case Constants.GUI_BUTTON_ID_FREE_CAMERA_HEIGHT:
                freeCameraResolutionHeight = clampInt(freeCameraResolutionHeight, increment, 1, 8192);
                break;
            case Constants.GUI_BUTTON_ID_INTERVAL:
                cfgInterval = clampInt(cfgInterval, increment, 0, 72000); // max 2h = 7200s
                break;
            case Constants.GUI_BUTTON_ID_ZOOM:
                cfgZoom = clampInt(cfgZoom, increment, -100, 100);
                break;
            case Constants.GUI_BUTTON_ID_TIMER_SELECT:
                if (increment > 0) { increment = 1; } else { increment = -1; }
                // 0 = Off, 1 = Video time, 2 = In-Game time, 3 = Number of shots
                cfgSelectedTimer = normalizeIntWrap(cfgSelectedTimer, increment, 0, 3);
                break;
            case Constants.GUI_BUTTON_ID_TIME_VIDEO_HOUR:
                increment *= 60;
            case Constants.GUI_BUTTON_ID_TIME_VIDEO_MINUTE:
                increment *= 60;
            case Constants.GUI_BUTTON_ID_TIME_VIDEO_SECOND:
                cfgTimerVideo = clampInt(cfgTimerVideo, increment, 0, 99 * 3600 + 59 * 60 + 59);
                break;
            case Constants.GUI_BUTTON_ID_TIME_REAL_HOUR:
                increment *= 60;
            case Constants.GUI_BUTTON_ID_TIME_REAL_MINUTE:
                increment *= 60;
            case Constants.GUI_BUTTON_ID_TIME_REAL_SECOND:
                cfgTimerRealTime = clampInt(cfgTimerRealTime, increment, 0, 99 * 3600 + 59 * 60 + 59);
                break;
            case Constants.GUI_BUTTON_ID_TIME_NUM_SHOTS:
                cfgTimerNumShots = clampInt(cfgTimerNumShots, increment, 0, 10000000);
                break;
            case Constants.GUI_BUTTON_ID_BROWSE: // FIXME We re-purpose the Browse button as a "Paste path from clipboard" button for now
                if (increment == -1) // with right click
                {
                    cfgMultishotSavePath = StringHelper.fixPath(ScreenGeneric.getClipboardString());
                }
                break;
            case Constants.GUI_BUTTON_ID_IMG_FORMAT:
                if (increment > 0) { increment = 1; } else { increment = -1; }
                // 0 = PNG, 1 = JPG with quality 75, 2 = JPG @ 80, 3 = JPG @ 85, 4 = JPG @ 90, 5 = JPG @ 95
                cfgImgFormat = normalizeIntWrap(cfgImgFormat, increment, 0, 5);
                break;
            case Constants.GUI_BUTTON_ID_GUI_POSITION:
                if (increment > 0) { increment = 1; } else { increment = -1; }
                // 0 = Top Right, 1 = Bottom Right, 2 = Bottom Left, 3 = Top Left
                cfgGuiPosition = normalizeIntWrap(cfgGuiPosition, increment, 0, 3);
                break;
            case Constants.GUI_BUTTON_ID_MOTION_MODE:
                if (increment > 0) { increment = 1; } else { increment = -1; }
                // Motion mode (0 = Linear, 1 = Circular, 2 = Elliptical, 3 = Path (linear segments), 4 = Path (smooth))
                cfgMotionMode = normalizeIntWrap(cfgMotionMode, increment, 0, 4);
                break;
            case Constants.GUI_BUTTON_ID_MOTION_SPEED:
                cfgMotionSpeed = clampInt(cfgMotionSpeed, increment, -1000000, 1000000); // max 1000m/s :p
                break;
            case Constants.GUI_BUTTON_ID_MOTION_X:
                cfgMotionX = clampInt(cfgMotionX, increment, -1000000, 1000000); // max 1000m/s :p
                break;
            case Constants.GUI_BUTTON_ID_MOTION_Z:
                cfgMotionZ = clampInt(cfgMotionZ, increment, -1000000, 1000000); // max 1000m/s :p
                break;
            case Constants.GUI_BUTTON_ID_MOTION_Y:
                cfgMotionY = clampInt(cfgMotionY, increment, -1000000, 1000000); // max 1000m/s :p
                break;
            case Constants.GUI_BUTTON_ID_ROTATION_YAW:
                cfgRotationYaw = clampInt(cfgRotationYaw, increment, -360000, 360000); // max 10 rotations/s :p
                break;
            case Constants.GUI_BUTTON_ID_ROTATION_PITCH:
                cfgRotationPitch = clampInt(cfgRotationPitch, increment, -360000, 360000); // max 10 rotations/s :p
                break;
            default:
                break;
        }

        writeToConfiguration();
    }

    // Invert a config's value (+/-), where it makes sense...
    public static void invertValue(int id)
    {
        switch (id)
        {
            case Constants.GUI_BUTTON_ID_MULTISHOT_ENABLED:
                cfgMultishotEnabled = ! cfgMultishotEnabled;
                break;
            case Constants.GUI_BUTTON_ID_MOTION_ENABLED:
                cfgMotionEnabled = ! cfgMotionEnabled;
                break;
            case Constants.GUI_BUTTON_ID_LOCK_CONTROLS:
                cfgLockControls = ! cfgLockControls;
                break;
            case Constants.GUI_BUTTON_ID_HIDE_GUI:
                cfgHideGui = ! cfgHideGui;
                break;
            case Constants.GUI_BUTTON_ID_ZOOM:
                cfgZoom = -cfgZoom;
                break;
            case Constants.GUI_BUTTON_ID_MOTION_SPEED:
                cfgMotionSpeed = -cfgMotionSpeed;
                break;
            case Constants.GUI_BUTTON_ID_MOTION_X:
                cfgMotionX = -cfgMotionX;
                break;
            case Constants.GUI_BUTTON_ID_MOTION_Z:
                cfgMotionZ = -cfgMotionZ;
                break;
            case Constants.GUI_BUTTON_ID_MOTION_Y:
                cfgMotionY = -cfgMotionY;
                break;
            case Constants.GUI_BUTTON_ID_ROTATION_YAW:
                cfgRotationYaw = -cfgRotationYaw;
                break;
            case Constants.GUI_BUTTON_ID_ROTATION_PITCH:
                cfgRotationPitch = -cfgRotationPitch;
                break;
            default:
                break;
        }

        writeToConfiguration();
    }

    // Reset a config's value
    public static void resetValue(int id)
    {
        int tmp;

        switch (id)
        {
            case Constants.GUI_BUTTON_ID_MULTISHOT_ENABLED:
                cfgMultishotEnabled = false;
                break;
            case Constants.GUI_BUTTON_ID_MOTION_ENABLED:
                cfgMotionEnabled = false;
                break;
            case Constants.GUI_BUTTON_ID_LOCK_CONTROLS:
                cfgLockControls = false;
                break;
            case Constants.GUI_BUTTON_ID_HIDE_GUI:
                cfgHideGui = false;
                break;
            case Constants.GUI_BUTTON_ID_USE_FREE_CAMERA:
                useFreeCamera = false;
                break;
            case Constants.GUI_BUTTON_ID_FREE_CAMERA_WIDTH:
                freeCameraResolutionWidth = 1280;
                break;
            case Constants.GUI_BUTTON_ID_FREE_CAMERA_HEIGHT:
                freeCameraResolutionHeight = 720;
                break;
            case Constants.GUI_BUTTON_ID_INTERVAL:
                cfgInterval = 0;
                break;
            case Constants.GUI_BUTTON_ID_ZOOM:
                cfgZoom = 0;
                break;
            case Constants.GUI_BUTTON_ID_TIMER_SELECT:
                cfgSelectedTimer = 0;
                break;
            case Constants.GUI_BUTTON_ID_TIME_VIDEO_HOUR:
                cfgTimerVideo = cfgTimerVideo % 3600;
                break;
            case Constants.GUI_BUTTON_ID_TIME_VIDEO_MINUTE:
                tmp = cfgTimerVideo - (cfgTimerVideo % 3600);
                cfgTimerVideo = tmp + cfgTimerVideo % 60;
                break;
            case Constants.GUI_BUTTON_ID_TIME_VIDEO_SECOND:
                cfgTimerVideo = cfgTimerVideo - (cfgTimerVideo % 60);
                break;
            case Constants.GUI_BUTTON_ID_TIME_REAL_HOUR:
                cfgTimerRealTime = cfgTimerRealTime % 3600;
                break;
            case Constants.GUI_BUTTON_ID_TIME_REAL_MINUTE:
                tmp = cfgTimerRealTime - (cfgTimerRealTime % 3600);
                cfgTimerRealTime = tmp + cfgTimerRealTime % 60;
                break;
            case Constants.GUI_BUTTON_ID_TIME_REAL_SECOND:
                cfgTimerRealTime = cfgTimerRealTime - (cfgTimerRealTime % 60);
                break;
            case Constants.GUI_BUTTON_ID_TIME_NUM_SHOTS:
                cfgTimerNumShots = 0;
                break;
            case Constants.GUI_BUTTON_ID_BROWSE:
                cfgMultishotSavePath = getDefaultMultishotPath();
                break;
            case Constants.GUI_BUTTON_ID_IMG_FORMAT:
                cfgImgFormat = 0;
                break;
            case Constants.GUI_BUTTON_ID_GUI_POSITION:
                cfgGuiPosition = 0;
                break;
            case Constants.GUI_BUTTON_ID_MOTION_MODE:
                cfgMotionMode = 0;
                break;
            case Constants.GUI_BUTTON_ID_MOTION_SPEED:
                cfgMotionSpeed = 0;
                break;
            case Constants.GUI_BUTTON_ID_MOTION_X:
                cfgMotionX = 0;
                break;
            case Constants.GUI_BUTTON_ID_MOTION_Z:
                cfgMotionZ = 0;
                break;
            case Constants.GUI_BUTTON_ID_MOTION_Y:
                cfgMotionY = 0;
                break;
            case Constants.GUI_BUTTON_ID_ROTATION_YAW:
                cfgRotationYaw = 0;
                break;
            case Constants.GUI_BUTTON_ID_ROTATION_PITCH:
                cfgRotationPitch = 0;
                break;
            default:
                break;
        }

        writeToConfiguration();
    }

    private static int clampInt (int val, int inc, int min, int max)
    {
        return MathHelper.clamp(val + inc, min, max);
    }

    private static int normalizeIntWrap (int val, int inc, int min, int max)
    {
        val += inc;

        if (val < min)
        {
            val = max;
        }
        else if (val > max)
        {
            val = min;
        }

        return val;
    }

    public static String getDisplayString (int id)
    {
        String s = "";

        switch (id)
        {
            case Constants.GUI_BUTTON_ID_MULTISHOT_ENABLED:
                s = getDisplayStringBoolean(cfgMultishotEnabled);
                break;
            case Constants.GUI_BUTTON_ID_MOTION_ENABLED:
                s = getDisplayStringBoolean(cfgMotionEnabled);
                break;
            case Constants.GUI_BUTTON_ID_LOCK_CONTROLS:
                s = getDisplayStringBoolean(cfgLockControls);
                break;
            case Constants.GUI_BUTTON_ID_HIDE_GUI:
                s = getDisplayStringBoolean(cfgHideGui);
                break;
            case Constants.GUI_BUTTON_ID_USE_FREE_CAMERA:
                s = getDisplayStringBoolean(useFreeCamera);
                break;
            case Constants.GUI_BUTTON_ID_FREE_CAMERA_WIDTH:
                s = String.valueOf(freeCameraResolutionWidth);
                break;
            case Constants.GUI_BUTTON_ID_FREE_CAMERA_HEIGHT:
                s = String.valueOf(freeCameraResolutionHeight);
                break;
            case Constants.GUI_BUTTON_ID_INTERVAL:
                if (cfgInterval == 0) { s = I18n.format("multishot.gui.label.off"); }
                else { s = String.format("%.1fs", ((float)cfgInterval / 10)); }
                break;
            case Constants.GUI_BUTTON_ID_ZOOM:
                if (cfgZoom == 0) { s = I18n.format("multishot.gui.label.off"); }
                else { s = String.format("%.1fx", (float)cfgZoom / 10.0f); }
                break;
            case Constants.GUI_BUTTON_ID_TIMER_SELECT:
                if (cfgSelectedTimer == 0) { s = I18n.format("multishot.gui.label.off"); }
                else if (cfgSelectedTimer == 1) { s = I18n.format("multishot.gui.label.video"); }
                else if (cfgSelectedTimer == 2) { s = I18n.format("multishot.gui.label.real"); }
                else if (cfgSelectedTimer == 3) { s = I18n.format("multishot.gui.label.shots"); }
                break;
            case Constants.GUI_BUTTON_ID_TIME_VIDEO_HOUR:
                s = String.format("%02d",  cfgTimerVideo / 3600);
                break;
            case Constants.GUI_BUTTON_ID_TIME_VIDEO_MINUTE:
                s = String.format("%02d",  (cfgTimerVideo % 3600) / 60);
                break;
            case Constants.GUI_BUTTON_ID_TIME_VIDEO_SECOND:
                s = String.format("%02d",  cfgTimerVideo % 60);
                break;
            case Constants.GUI_BUTTON_ID_TIME_REAL_HOUR:
                s = String.format("%02d",  cfgTimerRealTime / 3600);
                break;
            case Constants.GUI_BUTTON_ID_TIME_REAL_MINUTE:
                s = String.format("%02d",  (cfgTimerRealTime % 3600) / 60);
                break;
            case Constants.GUI_BUTTON_ID_TIME_REAL_SECOND:
                s = String.format("%02d",  cfgTimerRealTime % 60);
                break;
            case Constants.GUI_BUTTON_ID_TIME_NUM_SHOTS:
                s = String.format("%010d", cfgTimerNumShots);
                break;
            case Constants.GUI_BUTTON_ID_IMG_FORMAT:
                if (cfgImgFormat == 0) { s = "PNG"; }
                else if (cfgImgFormat == 1) { s = "JPG, 75"; }
                else if (cfgImgFormat == 2) { s = "JPG, 80"; }
                else if (cfgImgFormat == 3) { s = "JPG, 85"; }
                else if (cfgImgFormat == 4) { s = "JPG, 90"; }
                else if (cfgImgFormat == 5) { s = "JPG, 95"; }
                break;
            case Constants.GUI_BUTTON_ID_GUI_POSITION:
                if (cfgGuiPosition == 0) { s = I18n.format("multishot.gui.label.top") + " " + I18n.format("multishot.gui.label.right"); }
                else if (cfgGuiPosition == 1) { s = I18n.format("multishot.gui.label.bottom") + " " + I18n.format("multishot.gui.label.right"); }
                else if (cfgGuiPosition == 2) { s = I18n.format("multishot.gui.label.bottom") + " " + I18n.format("multishot.gui.label.left"); }
                else if (cfgGuiPosition == 3) { s = I18n.format("multishot.gui.label.top") + " " + I18n.format("multishot.gui.label.left"); }
                break;
            case Constants.GUI_BUTTON_ID_MOTION_MODE:
                if (cfgMotionMode == Constants.MOTION_MODE_LINEAR) { s = I18n.format("multishot.gui.label.motion.mode.linear"); }
                else if (cfgMotionMode == Constants.MOTION_MODE_CIRCLE) { s = I18n.format("multishot.gui.label.motion.mode.circle"); }
                else if (cfgMotionMode == Constants.MOTION_MODE_ELLIPSE) { s = I18n.format("multishot.gui.label.motion.mode.ellipse"); }
                else if (cfgMotionMode == Constants.MOTION_MODE_PATH_LINEAR) { s = I18n.format("multishot.gui.label.motion.mode.path.linear"); }
                else if (cfgMotionMode == Constants.MOTION_MODE_PATH_SMOOTH) { s = I18n.format("multishot.gui.label.motion.mode.path.smooth"); }
                break;
            case Constants.GUI_BUTTON_ID_MOTION_SPEED:
                s = getDisplayStringSpeed(cfgMotionSpeed);
                break;
            case Constants.GUI_BUTTON_ID_MOTION_X:
                s = getDisplayStringSpeed(cfgMotionX);
                break;
            case Constants.GUI_BUTTON_ID_MOTION_Z:
                s = getDisplayStringSpeed(cfgMotionZ);
                break;
            case Constants.GUI_BUTTON_ID_MOTION_Y:
                s = getDisplayStringSpeed(cfgMotionY);
                break;
            case Constants.GUI_BUTTON_ID_ROTATION_YAW:
                s = getDisplayStringRotation(cfgRotationYaw);
                break;
            case Constants.GUI_BUTTON_ID_ROTATION_PITCH:
                s = getDisplayStringRotation(cfgRotationPitch);
                break;
            case Constants.GUI_FIELD_ID_SAVE_PATH:
                s = cfgMultishotSavePath;
                    break;
            default:
                break;
        }

        return s;
    }

    private static String getDisplayStringBoolean (boolean val)
    {
        if (val == true)
        {
            return I18n.format("multishot.gui.label.on");
        }
        return I18n.format("multishot.gui.label.off");
    }

    private static String getDisplayStringSpeed (int val)
    {
        String s;
        if (val == 0)
        {
            s = "OFF";
        }
        else
        {
            s = String.format("%.3f m/s", ((float)val / 1000));
        }
        return s;
    }

    private static String getDisplayStringRotation (int val)
    {
        String s;
        if (val == 0)
        {
            s = "OFF";
        }
        else
        {
            s = String.format("%.2f\u00b0/s", ((float)val / 100));
        }
        return s;
    }

    public static int getActiveTimer()
    {
        return cfgSelectedTimer;
    }

    public static int getActiveTimerNumShots()
    {
        if (cfgInterval > 0)
        {
            if (cfgSelectedTimer == 1) // Video time
            {
                return (cfgTimerVideo * 24); // Assume 24 FPS video
            }
            else if (cfgSelectedTimer == 2) // Real time
            {
                return (cfgTimerRealTime * 10) / cfgInterval; // Interval is in 1/10 of a second
            }
            else if (cfgSelectedTimer == 3) // Number of shots
            {
                return cfgTimerNumShots;
            }
        }

        return 0;
    }

    public static boolean getMultishotEnabled()
    {
        return cfgMultishotEnabled;
    }

    public static boolean getMotionEnabled()
    {
        return cfgMotionEnabled;
    }

    public static boolean getControlsLocked()
    {
        return cfgLockControls;
    }

    public static boolean getHideGui()
    {
        return cfgHideGui;
    }

    public static int getGuiPosition()
    {
        return cfgGuiPosition;
    }

    public static int getGuiOffsetX()
    {
        return cfgGuiOffsetX;
    }

    public static int getGuiOffsetY()
    {
        return cfgGuiOffsetY;
    }

    public static int getZoom()
    {
        return cfgZoom;
    }

    public static boolean getUseFreeCamera()
    {
        return useFreeCamera;
    }

    public static int getFreeCameraWidth()
    {
        return freeCameraResolutionWidth;
    }

    public static int getFreeCameraHeight()
    {
        return freeCameraResolutionHeight;
    }

    public static String getSavePath()
    {
        return cfgMultishotSavePath;
    }

    public static File getPointsDir()
    {
        return pointsDir;
    }

    public static int getImgFormat()
    {
        return cfgImgFormat;
    }

    public static int getInterval()
    {
        return cfgInterval;
    }

    public static int getMotionMode()
    {
        return cfgMotionMode;
    }

    public static int getMotionSpeed()
    {
        return cfgMotionSpeed;
    }

    public static double getMotionX()
    {
        return (double)cfgMotionX / 1000.0 / 20.0;
    }

    public static double getMotionZ()
    {
        return (double)cfgMotionZ / 1000.0 / 20.0;
    }

    public static double getMotionY()
    {
        return (double)cfgMotionY / 1000.0 / 20.0;
    }

    public static float getRotationYaw()
    {
        return (float)cfgRotationYaw / 100.0f / 20.0f;
    }

    public static float getRotationPitch()
    {
        return (float)cfgRotationPitch / 100.0f / 20.0f;
    }

    public static float getFOV()
    {
        return 70.0f - ((float) cfgZoom * 69.9f / 100.0f);
    }
}
