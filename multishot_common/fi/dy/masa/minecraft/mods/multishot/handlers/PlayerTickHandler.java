package fi.dy.masa.minecraft.mods.multishot.handlers;

import java.util.EnumSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.config.MultishotConfigs;
import fi.dy.masa.minecraft.mods.multishot.motion.MultishotMotion;
import fi.dy.masa.minecraft.mods.multishot.output.SaveScreenshot;
import fi.dy.masa.minecraft.mods.multishot.state.MultishotState;

@SideOnly(Side.CLIENT)
public class PlayerTickHandler implements ITickHandler
{
	private MultishotConfigs multishotConfigs = null;
	private MultishotMotion multishotMotion = null;
	private Minecraft mc = null;
	private long lastCheckTime = 0;
	private long shotTimer = 0;

	public PlayerTickHandler(MultishotConfigs msCfg, MultishotMotion msMotion)
	{
		super();
		this.multishotConfigs = msCfg;
		this.multishotMotion = msMotion;
		this.mc = Minecraft.getMinecraft();
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		if (MultishotState.getRecording() == true || MultishotState.getMotion() == true)
		{
			if (MultishotState.getControlsLocked() == true)
			{
				KeyBinding.unPressAllKeys();
				this.mc.setIngameNotInFocus();
			}
		}
		if (MultishotState.getMotion() == true)
		{
			this.multishotMotion.movePlayer(this.mc.thePlayer);
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
		if (MultishotState.getRecording() == true && MultishotState.getPaused() == false && this.multishotConfigs.getInterval() > 0)
		{
			if (this.multishotConfigs.getActiveTimer() != 0)
			{
				// We hit the number of shots set in the current timed configuration
				if (SaveScreenshot.getInstance() != null && SaveScreenshot.getInstance().getCounter() >= this.multishotConfigs.getActiveTimerNumShots())
				{
					MultishotState.setRecording(false);
					MultishotState.setMotion(false);
					if (MultishotState.getMultishotThread() != null)
					{
						MultishotState.getMultishotThread().setStop();
						SaveScreenshot.clearInstance();
					}
					this.mc.setIngameFocus();
					this.mc.gameSettings.fovSetting = MultishotState.getFov(); // Restore the normal FoV value
					return;
				}
			}
			long currentTime = System.currentTimeMillis();
			if (currentTime < this.lastCheckTime)
			{
				// Time ran backwards, estimate 50ms has passed since the last time based on the tick system
				this.lastCheckTime = currentTime;
				this.shotTimer += 50;
			}
			else if ((currentTime - this.lastCheckTime) >= 50)
			{
				this.shotTimer += currentTime - this.lastCheckTime;
			}
			else
			{
				// Less than 50ms from the last check: Assume the tick system is more accurate than our measurement
				this.shotTimer += 50;
			}
			this.lastCheckTime = currentTime;
			long interval = (long)this.multishotConfigs.getInterval() * 100;
			if (this.shotTimer >= interval)
			{
				SaveScreenshot.getInstance().trigger(MultishotState.getShotCounter());
				MultishotState.incrementShotCounter();
				this.shotTimer = 0;
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public String getLabel()
	{
		return "Multishot: Player Tick";
	}
}
