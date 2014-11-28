package fi.dy.masa.minecraft.mods.multishot.reference;

import org.lwjgl.input.Keyboard;

public class MsConstants
{
    public static final String KEYBIND_MENU         = "key.multishot.menu";
    public static final String KEYBIND_STARTSTOP    = "key.multishot.startstop";
    public static final String KEYBIND_MOTION       = "key.multishot.motion";
    public static final String KEYBIND_PAUSE        = "key.multishot.pause";
    public static final String KEYBIND_LOCK         = "key.multishot.lock";
    public static final String KEYBIND_HIDEGUI      = "key.multishot.hidegui";

    public static final int KEYBIND_DEFAULT_MENU        = Keyboard.KEY_K;
    public static final int KEYBIND_DEFAULT_STARTSTOP   = Keyboard.KEY_M;
    public static final int KEYBIND_DEFAULT_MOTION      = Keyboard.KEY_N;
    public static final int KEYBIND_DEFAULT_PAUSE       = Keyboard.KEY_P;
    public static final int KEYBIND_DEFAULT_LOCK        = Keyboard.KEY_L;
    public static final int KEYBIND_DEFAULT_HIDEGUI     = Keyboard.KEY_H;

    public static final String KEYBIND_CATEGORY_MULTISHOT   = "category.multishot";

    public static final int GUI_BUTTON_ID_SCREEN_GENERIC    = 1;
    public static final int GUI_BUTTON_ID_SCREEN_MOTION     = 2;
    public static final int GUI_BUTTON_ID_BACK_TO_GAME      = 3;

    public static final int GUI_BUTTON_ID_MULTISHOT_ENABLED = 10;
    public static final int GUI_BUTTON_ID_MOTION_ENABLED    = 11;
    public static final int GUI_BUTTON_ID_LOCK_CONTROLS     = 12;
    public static final int GUI_BUTTON_ID_HIDE_GUI          = 13;
    public static final int GUI_BUTTON_ID_INTERVAL          = 14;
    public static final int GUI_BUTTON_ID_ZOOM              = 15;
    public static final int GUI_BUTTON_ID_BROWSE            = 16;
    public static final int GUI_BUTTON_ID_IMG_FORMAT        = 17;
    public static final int GUI_BUTTON_ID_LOAD_DEFAULTS     = 18;
    public static final int GUI_BUTTON_ID_GUI_POSITION      = 19;

    public static final int GUI_BUTTON_ID_MOTION_X          = 30;
    public static final int GUI_BUTTON_ID_MOTION_Z          = 31;
    public static final int GUI_BUTTON_ID_MOTION_Y          = 32;
    public static final int GUI_BUTTON_ID_ROTATION_YAW      = 33;
    public static final int GUI_BUTTON_ID_ROTATION_PITCH    = 34;
    public static final int GUI_BUTTON_ID_MOTION_MODE       = 35;
    public static final int GUI_BUTTON_ID_MOTION_SPEED      = 36;

    public static final int GUI_BUTTON_ID_TIMER_SELECT      = 50;
    public static final int GUI_BUTTON_ID_TIME_VIDEO_HOUR   = 51;
    public static final int GUI_BUTTON_ID_TIME_VIDEO_MINUTE = 52;
    public static final int GUI_BUTTON_ID_TIME_VIDEO_SECOND = 53;
    public static final int GUI_BUTTON_ID_TIME_REAL_HOUR    = 54;
    public static final int GUI_BUTTON_ID_TIME_REAL_MINUTE  = 55;
    public static final int GUI_BUTTON_ID_TIME_REAL_SECOND  = 56;
    public static final int GUI_BUTTON_ID_TIME_NUM_SHOTS    = 57;

    public static final int GUI_FIELD_ID_SAVE_PATH          = 70;

    public static final int MOTION_MODE_LINEAR              = 0; // Motion mode values, used in the config file
    public static final int MOTION_MODE_CIRCLE              = 1;
    public static final int MOTION_MODE_ELLIPSE             = 2;
    public static final int MOTION_MODE_PATH_LINEAR         = 3;
    public static final int MOTION_MODE_PATH_SMOOTH         = 4;
}
