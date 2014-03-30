package fi.dy.masa.minecraft.mods.multishot.output;

import java.util.logging.Level;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.Multishot;


@SideOnly(Side.CLIENT)
public class MultishotThread extends Thread
{
	private static MultishotThread instance = null;
	private SaveScreenshot saveScreenshot = null;
	private Thread t = null;
	private String threadName;
	private boolean stop;

	public MultishotThread(String path, int interval, int imgfmt)
	{
		this.threadName = "MultishotThread";
		this.t = new Thread(this, this.threadName);
		this.t.setDaemon(true);

		this.stop = false;
		this.saveScreenshot = new SaveScreenshot(path, interval, imgfmt);
		instance = this;
	}

	public static MultishotThread getInstance()
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
		Multishot.logger.log(Level.INFO, this.threadName + " exiting...");
	}
}
