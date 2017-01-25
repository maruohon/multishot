package fi.dy.masa.minecraft.mods.multishot.worker;

import net.minecraft.client.Minecraft;
import fi.dy.masa.minecraft.mods.multishot.Multishot;
import fi.dy.masa.minecraft.mods.multishot.config.Configs;


public class MsThread extends Thread
{
    private ScreenshotSaver screenshotSaver = null;
    private Thread thread = null;
    private String threadName;
    private boolean stop;
    private boolean trigger;
    private int shotCounter;

    public MsThread(String path, int interval, int imgfmt)
    {
        this.threadName = "MultishotThread";
        this.thread = new Thread(this, this.threadName);
        this.thread.setDaemon(true);

        this.stop = false;
        this.trigger = false;
        this.shotCounter = 0;

        if (Configs.getConfig().getUseFreeCamera())
        {
            int width = Configs.getConfig().getFreeCameraWidth();
            int height = Configs.getConfig().getFreeCameraHeight();
            this.screenshotSaver = new ScreenshotSaver(path, interval, imgfmt, width, height, true);
        }
        else
        {
            Minecraft mc = Minecraft.getMinecraft();
            this.screenshotSaver = new ScreenshotSaver(path, interval, imgfmt, mc.displayWidth, mc.displayHeight, false);
        }
    }

    @Override
    public void start()
    {
        this.thread.start();
    }

    public ScreenshotSaver getScreenshotSaver()
    {
        return this.screenshotSaver;
    }

    public int getCounter()
    {
        synchronized (this)
        {
            return this.shotCounter;
        }
    }

    private boolean shouldRun()
    {
        synchronized (this)
        {
            return this.stop == false;
        }
    }

    public void setStop()
    {
        synchronized (this)
        {
            this.stop = true;
            this.screenshotSaver.deleteFrameBuffer();
            this.notify();
        }
    }

    public void trigger(int shotNum)
    {
        this.screenshotSaver.trigger(shotNum);

        synchronized (this)
        {
            this.trigger = true;
            this.notify();
        }
    }

    private boolean getTrigger()
    {
        synchronized (this)
        {
            return this.trigger;
        }
    }

    @Override
    public void run()
    {
        while (this.shouldRun())
        {
            if (this.getTrigger() && this.screenshotSaver != null)
            {
                int counter = this.screenshotSaver.saveToFile();

                synchronized (this)
                {
                    this.shotCounter = counter;
                    this.trigger = false;
                }
            }
            else
            {
                synchronized (this)
                {
                    try
                    {
                        this.wait();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        Multishot.logger.info(this.threadName + " exiting...");
    }
}
