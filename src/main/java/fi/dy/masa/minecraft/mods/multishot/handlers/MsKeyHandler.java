package fi.dy.masa.minecraft.mods.multishot.handlers;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.Util;
import net.minecraft.util.Util.EnumOS;
import net.minecraftforge.common.config.Configuration;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.config.MsConfigs;
import fi.dy.masa.minecraft.mods.multishot.gui.MsScreenGeneric;
import fi.dy.masa.minecraft.mods.multishot.motion.MsMotion;
import fi.dy.masa.minecraft.mods.multishot.reference.MsConstants;
import fi.dy.masa.minecraft.mods.multishot.state.MsState;
import fi.dy.masa.minecraft.mods.multishot.worker.MsThread;

@SideOnly(Side.CLIENT)
public class MsKeyHandler
{
	private Minecraft mc = null;
	private Configuration configuration = null;
	private MsScreenGeneric multishotScreenConfigsGeneric = null;
	private MsConfigs multishotConfigs = null;
	private MsMotion multishotMotion = null;
	private static KeyBinding keyMultishotMenu = null;
	private static KeyBinding keyMultishotStart = null;
	private static KeyBinding keyMultishotMotion = null;
	private static KeyBinding keyMultishotPause = null;
	private static KeyBinding keyMultishotLock = null;
	private static KeyBinding keyMultishotHideGUI = null;
	
	public MsKeyHandler(Minecraft par1mc, Configuration cfg, MsConfigs msCfg, MsMotion msMotion)
	{
		this.mc = par1mc;
		this.configuration = cfg;
		this.multishotConfigs = msCfg;
		this.multishotMotion = msMotion;
		this.multishotScreenConfigsGeneric = new MsScreenGeneric(this.configuration, this.multishotConfigs, this.mc.currentScreen);

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
/*
	@Override
	public String getLabel()
	{
		return "Multishot keybinds";
	}
*/

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat)
	{
		if (! tickEnd)
		{
			return;
		}

		// Note: isRepeat seems to be reversed (see KeyBindingRegistry.java).
		// So we flip it here to avoid further confusion.
		isRepeat = ! isRepeat;

		// In-game (no GUI open)
		if (this.mc.currentScreen == null)
		{
			if (kb.keyCode == keyMultishotStart.keyCode && this.multishotConfigs.getMultishotEnabled() == true)
			{
				this.toggleRecording();
			}
			else if (kb.keyCode == keyMultishotMotion.keyCode && this.multishotConfigs.getMotionEnabled() == true)
			{
				// Start motion mode
				if (MsState.getMotion() == false)
				{
					if (this.multishotMotion.startMotion(this.mc.thePlayer) == true)
					{
						MsState.setMotion(true);
						// If the interval is not OFF, starting motion mode also starts the multishot mode
						if (this.multishotConfigs.getInterval() > 0)
						{
							MsState.setRecording(true);
							this.startRecording();
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
						this.stopRecording();
					}
				}
			}
			// The Pause key doubles as the "set point" key for the motion modes, when used outside of recording mode
			else if (kb.keyCode == keyMultishotPause.keyCode)
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
						this.multishotMotion.removeCenterPoint();
					}
					// DEL + END + P: Remove target point
					else if (isDeleteKeyDown() == true && isEndKeyDown() == true)
					{
						this.multishotMotion.removeTargetPoint();
					}
					// DEL + CTRL + P: Remove all points
					else if (isDeleteKeyDown() == true && isCtrlKeyDown() == true)
					{
						this.multishotMotion.removeAllPoints();
					}
					// HOME + P: Set center point
					else if (isHomeKeyDown() == true)
					{
						this.multishotMotion.setCenterPointFromCurrentPos(this.mc.thePlayer);
					}
					// END + P: Set target point
					else if (isEndKeyDown() == true)
					{
						this.multishotMotion.setTargetPointFromCurrentPos(this.mc.thePlayer);
					}
					// DEL + P: Remove nearest path point (path modes only)
					else if (isDeleteKeyDown() == true)
					{
						this.multishotMotion.removeNearestPathPoint(this.mc.thePlayer);
					}
					// CTRL + P: Move/replace a previously "stored" path point with the current location
					else if (isCtrlKeyDown() == true)
					{
						this.multishotMotion.replaceStoredPathPoint(this.mc.thePlayer);
					}
					// P: Add a path point (path mode) or ellipse longer semi-axis end point (ellipse mode)
					else
					{
						this.multishotMotion.addPointFromCurrentPos(this.mc.thePlayer);
					}
				}
			}
			else if (kb.keyCode == keyMultishotHideGUI.keyCode)
			{
				MsState.toggleHideGui();
				// Also update the configs to reflect the new state
				this.multishotConfigs.changeValue(MsConstants.GUI_BUTTON_ID_HIDE_GUI, 0, 0);
			}
			else if (kb.keyCode == keyMultishotLock.keyCode)
			{
				MsState.toggleControlsLocked();
				// Also update the configs to reflect the new state
				this.multishotConfigs.changeValue(MsConstants.GUI_BUTTON_ID_LOCK_CONTROLS, 0, 0);
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
			if (kb.keyCode == keyMultishotMenu.keyCode && MsState.getRecording() == false && MsState.getMotion() == false)
			{
				// CTRL + menu key: "cut" a path point (= store the index of the currently closest path point) for moving it
				if (isCtrlKeyDown() == true)
				{
					this.multishotMotion.storeNearestPathPointIndex(this.mc.thePlayer);
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

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd)
	{
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.CLIENT);
	}
}
