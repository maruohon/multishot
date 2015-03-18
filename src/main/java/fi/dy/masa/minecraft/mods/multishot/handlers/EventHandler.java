package fi.dy.masa.minecraft.mods.multishot.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.Util;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.input.Keyboard;

import fi.dy.masa.minecraft.mods.multishot.config.Configs;
import fi.dy.masa.minecraft.mods.multishot.gui.ScreenGeneric;
import fi.dy.masa.minecraft.mods.multishot.motion.Motion;
import fi.dy.masa.minecraft.mods.multishot.reference.Constants;
import fi.dy.masa.minecraft.mods.multishot.state.State;
import fi.dy.masa.minecraft.mods.multishot.worker.MsThread;
import fi.dy.masa.minecraft.mods.multishot.worker.RecordingHandler;

@SideOnly(Side.CLIENT)
public class EventHandler
{
    private Minecraft mc = null;
    private static KeyBinding keyMultishotMenu = null;
    private static KeyBinding keyMultishotStart = null;
    private static KeyBinding keyMultishotMotion = null;
    private static KeyBinding keyMultishotPause = null;
    private static KeyBinding keyMultishotLock = null;
    private static KeyBinding keyMultishotHideGUI = null;
    
    public EventHandler()
    {
        this.mc = Minecraft.getMinecraft();

        keyMultishotMenu    = new KeyBinding(Constants.KEYBIND_MENU,      Constants.KEYBIND_DEFAULT_MENU,       Constants.KEYBIND_CATEGORY_MULTISHOT);
        keyMultishotStart   = new KeyBinding(Constants.KEYBIND_STARTSTOP, Constants.KEYBIND_DEFAULT_STARTSTOP,  Constants.KEYBIND_CATEGORY_MULTISHOT);
        keyMultishotMotion  = new KeyBinding(Constants.KEYBIND_MOTION,    Constants.KEYBIND_DEFAULT_MOTION,     Constants.KEYBIND_CATEGORY_MULTISHOT);
        keyMultishotPause   = new KeyBinding(Constants.KEYBIND_PAUSE,     Constants.KEYBIND_DEFAULT_PAUSE,      Constants.KEYBIND_CATEGORY_MULTISHOT);
        keyMultishotLock    = new KeyBinding(Constants.KEYBIND_LOCK,      Constants.KEYBIND_DEFAULT_LOCK,       Constants.KEYBIND_CATEGORY_MULTISHOT);
        keyMultishotHideGUI = new KeyBinding(Constants.KEYBIND_HIDEGUI,   Constants.KEYBIND_DEFAULT_HIDEGUI,    Constants.KEYBIND_CATEGORY_MULTISHOT);

        ClientRegistry.registerKeyBinding(keyMultishotMenu);
        ClientRegistry.registerKeyBinding(keyMultishotStart);
        ClientRegistry.registerKeyBinding(keyMultishotMotion);
        ClientRegistry.registerKeyBinding(keyMultishotPause);
        ClientRegistry.registerKeyBinding(keyMultishotLock);
        ClientRegistry.registerKeyBinding(keyMultishotHideGUI);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END && this.mc.isGamePaused() == false)
        {
            // Prevent mouse input while recording and controls locked, and always while moving
            if ((State.getRecording() == true && State.getControlsLocked() == true) || State.getMotion() == true)
            {
                this.mc.setIngameNotInFocus();
            }

            RecordingHandler.getInstance().multishotScheduler();

            // Move the player. Note: the pause key doesn't have an effect if not recording
            if (State.getMotion() == true && (State.getPaused() == false || State.getRecording() == false))
            {
                Motion.getMotion().movePlayer(this.mc.thePlayer);
            }

            // Print the queued messages from the screenshot saving thread, if any
            MsThread.printGuiMessages();
        }
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event)
    {
        // In-game (no GUI open)
        if (this.mc.currentScreen == null)
        {
            Motion motion = Motion.getMotion();

            // M: Toggle recording
            if (keyMultishotStart.isPressed() == true && Configs.getConfig().getMultishotEnabled() == true)
            {
                RecordingHandler.getInstance().toggleRecording();
            }
            else if (keyMultishotMotion.isPressed() == true && Configs.getConfig().getMotionEnabled() == true)
            {
                // CTRL + N: Move to path start position (path modes only)
                if (isCtrlKeyDown() == true)
                {
                    motion.toggleMoveToStartPoint(this.mc.thePlayer);
                }
                // SHIFT + N: Move to the closest (= hilighted) path point (path modes only)
                else if (isShiftKeyDown() == true)
                {
                    motion.toggleMoveToClosestPoint(this.mc.thePlayer);
                }
                // N: Toggle motion
                else
                {
                    motion.toggleMotion(this.mc.thePlayer);
                }
            }
            // The Pause key doubles as the "set point" key for the motion modes, when used outside of recording mode
            else if (keyMultishotPause.isPressed() == true)
            {
                if (State.getRecording() == true)
                {
                    // Reset the screenshot scheduler when unpausing, so that the shot interval should "preserve"
                    // correctly around the pause period.
                    if (State.getPaused() == true)
                    {
                        RecordingHandler.getInstance().resetScheduler();
                    }
                    State.togglePaused();
                }
                else
                {
                    // DEL + HOME + P: Remove center point
                    if (isDeleteKeyDown() == true && isHomeKeyDown() == true)
                    {
                        motion.removeCenterPoint();
                    }
                    // DEL + END + P: Remove target point
                    else if (isDeleteKeyDown() == true && isEndKeyDown() == true)
                    {
                        motion.removeTargetPoint();
                    }
                    // DEL + CTRL + P: Remove all points
                    else if (isDeleteKeyDown() == true && isCtrlKeyDown() == true)
                    {
                        motion.removeAllPoints();
                    }
                    // INSERT + HOME + P: Insert a path point BEFORE the hilighted point
                    else if (isInsertKeyDown() == true && isHomeKeyDown() == true)
                    {
                        motion.insertPathPoint(this.mc.thePlayer, true);
                    }
                    // INSERT + P: Insert a path point AFTER the hilighted point
                    else if (isInsertKeyDown() == true)
                    {
                        motion.insertPathPoint(this.mc.thePlayer, false);
                    }
                    // HOME + END + P: Reverse the active path's traveling direction
                    else if (isHomeKeyDown() == true && isEndKeyDown() == true)
                    {
                        motion.reversePath();
                    }
                    // HOME + P: Set center point
                    else if (isHomeKeyDown() == true)
                    {
                        motion.setCenterPointFromCurrentPos(this.mc.thePlayer);
                    }
                    // END + P: Set target point
                    else if (isEndKeyDown() == true)
                    {
                        motion.setTargetPointFromCurrentPos(this.mc.thePlayer);
                    }
                    // DEL + P: Remove nearest path point (path modes only)
                    else if (isDeleteKeyDown() == true)
                    {
                        motion.removeNearestPathPoint(this.mc.thePlayer);
                    }
                    // CTRL + P: Move/replace a previously "stored" path point with the current location
                    else if (isCtrlKeyDown() == true)
                    {
                        motion.replaceStoredPathPoint(this.mc.thePlayer);
                    }
                    // UP + DOWN + P: Reload current active path from file
                    else if (isUpKeyDown() == true && isDownKeyDown() == true)
                    {
                        motion.reloadCurrentPath();
                    }
                    // UP + P: Select the next path (= +1)
                    else if (isUpKeyDown() == true)
                    {
                        motion.selectNextPath();
                    }
                    // DOWN + P: Select the previous path (= -1)
                    else if (isDownKeyDown() == true)
                    {
                        motion.selectPreviousPath();
                    }
                    // P: Add a path point (path mode) or ellipse longer semi-axis end point (ellipse mode)
                    else
                    {
                        motion.addPointFromCurrentPos(this.mc.thePlayer);
                    }
                }
            }
            else if (keyMultishotHideGUI.isPressed() == true)
            {
                State.toggleHideGui();
                // Also update the configs to reflect the new state
                Configs.getConfig().changeValue(Constants.GUI_BUTTON_ID_HIDE_GUI, 0, 0);
            }
            else if (keyMultishotLock.isPressed() == true)
            {
                State.toggleControlsLocked();
                // Also update the configs to reflect the new state
                Configs.getConfig().changeValue(Constants.GUI_BUTTON_ID_LOCK_CONTROLS, 0, 0);
            }
            else
            {
                // Lock the keys when requested while recording, and also always in motion mode
                if ((State.getRecording() == true && State.getControlsLocked() == true) || State.getMotion() == true)
                {
                    KeyBinding.unPressAllKeys();
                }
            }

            // Check if we need to unlock the controls, aka. return the focus to the game.
            // The locking is done in the PlayerTickHandler at every tick, when recording or motion is enabled.
            if ((State.getMotion() == false && State.getRecording() == false) ||
                    State.getControlsLocked() == false)
            {
                this.mc.setIngameFocus();
            }
            // The gui screen needs to be opened after we possibly return the focus to the game (see above),
            // otherwise the currentScreen will get reset to null and the menu won't stay open
            if (keyMultishotMenu.isPressed() == true && State.getRecording() == false && State.getMotion() == false)
            {
                // CTRL + menu key: "cut" a path point (= store the index of the currently closest path point) for moving it
                if (isCtrlKeyDown() == true)
                {
                    motion.storeNearestPathPointIndex(this.mc.thePlayer);
                }
                else
                {
                    this.mc.displayGuiScreen(new ScreenGeneric());
                }
            }
        }
    }

    public static boolean isCtrlKeyDown()
    {
        boolean flag = Keyboard.isKeyDown(28) && Keyboard.getEventCharacter() == 0;
        return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) || Util.getOSType() == Util.EnumOS.OSX && (flag || Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220));
    }

    public static boolean isShiftKeyDown()
    {
        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
    }

    public static boolean isDeleteKeyDown()
    {
        return Keyboard.isKeyDown(Keyboard.KEY_DELETE);
    }

    public static boolean isHomeKeyDown()
    {
        return Keyboard.isKeyDown(Keyboard.KEY_HOME);
    }

    public static boolean isEndKeyDown()
    {
        return Keyboard.isKeyDown(Keyboard.KEY_END);
    }

    public static boolean isUpKeyDown()
    {
        return Keyboard.isKeyDown(Keyboard.KEY_UP);
    }

    public static boolean isDownKeyDown()
    {
        return Keyboard.isKeyDown(Keyboard.KEY_DOWN);
    }

    public static boolean isInsertKeyDown()
    {
        return Keyboard.isKeyDown(Keyboard.KEY_INSERT);
    }
}
