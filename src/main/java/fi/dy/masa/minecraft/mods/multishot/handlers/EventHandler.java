package fi.dy.masa.minecraft.mods.multishot.handlers;

import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import fi.dy.masa.minecraft.mods.multishot.config.Configs;
import fi.dy.masa.minecraft.mods.multishot.gui.ScreenGeneric;
import fi.dy.masa.minecraft.mods.multishot.motion.Motion;
import fi.dy.masa.minecraft.mods.multishot.reference.Constants;
import fi.dy.masa.minecraft.mods.multishot.state.State;
import fi.dy.masa.minecraft.mods.multishot.worker.RecordingHandler;

public class EventHandler
{
    private Minecraft mc;
    private static final KeyBinding KEY_MENU     = new KeyBinding(Constants.KEYBIND_MENU,      Constants.KEYBIND_DEFAULT_MENU,      Constants.KEYBIND_CATEGORY_MULTISHOT);
    private static final KeyBinding KEY_START    = new KeyBinding(Constants.KEYBIND_STARTSTOP, Constants.KEYBIND_DEFAULT_STARTSTOP, Constants.KEYBIND_CATEGORY_MULTISHOT);
    private static final KeyBinding KEY_MOTION   = new KeyBinding(Constants.KEYBIND_MOTION,    Constants.KEYBIND_DEFAULT_MOTION,    Constants.KEYBIND_CATEGORY_MULTISHOT);
    private static final KeyBinding KEY_PAUSE    = new KeyBinding(Constants.KEYBIND_PAUSE,     Constants.KEYBIND_DEFAULT_PAUSE,     Constants.KEYBIND_CATEGORY_MULTISHOT);
    private static final KeyBinding KEY_LOCK     = new KeyBinding(Constants.KEYBIND_LOCK,      Constants.KEYBIND_DEFAULT_LOCK,      Constants.KEYBIND_CATEGORY_MULTISHOT);
    private static final KeyBinding KEY_HIDE_GUI = new KeyBinding(Constants.KEYBIND_HIDEGUI,   Constants.KEYBIND_DEFAULT_HIDEGUI,   Constants.KEYBIND_CATEGORY_MULTISHOT);
    
    public EventHandler()
    {
        this.mc = Minecraft.getMinecraft();

        ClientRegistry.registerKeyBinding(KEY_MENU);
        ClientRegistry.registerKeyBinding(KEY_START);
        ClientRegistry.registerKeyBinding(KEY_MOTION);
        ClientRegistry.registerKeyBinding(KEY_PAUSE);
        ClientRegistry.registerKeyBinding(KEY_LOCK);
        ClientRegistry.registerKeyBinding(KEY_HIDE_GUI);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END && this.mc.isGamePaused() == false)
        {
            // Prevent mouse input while recording and controls locked, and always while moving
            if (Configs.getUseFreeCamera() == false &&
                ((State.getRecording() && State.getControlsLocked()) || State.getMotion()))
            {
                this.mc.setIngameNotInFocus();
            }

            RecordingHandler.getInstance().multishotScheduler();

            // Move the player. Note: the pause key doesn't have an effect if not recording
            if (State.getMotion() && State.getPaused() == false)
            {
                Motion.getMotion().moveCameraEntity(this.mc.player);
            }
        }
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event)
    {
        // In-game (no GUI open)
        if (this.mc.currentScreen != null)
        {
            return;
        }

        EntityPlayer player = this.mc.player;
        Motion motion = Motion.getMotion();

        // M: Toggle recording
        if (KEY_START.isPressed() && Configs.getMultishotEnabled())
        {
            RecordingHandler.getInstance().toggleRecording();
        }
        // N: Toggle motion; Don't allow starting motion while already recording without motion
        else if (KEY_MOTION.isPressed() && Configs.getMotionEnabled()
                && (State.getRecording() == false || State.getMotion()))
        {
            // CTRL + N: Move to path start position (path modes only)
            if (GuiScreen.isCtrlKeyDown())
            {
                motion.toggleMoveToStartPoint(player);
            }
            // SHIFT + N: Move to the closest (= hilighted) path point (path modes only)
            else if (GuiScreen.isShiftKeyDown())
            {
                motion.toggleMoveToClosestPoint(player);
            }
            // N: Toggle motion
            else
            {
                motion.toggleMotion(player);
            }
        }
        // The Pause key doubles as the "set point" key for the motion modes, when used outside of recording mode
        else if (KEY_PAUSE.isPressed())
        {
            if (State.getRecording() || State.getMotion())
            {
                // Reset the screenshot scheduler when unpausing, so that the shot interval should "preserve"
                // correctly around the pause period.
                if (State.getRecording() && State.getPaused())
                {
                    RecordingHandler.getInstance().resetScheduler();
                }

                State.togglePaused();
            }
            else
            {
                // DEL + HOME + P: Remove center point
                if (isDeleteKeyDown() && isHomeKeyDown())
                {
                    motion.removeCenterPoint();
                }
                // DEL + END + P: Remove target point
                else if (isDeleteKeyDown() && isEndKeyDown())
                {
                    motion.removeTargetPoint();
                }
                // DEL + CTRL + P: Remove all points
                else if (isDeleteKeyDown() && GuiScreen.isCtrlKeyDown())
                {
                    motion.removeAllPoints();
                }
                // INSERT + HOME + P: Insert a path point BEFORE the hilighted point
                else if (isInsertKeyDown() && isHomeKeyDown())
                {
                    motion.insertPathPoint(player, true);
                }
                // INSERT + P: Insert a path point AFTER the hilighted point
                else if (isInsertKeyDown())
                {
                    motion.insertPathPoint(player, false);
                }
                // HOME + END + P: Reverse the active path's traveling direction
                else if (isHomeKeyDown() && isEndKeyDown())
                {
                    motion.reversePath();
                }
                // HOME + P: Set center point
                else if (isHomeKeyDown())
                {
                    motion.setCenterPointFromCurrentPos(player);
                }
                // END + P: Set target point
                else if (isEndKeyDown())
                {
                    motion.setTargetPointFromCurrentPos(player);
                }
                // DEL + P: Remove nearest path point (path modes only)
                else if (isDeleteKeyDown())
                {
                    motion.removeNearestPathPoint(player);
                }
                // CTRL + P: Move/replace a previously "stored" path point with the current location
                else if (GuiScreen.isCtrlKeyDown())
                {
                    motion.replaceStoredPathPoint(player);
                }
                // UP + DOWN + P: Reload current active path from file
                else if (isUpKeyDown() && isDownKeyDown())
                {
                    motion.reloadCurrentPath();
                }
                // UP + P: Select the next path (= +1)
                else if (isUpKeyDown())
                {
                    motion.selectNextPath();
                }
                // DOWN + P: Select the previous path (= -1)
                else if (isDownKeyDown())
                {
                    motion.selectPreviousPath();
                }
                // P: Add a path point (path mode) or ellipse longer semi-axis end point (ellipse mode)
                else
                {
                    motion.addPointFromCurrentPos(player);
                }
            }
        }
        else if (KEY_HIDE_GUI.isPressed())
        {
            State.toggleHideGui();
            // Also update the configs to reflect the new state
            Configs.changeValue(Constants.GUI_BUTTON_ID_HIDE_GUI, 0, 0);
        }
        else if (KEY_LOCK.isPressed())
        {
            State.toggleControlsLocked();
            // Also update the configs to reflect the new state
            Configs.changeValue(Constants.GUI_BUTTON_ID_LOCK_CONTROLS, 0, 0);
        }
        else
        {
            // Lock the keys when requested while recording, and also always in motion mode
            if (Configs.getUseFreeCamera() == false &&
                ((State.getRecording() && State.getControlsLocked()) || State.getMotion()))
            {
                KeyBinding.unPressAllKeys();
            }
        }

        // Check if we need to unlock the controls, aka. return the focus to the game.
        // The locking is done in the PlayerTickHandler at every tick, when recording or motion is enabled.
        if (Configs.getUseFreeCamera() == false &&
            (State.getMotion() == false && State.getRecording() == false) || State.getControlsLocked() == false)
        {
            this.mc.setIngameFocus();
        }
        // The gui screen needs to be opened after we possibly return the focus to the game (see above),
        // otherwise the currentScreen will get reset to null and the menu won't stay open
        if (KEY_MENU.isPressed() && State.getRecording() == false && State.getMotion() == false)
        {
            // CTRL + menu key: "cut" a path point (= store the index of the currently closest path point) for moving it
            if (GuiScreen.isCtrlKeyDown())
            {
                motion.storeNearestPathPointIndex(player);
            }
            else
            {
                this.mc.displayGuiScreen(new ScreenGeneric());
            }
        }
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
