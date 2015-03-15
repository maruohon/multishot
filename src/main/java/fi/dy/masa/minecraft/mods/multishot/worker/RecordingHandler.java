package fi.dy.masa.minecraft.mods.multishot.worker;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.config.Configs;
import fi.dy.masa.minecraft.mods.multishot.state.ClassReference;
import fi.dy.masa.minecraft.mods.multishot.state.State;

@SideOnly(Side.CLIENT)
public class RecordingHandler
{
    private long lastCheckTime = 0;
    private long shotTimer = 0;
    private boolean ready = false;
    private static RecordingHandler instance;

    public RecordingHandler()
    {
        instance = this;
    }

    public static RecordingHandler getInstance()
    {
        return instance;
    }

    public void resetScheduler()
    {
        this.lastCheckTime = 0; // reset the latest time stamp
        this.ready = false;
    }

    public void multishotScheduler()
    {
        SaveScreenshot mssave = SaveScreenshot.getInstance();

        if (State.getRecording() == true && State.getPaused() == false && Configs.getConfig().getInterval() > 0)
        {
            // Do we have an active timer, and did we hit the number of shots set in the current timed configuration
            if (Configs.getConfig().getActiveTimer() != 0 && mssave != null && mssave.getCounter() >= Configs.getConfig().getActiveTimerNumShots())
            {
                this.stopRecording();
                State.setMotion(false);
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

            if (this.shotTimer >= ((long)Configs.getConfig().getInterval() * 100000000L)) // 100M ns = 0.1s
            {
                mssave.trigger(State.getShotCounter());
                State.incrementShotCounter();
                this.shotTimer = 0;
            }
        }
    }

    public void startRecording()
    {
        Configs mscfg = Configs.getConfig();
        Minecraft mc = Minecraft.getMinecraft();

        State.storeFov(mc.gameSettings.fovSetting);

        if (mscfg.getZoom() != 0)
        {
            // -160 - 160 is somewhat "sane"
            mc.gameSettings.fovSetting = 70.0f - (float)mscfg.getZoom() / 100.0f * 70.0f;
        }

        if (mscfg.getInterval() > 0)
        {
            MsThread t;
            State.resetShotCounter();
            t = new MsThread(mscfg.getSavePath(), mscfg.getInterval(), mscfg.getImgFormat());
            State.setMultishotThread(t); // FIXME remove
            ClassReference.setThread(t);
            t.start();
        }
        State.setRecording(true);
    }

    public void stopRecording()
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (ClassReference.getThread() != null)
        {
            ClassReference.getThread().setStop();
            SaveScreenshot.clearInstance();
        }

        State.setRecording(false);
        State.setPaused(false);
        this.resetScheduler();
        mc.setIngameFocus();
        // Restore the normal FoV value
        mc.gameSettings.fovSetting = State.getFov();
    }

    public void toggleRecording()
    {
        if (State.getRecording() == true)
        {
            stopRecording();
        }
        else
        {
            startRecording();
        }
    }
}
