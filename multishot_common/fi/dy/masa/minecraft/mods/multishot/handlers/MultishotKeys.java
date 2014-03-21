package fi.dy.masa.minecraft.mods.multishot.handlers;

import java.util.EnumSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.config.MultishotConfigs;
import fi.dy.masa.minecraft.mods.multishot.gui.MultishotScreenConfigsGeneric;
import fi.dy.masa.minecraft.mods.multishot.libs.Constants;

@SideOnly(Side.CLIENT)
public class MultishotKeys extends KeyHandler
{
	private Minecraft mc = null;
	private MultishotScreenConfigsGeneric multishotScreenConfigsGeneric = null;
	private MultishotConfigs multishotConfigs = null;
	protected static KeyBinding keyMultishotMenu	= new KeyBinding(Constants.BIND_MULTISHOT_MENU,		Keyboard.KEY_K);
	protected static KeyBinding keyMultishotStart	= new KeyBinding(Constants.BIND_MULTISHOT_STARTSTOP,Keyboard.KEY_M);
	protected static KeyBinding keyMultishotMotion	= new KeyBinding(Constants.BIND_MULTISHOT_MOTION,	Keyboard.KEY_N);
	protected static KeyBinding keyMultishotPause	= new KeyBinding(Constants.BIND_MULTISHOT_PAUSE,	Keyboard.KEY_P);
	protected static KeyBinding keyMultishotLock	= new KeyBinding(Constants.BIND_MULTISHOT_LOCK,		Keyboard.KEY_L);
	protected static KeyBinding keyMultishotHideGUI	= new KeyBinding(Constants.BIND_MULTISHOT_HIDEGUI,	Keyboard.KEY_H);
	
	public MultishotKeys()
	{
		super(new KeyBinding[]{	keyMultishotMenu,
								keyMultishotStart,
								keyMultishotMotion,
								keyMultishotPause,
								keyMultishotLock,
								keyMultishotHideGUI}, new boolean[]{false, false, false, false, false, false});
		this.mc = Minecraft.getMinecraft();
		this.multishotConfigs = new MultishotConfigs();
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

		if (this.mc.currentScreen == null)
		{
			if (kb.keyCode == keyMultishotMenu.keyCode)
			{
				System.out.println("Multishot menu key pressed, value: " + kb.keyCode);	// FIXME debug
				if (this.multishotScreenConfigsGeneric == null)
				{
					this.multishotScreenConfigsGeneric = new MultishotScreenConfigsGeneric(this.multishotConfigs, this.mc.currentScreen);
				}
				this.mc.displayGuiScreen(this.multishotScreenConfigsGeneric);
			}
		}
		// FIXME TESTING/DEBUG:
		else
		{
			if (kb.keyCode == keyMultishotMenu.keyCode)
			{
				this.mc.displayGuiScreen((GuiScreen)null);
				this.mc.setIngameFocus();
			}
			else if (kb.keyCode == keyMultishotPause.keyCode)
			{
				//this.mc.thePlayer.sendChatMessage("pause");
				this.mc.ingameGUI.getChatGUI().printChatMessage("derp");
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
