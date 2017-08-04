package fi.dy.masa.minecraft.mods.multishot.worker;

import net.minecraft.client.Minecraft;
import fi.dy.masa.minecraft.mods.multishot.config.Configs;
import fi.dy.masa.minecraft.mods.multishot.handlers.RenderEventHandler;
import fi.dy.masa.minecraft.mods.multishot.motion.Motion;
import fi.dy.masa.minecraft.mods.multishot.state.State;

public class RecordingHandler
{
    private Minecraft mc;
    private long lastCheckTime = 0;
    private long shotTimer = 0;
    private MsThread multishotThread;
    private static RecordingHandler instance;

    public RecordingHandler()
    {
        this.mc = Minecraft.getMinecraft();
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
        if (State.getRecording() && State.getPaused() == false && Configs.getInterval() > 0)
        {
            // Do we have an active timer, and did we hit the number of shots set in the current timed configuration
            if (Configs.getActiveTimer() != 0 && this.multishotThread.getCounter() >= Configs.getActiveTimerNumShots())
            {
                this.stopRecording();
                State.setMotion(false);
                return;
            }

            long currentTime = System.currentTimeMillis();
            this.shotTimer += (currentTime - this.lastCheckTime);
            this.lastCheckTime = currentTime;

            if (this.shotTimer >= ((long) Configs.getInterval() * 100L)) // 100ms = 0.1s
            {
                RenderEventHandler.instance().trigger(State.getShotCounter());
                State.incrementShotCounter();
                this.shotTimer = 0;
            }
        }
    }

    public void startRecording()
    {
        if (Configs.getInterval() > 0)
        {
            State.resetShotCounter();
            this.lastCheckTime = System.currentTimeMillis();

            if (Configs.getUseFreeCamera() && State.getMotion() == false)
            {
                Motion.setCameraEntityPositionFromPlayer(RenderEventHandler.instance().getCameraEntity(), this.mc.player);
            }

            this.multishotThread = new MsThread(Configs.getSavePath(), Configs.getInterval(), Configs.getImgFormat());
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
