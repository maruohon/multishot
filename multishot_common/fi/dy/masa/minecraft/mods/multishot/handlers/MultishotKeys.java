package fi.dy.masa.minecraft.mods.multishot.handlers;

import java.util.EnumSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.Configuration;
import org.lwjgl.input.Keyboard;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.config.MultishotConfigs;
import fi.dy.masa.minecraft.mods.multishot.gui.MultishotScreenConfigsGeneric;
import fi.dy.masa.minecraft.mods.multishot.gui.MultishotScreenEmpty;
import fi.dy.masa.minecraft.mods.multishot.libs.Constants;
import fi.dy.masa.minecraft.mods.multishot.state.MultishotState;

@SideOnly(Side.CLIENT)
public class MultishotKeys extends KeyHandler
{
	private Minecraft mc = null;
	private Configuration configuration = null;
	private MultishotScreenConfigsGeneric multishotScreenConfigsGeneric = null;
	private MultishotScreenEmpty multishotScreenEmpty = null;
	private MultishotConfigs multishotConfigs = null;
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
		this.multishotScreenEmpty = new MultishotScreenEmpty();
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

		// In-game (no GUI open) or we have the dummy empty GUI screen open for locking the controls
		if (this.mc.currentScreen == null || this.mc.currentScreen == this.multishotScreenEmpty)
		{
			if (kb.keyCode == keyMultishotMenu.keyCode && this.mc.currentScreen == null)
			{
				this.mc.displayGuiScreen(this.multishotScreenConfigsGeneric);
			}
			else if (kb.keyCode == keyMultishotStart.keyCode && this.multishotConfigs.getMultishotEnabled() == true)
			{
				MultishotState.toggleRecording();
			}
			else if (kb.keyCode == keyMultishotMotion.keyCode && this.multishotConfigs.getMotionEnabled() == true)
			{
				MultishotState.toggleMotion();
			}
			else if (kb.keyCode == keyMultishotPause.keyCode)
			{
				MultishotState.togglePaused();
			}
			else if (kb.keyCode == keyMultishotHideGUI.keyCode)
			{
				MultishotState.toggleHideGui();
			}
			else if (kb.keyCode == keyMultishotLock.keyCode)
			{
				MultishotState.toggleControlsLocked();
			}
			// Check if we want to lock or unlock the screen by displaying an empty GUI screen.
			if ((MultishotState.getMotion() == true || MultishotState.getRecording() == true) &&
					MultishotState.getControlsLocked() == true)
			{
				// Motion or recording enabled and controls should be locked, check that we are displaying the dummy empty GUI screen
				if (this.mc.currentScreen == null)
				{
					this.multishotScreenEmpty = new MultishotScreenEmpty();
					this.mc.displayGuiScreen(this.multishotScreenEmpty);
				}
			}
			else
			{
				// Not in motion or recording mode, or controls should not be locked in those anyway, make sure that they aren't
				if (this.mc.currentScreen == this.multishotScreenEmpty)
				{
					this.mc.displayGuiScreen((GuiScreen)null);
				}
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
