package fi.dy.masa.minecraft.mods.multishot.handlers;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.config.MsConfigs;
import fi.dy.masa.minecraft.mods.multishot.state.MsClassReference;
import fi.dy.masa.minecraft.mods.multishot.state.MsState;
import fi.dy.masa.minecraft.mods.multishot.worker.MsRecordingHandler;
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

    public void resetScheduler()
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

            // Move the player. Note: the pause key doesn't have an effect if not recording
            if (MsState.getMotion() == true && (MsState.getPaused() == false || MsState.getRecording() == false))
            {
                MsClassReference.getMotion().movePlayer(this.mc.thePlayer);
            }
        }
    }

    public void multishotScheduler()
    {
        MsConfigs mscfg = MsClassReference.getMsConfigs();
        MsSaveScreenshot mssave = MsSaveScreenshot.getInstance();

        if (MsState.getRecording() == true && MsState.getPaused() == false && mscfg.getInterval() > 0)
        {
            // Do we have an active timer, and did we hit the number of shots set in the current timed configuration
            if (mscfg.getActiveTimer() != 0 && mssave != null && mssave.getCounter() >= mscfg.getActiveTimerNumShots())
            {
                MsRecordingHandler.stopRecording();
                MsState.setMotion(false);
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

            if (this.shotTimer >= ((long)mscfg.getInterval() * 100000000L)) // 100M ns = 0.1s
            {
                mssave.trigger(MsState.getShotCounter());
                MsState.incrementShotCounter();
                this.shotTimer = 0;
            }
        }
    }
}
