package fi.dy.masa.minecraft.mods.multishot.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.Util;
import net.minecraft.util.Util.EnumOS;
import net.minecraftforge.common.config.Configuration;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.InputEvent.MouseInputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.config.MsConfigs;
import fi.dy.masa.minecraft.mods.multishot.gui.MsScreenGeneric;
import fi.dy.masa.minecraft.mods.multishot.motion.MsMotion;
import fi.dy.masa.minecraft.mods.multishot.reference.MsConstants;
import fi.dy.masa.minecraft.mods.multishot.state.MsClassReference;
import fi.dy.masa.minecraft.mods.multishot.state.MsState;
import fi.dy.masa.minecraft.mods.multishot.worker.MsRecordingHandler;

@SideOnly(Side.CLIENT)
public class MsKeyEvent
{
	private Minecraft mc = null;
	private MsScreenGeneric multishotScreenConfigsGeneric = null;
	private static KeyBinding keyMultishotMenu = null;
	private static KeyBinding keyMultishotStart = null;
	private static KeyBinding keyMultishotMotion = null;
	private static KeyBinding keyMultishotPause = null;
	private static KeyBinding keyMultishotLock = null;
	private static KeyBinding keyMultishotHideGUI = null;
	
	public MsKeyEvent(Minecraft par1mc, Configuration cfg, MsConfigs msCfg, MsMotion msMotion)
	{
		this.mc = par1mc;
		this.multishotScreenConfigsGeneric = new MsScreenGeneric(this.mc.currentScreen);

		keyMultishotMenu	= new KeyBinding(MsConstants.KEYBIND_MENU,		MsConstants.KEYBIND_DEFAULT_MENU,		MsConstants.KEYBIND_CATEGORY_MULTISHOT);
		keyMultishotStart	= new KeyBinding(MsConstants.KEYBIND_STARTSTOP,	MsConstants.KEYBIND_DEFAULT_STARTSTOP,	MsConstants.KEYBIND_CATEGORY_MULTISHOT);
		keyMultishotMotion	= new KeyBinding(MsConstants.KEYBIND_MOTION,	MsConstants.KEYBIND_DEFAULT_MOTION,		MsConstants.KEYBIND_CATEGORY_MULTISHOT);
		keyMultishotPause	= new KeyBinding(MsConstants.KEYBIND_PAUSE,		MsConstants.KEYBIND_DEFAULT_PAUSE,		MsConstants.KEYBIND_CATEGORY_MULTISHOT);
		keyMultishotLock	= new KeyBinding(MsConstants.KEYBIND_LOCK,		MsConstants.KEYBIND_DEFAULT_LOCK,		MsConstants.KEYBIND_CATEGORY_MULTISHOT);
		keyMultishotHideGUI	= new KeyBinding(MsConstants.KEYBIND_HIDEGUI,	MsConstants.KEYBIND_DEFAULT_HIDEGUI,	MsConstants.KEYBIND_CATEGORY_MULTISHOT);

		ClientRegistry.registerKeyBinding(keyMultishotMenu);
		ClientRegistry.registerKeyBinding(keyMultishotStart);
		ClientRegistry.registerKeyBinding(keyMultishotMotion);
		ClientRegistry.registerKeyBinding(keyMultishotPause);
		ClientRegistry.registerKeyBinding(keyMultishotLock);
		ClientRegistry.registerKeyBinding(keyMultishotHideGUI);
	}

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event)
	{
		// In-game (no GUI open)
		if (this.mc.currentScreen == null)
		{
			if (keyMultishotStart.isPressed() == true && MsClassReference.getMsConfigs().getMultishotEnabled() == true)
			{
				MsRecordingHandler.toggleRecording();
			}
			else if (keyMultishotMotion.isPressed() == true && MsClassReference.getMsConfigs().getMotionEnabled() == true)
			{
				// Start motion mode
				if (MsState.getMotion() == false)
				{
					if (MsClassReference.getMotion().startMotion(this.mc.thePlayer) == true)
					{
						MsState.setMotion(true);
						// If the interval is not OFF, starting motion mode also starts the multishot mode
						if (MsClassReference.getMsConfigs().getInterval() > 0)
						{
							MsState.setRecording(true);
							MsRecordingHandler.startRecording();
						}
					}
				}
				// Stop motion mode
				else
				{
					MsState.setMotion(false);
					if (MsState.getRecording() == true)
					{
						MsState.setRecording(false);
						MsRecordingHandler.stopRecording();
					}
				}
			}
			// The Pause key doubles as the "set point" key for the motion modes, when used outside of recording mode
			else if (keyMultishotPause.isPressed() == true)
			{
				if (MsState.getRecording() == true)
				{
					MsState.togglePaused();
				}
				else
				{
					// DEL + HOME + P: Remove center point
					if (isDeleteKeyDown() == true && isHomeKeyDown() == true)
					{
						MsClassReference.getMotion().removeCenterPoint();
					}
					// DEL + END + P: Remove target point
					else if (isDeleteKeyDown() == true && isEndKeyDown() == true)
					{
						MsClassReference.getMotion().removeTargetPoint();
					}
					// DEL + CTRL + P: Remove all points
					else if (isDeleteKeyDown() == true && isCtrlKeyDown() == true)
					{
						MsClassReference.getMotion().removeAllPoints();
					}
					// HOME + P: Set center point
					else if (isHomeKeyDown() == true)
					{
						MsClassReference.getMotion().setCenterPointFromCurrentPos(this.mc.thePlayer);
					}
					// END + P: Set target point
					else if (isEndKeyDown() == true)
					{
						MsClassReference.getMotion().setTargetPointFromCurrentPos(this.mc.thePlayer);
					}
					// DEL + P: Remove nearest path point (path modes only)
					else if (isDeleteKeyDown() == true)
					{
						MsClassReference.getMotion().removeNearestPathPoint(this.mc.thePlayer);
					}
					// CTRL + P: Move/replace a previously "stored" path point with the current location
					else if (isCtrlKeyDown() == true)
					{
						MsClassReference.getMotion().replaceStoredPathPoint(this.mc.thePlayer);
					}
					// P: Add a path point (path mode) or ellipse longer semi-axis end point (ellipse mode)
					else
					{
						MsClassReference.getMotion().addPointFromCurrentPos(this.mc.thePlayer);
					}
				}
			}
			else if (keyMultishotHideGUI.isPressed() == true)
			{
				MsState.toggleHideGui();
				// Also update the configs to reflect the new state
				MsClassReference.getMsConfigs().changeValue(MsConstants.GUI_BUTTON_ID_HIDE_GUI, 0, 0);
			}
			else if (keyMultishotLock.isPressed() == true)
			{
				MsState.toggleControlsLocked();
				// Also update the configs to reflect the new state
				MsClassReference.getMsConfigs().changeValue(MsConstants.GUI_BUTTON_ID_LOCK_CONTROLS, 0, 0);
			}
			else
			{
				// Lock the keys when requested while recording, and also always in motion mode
				if ((MsState.getRecording() == true && MsState.getControlsLocked() == true) || MsState.getMotion() == true)
				{
					KeyBinding.unPressAllKeys();
				}
			}

			// Check if we need to unlock the controls, aka. return the focus to the game.
			// The locking is done in the PlayerTickHandler at every tick, when recording or motion is enabled.
			if ((MsState.getMotion() == false && MsState.getRecording() == false) ||
					MsState.getControlsLocked() == false)
			{
				this.mc.setIngameFocus();
			}
			// The gui screen needs to be opened after we possibly return the focus to the game (see above),
			// otherwise the currentScreen will get reset to null and the menu won't stay open
			if (keyMultishotMenu.isPressed() == true && MsState.getRecording() == false && MsState.getMotion() == false)
			{
				// CTRL + menu key: "cut" a path point (= store the index of the currently closest path point) for moving it
				if (isCtrlKeyDown() == true)
				{
					MsClassReference.getMotion().storeNearestPathPointIndex(this.mc.thePlayer);
				}
				else
				{
					this.mc.displayGuiScreen(this.multishotScreenConfigsGeneric);
				}
			}
		}
	}

	public static boolean isCtrlKeyDown()
	{
		boolean flag = Keyboard.isKeyDown(28) && Keyboard.getEventCharacter() == 0;
		return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) || Util.getOSType() == EnumOS.MACOS && (flag || Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220));
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
}
