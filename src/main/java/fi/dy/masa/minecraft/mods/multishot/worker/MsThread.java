package fi.dy.masa.minecraft.mods.multishot.worker;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.Multishot;
import fi.dy.masa.minecraft.mods.multishot.gui.MsGui;


@SideOnly(Side.CLIENT)
public class MsThread extends Thread
{
    private SaveScreenshot saveScreenshot = null;
    private Thread thread = null;
    private String threadName;
    private boolean stop;
    private boolean trigger;
    private int shotCounter;
    private static List<String> guiMessages;

    static
    {
        guiMessages = new ArrayList<String>();
    }

    public MsThread(String path, int interval, int imgfmt)
    {
        this.threadName = "MultishotThread";
        this.thread = new Thread(this, this.threadName);
        this.thread.setDaemon(true);

        this.stop = false;
        this.trigger = false;
        this.shotCounter = 0;
        this.saveScreenshot = new SaveScreenshot(path, interval, imgfmt);
        this.start();
    }

    public void start()
    {
        this.thread.start();
    }

    synchronized public static void addGuiMessage(String str)
    {
        guiMessages.add(str);
    }

    synchronized public static void printGuiMessages()
    {
        int len = guiMessages.size();
        for (int i = 0; i < len; ++i)
        {
            MsGui.getGui().addMessage(guiMessages.get(i));
        }

        guiMessages.clear();
    }

    synchronized public int getCounter()
    {
        return this.shotCounter;
    }

    synchronized private void setCounter(int val)
    {
        this.shotCounter = val;
    }

    synchronized private boolean getStop()
    {
        return this.stop;
    }

    synchronized public void setStop()
    {
        this.stop = true;
        this.saveScreenshot = null;
        this.notify();
    }

    synchronized public void trigger(int shotNum)
    {
        this.saveScreenshot.trigger(shotNum);
        this.trigger = true;
        this.notify();
    }

    synchronized private void setTrigger(boolean val)
    {
        this.trigger = val;
    }

    synchronized private boolean getTrigger()
    {
        if (this.trigger == false)
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

        return this.trigger;
    }

    @Override
    public void run()
    {
        while (this.getStop() == false)
        {
            if (this.getTrigger() == true)
            {
                int counter = this.saveScreenshot.saveToFile();
                this.setCounter(counter);
                this.setTrigger(false);
            }
        }

        Multishot.logger.info(this.threadName + " exiting...");
    }
}
