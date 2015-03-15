package fi.dy.masa.minecraft.mods.multishot.worker;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class MsThread extends Thread
{
    private static MsThread instance = null;
    private SaveScreenshot saveScreenshot = null;
    private Thread t = null;
    private String threadName;
    private boolean stop;

    public MsThread(String path, int interval, int imgfmt)
    {
        this.threadName = "MultishotThread";
        this.t = new Thread(this, this.threadName);
        this.t.setDaemon(true);

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
        this.t.start();
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
        FMLLog.info(this.threadName + " exiting...");
    }
}
