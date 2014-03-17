package fi.dy.masa.minecraft.mods.multishot.handlers;

import java.util.EnumSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.gui.MultishotScreenConfigsGeneral;
import fi.dy.masa.minecraft.mods.multishot.libs.Constants;

@SideOnly(Side.CLIENT)
public class MultishotKeys extends KeyHandler
{
	Minecraft mc = Minecraft.getMinecraft();
	public static KeyBinding keyMultishotMenu		= new KeyBinding(Constants.BIND_MULTISHOT_MENU,		Keyboard.KEY_K);
	public static KeyBinding keyMultishotStart		= new KeyBinding(Constants.BIND_MULTISHOT_STARTSTOP,Keyboard.KEY_M);
	public static KeyBinding keyMultishotMotion		= new KeyBinding(Constants.BIND_MULTISHOT_MOTION,	Keyboard.KEY_N);
	public static KeyBinding keyMultishotPause		= new KeyBinding(Constants.BIND_MULTISHOT_PAUSE,	Keyboard.KEY_P);
	public static KeyBinding keyMultishotLock		= new KeyBinding(Constants.BIND_MULTISHOT_LOCK,		Keyboard.KEY_L);
	public static KeyBinding keyMultishotHideGUI	= new KeyBinding(Constants.BIND_MULTISHOT_HIDEGUI,	Keyboard.KEY_H);
	
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
		return "Multishot config screen keybind";
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
				System.out.println("Multishot menu key pressed, value: " + kb.keyCode);
				this.mc.displayGuiScreen(new MultishotScreenConfigsGeneral(this.mc.currentScreen));
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
