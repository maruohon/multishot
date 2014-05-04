package fi.dy.masa.minecraft.mods.multishot.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.gui.MsScreenBase;
import fi.dy.masa.minecraft.mods.multishot.state.MsClassReference;
import fi.dy.masa.minecraft.mods.multishot.state.MsState;
import fi.dy.masa.minecraft.mods.multishot.worker.MsSaveScreenshot;

@SideOnly(Side.CLIENT)
public class MsTickEvent
{
	private Minecraft mc = null;
	private long lastCheckTime = 0;
	private long shotTimer = 0;
	private boolean ready = false;

	public MsTickEvent()
	{
		this.mc = Minecraft.getMinecraft();
	}

	public void reset()
	{
		this.lastCheckTime = 0; // reset the latest time stamp
		this.ready = false;
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event)
	{
		if (event.phase == TickEvent.Phase.END && this.mc.isGamePaused() == false)
		{
			// Prevent mouse input while recording and controls locked, and always while moving
			if ((MsState.getRecording() == true && MsState.getControlsLocked() == true) || MsState.getMotion() == true)
			{
				this.mc.setIngameNotInFocus();
			}

			this.multishotScheduler();

			if (MsState.getMotion() == true)
			{
				MsClassReference.getMotion().movePlayer(this.mc.thePlayer);
			}
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

	public void multishotScheduler()
	{
		if (MsState.getRecording() == true && MsState.getPaused() == false && MsClassReference.getMsConfigs().getInterval() > 0)
		{
			// Do we have an active timer, and did we hit the number of shots set in the current timed configuration
			if (MsClassReference.getMsConfigs().getActiveTimer() != 0
					&& MsSaveScreenshot.getInstance() != null
					&& MsSaveScreenshot.getInstance().getCounter() >= MsClassReference.getMsConfigs().getActiveTimerNumShots())
			{
				this.stopRecordingAndMotion();
				this.reset();
				return;
			}

			long currentTime = System.nanoTime();

			// If we have just started the recording, set up the time stamp and wait for the next tick
			if (this.ready == false)
			{
				this.lastCheckTime = currentTime;
				this.ready = true;
				return;
			}

			this.shotTimer += (currentTime - this.lastCheckTime);
			this.lastCheckTime = currentTime;

			if (this.shotTimer >= ((long)MsClassReference.getMsConfigs().getInterval() * 100000000L)) // 100M ns = 0.1s
			{
				MsSaveScreenshot.getInstance().trigger(MsState.getShotCounter());
				MsState.incrementShotCounter();
				this.shotTimer = 0;
			}
		}
	}
}
