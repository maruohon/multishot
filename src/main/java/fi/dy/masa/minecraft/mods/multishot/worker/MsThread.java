package fi.dy.masa.minecraft.mods.multishot.worker;

import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.Multishot;
import fi.dy.masa.minecraft.mods.multishot.gui.MsGui;


@SideOnly(Side.CLIENT)
public class MsThread extends Thread
{
    private ScreenshotSaver saveScreenshot = null;
    private Thread thread = null;
    private String threadName;
    private boolean stop;
    private boolean trigger;
    private int shotCounter;
    private static final List<String> GUI_MESSAGES = new ArrayList<String>();

    public MsThread(String path, int interval, int imgfmt)
    {
        this.threadName = "MultishotThread";
        this.thread = new Thread(this, this.threadName);
        this.thread.setDaemon(true);

        this.stop = false;
        this.trigger = false;
        this.shotCounter = 0;
        this.saveScreenshot = new ScreenshotSaver(path, interval, imgfmt);
    }

    @Override
    public void start()
    {
        this.thread.start();
    }

    public static void addGuiMessage(String str)
    {
        synchronized (GUI_MESSAGES)
        {
            GUI_MESSAGES.add(str);
        }
    }

    public static void printGuiMessages()
    {
        synchronized (GUI_MESSAGES)
        {
            int len = GUI_MESSAGES.size();

            for (int i = 0; i < len; ++i)
            {
                MsGui.getGui().addMessage(GUI_MESSAGES.get(i));
            }

            GUI_MESSAGES.clear();
        }
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
            this.saveScreenshot = null;
            this.notify();
        }
    }

    public void trigger(int shotNum)
    {
        this.saveScreenshot.trigger(shotNum);

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
            if (this.getTrigger())
            {
                int counter = this.saveScreenshot.saveToFile();

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
