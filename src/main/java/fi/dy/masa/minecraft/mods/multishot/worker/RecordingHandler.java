package fi.dy.masa.minecraft.mods.multishot.worker;

import net.minecraft.client.Minecraft;
import fi.dy.masa.minecraft.mods.multishot.config.Configs;
import fi.dy.masa.minecraft.mods.multishot.handlers.RenderEventHandler;
import fi.dy.masa.minecraft.mods.multishot.motion.Motion;
import fi.dy.masa.minecraft.mods.multishot.state.State;

public class RecordingHandler
{
    private Minecraft mc;
    private RenderEventHandler renderEventHandler;
    private long lastCheckTime = 0;
    private long shotTimer = 0;
    private MsThread multishotThread;
    private static RecordingHandler instance;

    public RecordingHandler(RenderEventHandler renderEventHandler)
    {
        this.mc = Minecraft.getMinecraft();
        this.renderEventHandler = renderEventHandler;
        instance = this;
    }

    public static RecordingHandler getInstance()
    {
        return instance;
    }

    public void resetScheduler()
    {
        this.lastCheckTime = System.currentTimeMillis();
    }

    public void multishotScheduler()
    {
        if (State.getRecording() && State.getPaused() == false && Configs.getConfig().getInterval() > 0)
        {
            // Do we have an active timer, and did we hit the number of shots set in the current timed configuration
            if (Configs.getConfig().getActiveTimer() != 0 && this.multishotThread.getCounter() >= Configs.getConfig().getActiveTimerNumShots())
            {
                this.stopRecording();
                State.setMotion(false);
                return;
            }

            long currentTime = System.currentTimeMillis();
            this.shotTimer += (currentTime - this.lastCheckTime);
            this.lastCheckTime = currentTime;

            if (this.shotTimer >= ((long) Configs.getConfig().getInterval() * 100L)) // 100ms = 0.1s
            {
                this.renderEventHandler.trigger(State.getShotCounter());
                State.incrementShotCounter();
                this.shotTimer = 0;
            }
        }
    }

    public void startRecording()
    {
        Configs mscfg = Configs.getConfig();

        State.storeFov(this.mc.gameSettings.fovSetting);

        if (mscfg.getZoom() != 0)
        {
            // -160..160 is somewhat "sane"
            this.mc.gameSettings.fovSetting = 70.0f - ((float)mscfg.getZoom() * 70.0f / 100.0f);
        }

        if (mscfg.getInterval() > 0)
        {
            State.resetShotCounter();
            this.lastCheckTime = System.currentTimeMillis();

            if (State.getMotion() == false)
            {
                Motion.setCameraEntityPositionFromPlayer(RenderEventHandler.instance().getCameraEntity(), this.mc.player);
            }

            this.multishotThread = new MsThread(mscfg.getSavePath(), mscfg.getInterval(), mscfg.getImgFormat());
            this.multishotThread.start();
        }

        this.resetScheduler();
        State.setRecording(true);

        // This can't be in the resetScheduler(), because that gets called when unpausing, and it would skew the timing
        this.shotTimer = 0;
    }

    public void stopRecording()
    {
        if (this.multishotThread != null)
        {
            this.multishotThread.setStop();
            this.multishotThread = null;
        }

        State.setRecording(false);
        this.resetScheduler();

        // Don't clear Paused state if the recording is stopped without stopping motion
        if (State.getMotion() == false)
        {
            State.setPaused(false);
        }

        // This can't be in the resetScheduler(), because that gets called when unpausing, and it would skew the timing
        this.shotTimer = 0;

        this.mc.setIngameFocus();
        this.mc.gameSettings.fovSetting = State.getFov();   // Restore the normal FoV value
    }

    public void toggleRecording()
    {
        if (State.getRecording())
        {
            this.stopRecording();
        }
        else
        {
            this.startRecording();
        }
    }

    public void trigger(int shotNum)
    {
        if (this.multishotThread != null)
        {
            this.multishotThread.trigger(shotNum);
        }
    }
}
