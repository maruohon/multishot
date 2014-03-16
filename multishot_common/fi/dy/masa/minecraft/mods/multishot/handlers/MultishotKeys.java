package fi.dy.masa.minecraft.mods.multishot.handlers;

import java.util.EnumSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MultishotKeys extends KeyHandler
{
	public Minecraft mc = Minecraft.getMinecraft();
	public static KeyBinding keyMultishotMenu		= new KeyBinding("key.multishot_menu", Keyboard.KEY_K);
	public static KeyBinding keyMultishotStart		= new KeyBinding("key.multishot_startstop", Keyboard.KEY_M);
	public static KeyBinding keyMultishotMotion		= new KeyBinding("key.multishot_motion", Keyboard.KEY_N);
	public static KeyBinding keyMultishotPause		= new KeyBinding("key.multishot_pause", Keyboard.KEY_P);
	public static KeyBinding keyMultishotLock		= new KeyBinding("key.multishot_lock", Keyboard.KEY_L);
	public static KeyBinding keyMultishotHideGUI	= new KeyBinding("key.multishot_hidegui", Keyboard.KEY_H);
	
	public MultishotKeys()
	{
		super(new KeyBinding[]{	keyMultishotMenu,
								keyMultishotStart,
								keyMultishotMotion,
								keyMultishotPause,
								keyMultishotLock,
								keyMultishotHideGUI},
					new boolean[]{false, false, false, false, false, false});
	}

	@Override
	public String getLabel()
	{
		return "Key bindings";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat)
	{
		// Note: isRepeat seems to be reversed (see KeyBindingRegistry.java).
		// So we flip it here to avoid further confusion.
		isRepeat = ! isRepeat;

		if (tickEnd && mc.currentScreen == null && ! isRepeat)
		{
			if (kb.keyCode == keyMultishotMenu.keyCode)
			{
				System.out.println("Multishot menu key pressed, value: " + kb.keyCode);
				//Minecraft.getMinecraft().displayGuiScreen(Multishot.guiSettings);
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd)
	{
	}
}
