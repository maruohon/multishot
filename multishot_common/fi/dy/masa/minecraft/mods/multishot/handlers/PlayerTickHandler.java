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

	private void stopRecordingAndMotion()
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
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		if (MultishotState.getRecording() == true || MultishotState.getMotion() == true)
		{
			// Lock the keys when requested, and also always in motion mode
			if (MultishotState.getControlsLocked() == true || MultishotState.getMotion() == true)
			{
				KeyBinding.unPressAllKeys();
				this.mc.setIngameNotInFocus();
			}
		}
		if (MultishotState.getMotion() == true)
		{
			this.multishotMotion.movePlayer(this.mc.thePlayer, this.multishotConfigs.getMotionMode());
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
		if (MultishotState.getRecording() == true && MultishotState.getPaused() == false && this.multishotConfigs.getInterval() > 0)
		{
			// Do we have an active timer, and did we hit the number of shots set in the current timed configuration
			if (this.multishotConfigs.getActiveTimer() != 0
					&& SaveScreenshot.getInstance() != null
					&& SaveScreenshot.getInstance().getCounter() >= this.multishotConfigs.getActiveTimerNumShots())
			{
				this.stopRecordingAndMotion();
				return;
			}

			long currentTime = System.currentTimeMillis();
			if (currentTime < this.lastCheckTime || (currentTime - this.lastCheckTime) < 50)
			{
				// Time ran backwards or less than 50ms has passed since the last time,
				// estimate that 50ms has passed since the last time, based on the tick system
				this.shotTimer += 50;
			}
			else if ((currentTime - this.lastCheckTime) >= 50)
			{
				this.shotTimer += currentTime - this.lastCheckTime;
			}
			this.lastCheckTime = currentTime;

			if (this.shotTimer >= ((long)this.multishotConfigs.getInterval() * 100))
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
