package fi.dy.masa.minecraft.mods.multishot.handlers;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.config.MsConfigs;
import fi.dy.masa.minecraft.mods.multishot.motion.MsMotion;
import fi.dy.masa.minecraft.mods.multishot.state.MsState;
import fi.dy.masa.minecraft.mods.multishot.worker.MsSaveScreenshot;

@SideOnly(Side.CLIENT)
public class MsPlayerTickHandler implements ITickHandler
{
	private MsConfigs multishotConfigs = null;
	private MsMotion multishotMotion = null;
	private Minecraft mc = null;
	private long lastCheckTime = 0;
	private long shotTimer = 0;

	public MsPlayerTickHandler(MsConfigs msCfg, MsMotion msMotion)
	{
		super();
		this.multishotConfigs = msCfg;
		this.multishotMotion = msMotion;
		this.mc = Minecraft.getMinecraft();
	}

	private void stopRecordingAndMotion()
	{
		MsState.setRecording(false);
		MsState.setMotion(false);
		if (MsState.getMultishotThread() != null)
		{
			MsState.getMultishotThread().setStop();
			MsSaveScreenshot.clearInstance();
		}
		this.mc.setIngameFocus();
		this.mc.gameSettings.fovSetting = MsState.getFov(); // Restore the normal FoV value
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		if (MsState.getRecording() == true || MsState.getMotion() == true)
		{
			// Lock the keys when requested, and also always in motion mode
			if (MsState.getControlsLocked() == true || MsState.getMotion() == true)
			{
				KeyBinding.unPressAllKeys();
				this.mc.setIngameNotInFocus();
			}
		}
		if (MsState.getMotion() == true)
		{
			this.multishotMotion.movePlayer(this.mc.thePlayer);
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
		if (MsState.getRecording() == true && MsState.getPaused() == false && this.multishotConfigs.getInterval() > 0)
		{
			// Do we have an active timer, and did we hit the number of shots set in the current timed configuration
			if (this.multishotConfigs.getActiveTimer() != 0
					&& MsSaveScreenshot.getInstance() != null
					&& MsSaveScreenshot.getInstance().getCounter() >= this.multishotConfigs.getActiveTimerNumShots())
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
				MsSaveScreenshot.getInstance().trigger(MsState.getShotCounter());
				MsState.incrementShotCounter();
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
