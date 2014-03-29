package fi.dy.masa.minecraft.mods.multishot.handlers;

import java.util.EnumSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.Configuration;
import org.lwjgl.input.Keyboard;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.config.MultishotConfigs;
import fi.dy.masa.minecraft.mods.multishot.gui.MultishotScreenConfigsGeneric;
import fi.dy.masa.minecraft.mods.multishot.libs.Constants;
import fi.dy.masa.minecraft.mods.multishot.output.MultishotThread;
import fi.dy.masa.minecraft.mods.multishot.state.MultishotState;

@SideOnly(Side.CLIENT)
public class MultishotKeys extends KeyHandler
{
	private Minecraft mc = null;
	private Configuration configuration = null;
	private MultishotScreenConfigsGeneric multishotScreenConfigsGeneric = null;
	private MultishotConfigs multishotConfigs = null;
	private MultishotThread multishotThread = null;
	protected static KeyBinding keyMultishotMenu	= new KeyBinding(Constants.BIND_MULTISHOT_MENU,		Keyboard.KEY_K);
	protected static KeyBinding keyMultishotStart	= new KeyBinding(Constants.BIND_MULTISHOT_STARTSTOP,Keyboard.KEY_M);
	protected static KeyBinding keyMultishotMotion	= new KeyBinding(Constants.BIND_MULTISHOT_MOTION,	Keyboard.KEY_N);
	protected static KeyBinding keyMultishotPause	= new KeyBinding(Constants.BIND_MULTISHOT_PAUSE,	Keyboard.KEY_P);
	protected static KeyBinding keyMultishotLock	= new KeyBinding(Constants.BIND_MULTISHOT_LOCK,		Keyboard.KEY_L);
	protected static KeyBinding keyMultishotHideGUI	= new KeyBinding(Constants.BIND_MULTISHOT_HIDEGUI,	Keyboard.KEY_H);
	
	public MultishotKeys(Minecraft par1mc, Configuration cfg, MultishotConfigs msCfg)
	{
		super(new KeyBinding[]{	keyMultishotMenu,
								keyMultishotStart,
								keyMultishotMotion,
								keyMultishotPause,
								keyMultishotLock,
								keyMultishotHideGUI}, new boolean[]{false, false, false, false, false, false});
		this.mc = par1mc;
		this.configuration = cfg;
		this.multishotConfigs = msCfg;
		this.multishotScreenConfigsGeneric = new MultishotScreenConfigsGeneric(this.configuration, this.multishotConfigs, this.mc.currentScreen);
	}

	@Override
	public String getLabel()
	{
		return "Multishot keybinds";
	}

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
				MultishotState.toggleRecording();
				if (MultishotState.getRecording() == true)
				{
					if (this.multishotConfigs.getInterval() > 0)
					{
						MultishotState.resetShotCounter();
						this.multishotThread = new MultishotThread(	this.multishotConfigs.getSavePath(),
																	this.multishotConfigs.getInterval(),
																	this.multishotConfigs.getImgFormat());
						this.multishotThread.start();
					}
				}
				else
				{
					if (this.multishotThread != null)
					{
						this.multishotThread.setStop();
						this.multishotThread = null;
					}
					// Disable the paused state when the recording ends
					if (MultishotState.getPaused() == true)
					{
						MultishotState.setPaused(false);
					}
				}
			}
			else if (kb.keyCode == keyMultishotMotion.keyCode && this.multishotConfigs.getMotionEnabled() == true)
			{
				MultishotState.toggleMotion();
			}
			else if (kb.keyCode == keyMultishotPause.keyCode && MultishotState.getRecording() == true)
			{
				MultishotState.togglePaused();
			}
			else if (kb.keyCode == keyMultishotHideGUI.keyCode)
			{
				MultishotState.toggleHideGui();
				// Also update the configs to reflect the new state
				this.multishotConfigs.changeValue(Constants.GUI_BUTTON_ID_HIDE_GUI, 0, 0);
			}
			else if (kb.keyCode == keyMultishotLock.keyCode)
			{
				MultishotState.toggleControlsLocked();
				// Also update the configs to reflect the new state
				this.multishotConfigs.changeValue(Constants.GUI_BUTTON_ID_LOCK_CONTROLS, 0, 0);
			}
			// Check if we need to unlock the controls, aka. return the focus to the game.
			// The locking is done in the PlayerTickHandler at every tick, when recording or motion is enabled.
			if ((MultishotState.getMotion() == false && MultishotState.getRecording() == false) ||
					MultishotState.getControlsLocked() == false)
			{
				this.mc.setIngameFocus();
			}
			// The gui screen needs to be opened after we possibly return the focus to the game (see above),
			// otherwise the currentScreen will get reset to null and the menu won't stay open
			if (kb.keyCode == keyMultishotMenu.keyCode)
			{
				this.mc.displayGuiScreen(this.multishotScreenConfigsGeneric);
			}
		}
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
