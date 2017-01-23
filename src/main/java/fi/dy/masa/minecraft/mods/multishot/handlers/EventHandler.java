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
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import fi.dy.masa.minecraft.mods.multishot.config.Configs;
import fi.dy.masa.minecraft.mods.multishot.gui.ScreenGeneric;
import fi.dy.masa.minecraft.mods.multishot.motion.Motion;
import fi.dy.masa.minecraft.mods.multishot.reference.Constants;
import fi.dy.masa.minecraft.mods.multishot.state.State;
import fi.dy.masa.minecraft.mods.multishot.worker.RecordingHandler;

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
            if ((State.getRecording() && State.getControlsLocked()) || State.getMotion())
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
        if (keyMultishotStart.isPressed() && Configs.getConfig().getMultishotEnabled())
        {
            RecordingHandler.getInstance().toggleRecording();
        }
        // N: Toggle motion; Don't allow starting motion while already recording without motion
        else if (keyMultishotMotion.isPressed() && Configs.getConfig().getMotionEnabled()
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
        else if (keyMultishotPause.isPressed())
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
        else if (keyMultishotHideGUI.isPressed())
        {
            State.toggleHideGui();
            // Also update the configs to reflect the new state
            Configs.getConfig().changeValue(Constants.GUI_BUTTON_ID_HIDE_GUI, 0, 0);
        }
        else if (keyMultishotLock.isPressed())
        {
            State.toggleControlsLocked();
            // Also update the configs to reflect the new state
            Configs.getConfig().changeValue(Constants.GUI_BUTTON_ID_LOCK_CONTROLS, 0, 0);
        }
        else
        {
            // Lock the keys when requested while recording, and also always in motion mode
            if ((State.getRecording() && State.getControlsLocked()) || State.getMotion())
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
        if (keyMultishotMenu.isPressed() && State.getRecording() == false && State.getMotion() == false)
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

    @SubscribeEvent
    // for debugging: (RenderGameOverlayEvent event)
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if (event.phase == Phase.START || this.mc.isGamePaused())
        {
            return;
        }

        if (Configs.getConfig().getUseFreeCamera())
        {
            RecordingHandler.getInstance().renderFreeCamera();
            return;
        }

        Motion motion = Motion.getMotion();

        // "The interpolated method", see MsMotion.reOrientPlayerToAngle() and MsMotion.toggleMotion() for the other bits of this code
        // Update the player rotation and pitch here in smaller steps, so that the camera doesn't jitter so terribly
        if (State.getMotion() && State.getPaused() == false && motion.getDoPlayerReorientation())
        {
            float partialTicks = event.renderTickTime;
            float yaw = motion.prevYaw + (motion.yawIncrement * partialTicks);
            float pitch = motion.prevPitch + (motion.pitchIncrement * partialTicks);
            //if (yaw > 180.0f) { yaw -= 360.0f; }
            //else if (yaw < -180.0f) { yaw += 360.0f; }

            EntityPlayer p = this.mc.player;
            p.rotationYaw = yaw;
            p.prevRotationYaw = yaw;
            p.rotationPitch = pitch;
            p.prevRotationPitch = pitch;
        }

/*
        // FIXME debug stuff:
        // Note: the text will only get rendered when using the RenderGameOverlayEvent, not using the RenderWorldLastEvent
        // Then again, we can't use RenderGameOverlayEvent to actually do the rotation stuff, because that event won't
        // happen when the HUD is hidden (with F1).
        if (MsScreenBase.isCtrlKeyDown())
        {
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            String s1 = String.format("ms prevYaw: %f", MsClassReference.getMotion().prevYaw);
            String s2 = String.format("mc prevYaw: %f", this.mc.thePlayer.prevRotationYaw);
            String s3 = String.format("rotationYaw: %f", this.mc.thePlayer.rotationYaw);
            String s4 = String.format("yawInc: %f", MsClassReference.getMotion().yawIncrement);
            String s5 = String.format("yaw: %f", yaw);
            String s6 = String.format("MC yaw: %f", this.mc.thePlayer.rotationYaw);
            String s7 = String.format("MC pitch: %f", this.mc.thePlayer.rotationPitch);
            //String s6 = String.format("targetAtan2: %f", MsClassReference.getMotion().targetAtan2);
            //String s7 = String.format("targetAtan2Deg: %f", MsClassReference.getMotion().targetAtan2Deg);
            this.mc.fontRenderer.drawStringWithShadow(s1, 5, 20, 0xffffffff);
            this.mc.fontRenderer.drawStringWithShadow(s2, 5, 30, 0xffffffff);
            this.mc.fontRenderer.drawStringWithShadow(s3, 5, 40, 0xffffffff);
            this.mc.fontRenderer.drawStringWithShadow(s4, 5, 50, 0xffffffff);
            this.mc.fontRenderer.drawStringWithShadow(s5, 5, 60, 0xffffffff);
            this.mc.fontRenderer.drawStringWithShadow(s6, 5, 70, 0xffffffff);
            this.mc.fontRenderer.drawStringWithShadow(s7, 5, 80, 0xffffffff);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
            //System.out.printf("ms prevYaw: %f mc prevYaw: %f yawInc: %f yaw: %f\n", MsClassReference.getMotion().prevYaw, this.mc.thePlayer.prevRotationYaw, MsClassReference.getMotion().yawIncrement, yaw);
        }
*/
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
