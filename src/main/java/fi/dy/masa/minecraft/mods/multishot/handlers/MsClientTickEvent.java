package fi.dy.masa.minecraft.mods.multishot.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.state.MsClassReference;
import fi.dy.masa.minecraft.mods.multishot.state.MsState;
import fi.dy.masa.minecraft.mods.multishot.worker.MsSaveScreenshot;

@SideOnly(Side.CLIENT)
public class MsClientTickEvent
{
	private Minecraft mc = null;
	private long lastCheckTime = 0;
	private long shotTimer = 0;

	public MsClientTickEvent()
	{
		this.mc = Minecraft.getMinecraft();
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event)
	{
		if (event.phase == TickEvent.Phase.START)
		{
			this.onTickStart();
			return;
		}

		if (event.phase == TickEvent.Phase.END)
		{
			this.onTickEnd();
			return;
		}
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

	public void onTickStart()
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
			MsClassReference.getMotion().movePlayer(this.mc.thePlayer);
		}
	}

	public void onTickEnd()
	{
		if (MsState.getRecording() == true && MsState.getPaused() == false && MsClassReference.getMsConfigs().getInterval() > 0)
		{
			// Do we have an active timer, and did we hit the number of shots set in the current timed configuration
			if (MsClassReference.getMsConfigs().getActiveTimer() != 0
					&& MsSaveScreenshot.getInstance() != null
					&& MsSaveScreenshot.getInstance().getCounter() >= MsClassReference.getMsConfigs().getActiveTimerNumShots())
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

			if (this.shotTimer >= ((long)MsClassReference.getMsConfigs().getInterval() * 100))
			{
				MsSaveScreenshot.getInstance().trigger(MsState.getShotCounter());
				MsState.incrementShotCounter();
				this.shotTimer = 0;
			}
		}
	}
}
