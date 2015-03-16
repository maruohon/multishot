package fi.dy.masa.minecraft.mods.multishot.worker;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.Multishot;


@SideOnly(Side.CLIENT)
public class MsThread extends Thread
{
    private static MsThread instance = null;
    private SaveScreenshot saveScreenshot = null;
    private Thread thread = null;
    private String threadName;
    private boolean stop;

    public MsThread(String path, int interval, int imgfmt)
    {
        this.threadName = "MultishotThread";
        this.thread = new Thread(this, this.threadName);
        this.thread.setDaemon(true);

        this.stop = false;
        this.saveScreenshot = new SaveScreenshot(path, interval, imgfmt);
        instance = this;
    }

    public static MsThread getInstance()
    {
        return instance;
    }

    synchronized private boolean getStop()
    {
        return this.stop;
    }

    synchronized public void setStop()
    {
        this.stop = true;
        instance = null;
    }

    public void start()
    {
        this.thread.start();
    }

    @Override
    public void run()
    {
        while(this.getStop() == false)
        {
            if (this.saveScreenshot.triggerActivated() == true)
            {
                this.saveScreenshot.saveToFile();
            }
        }

        Multishot.logger.info(this.threadName + " exiting...");
    }
}
