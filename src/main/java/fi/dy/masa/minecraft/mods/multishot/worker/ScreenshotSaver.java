package fi.dy.masa.minecraft.mods.multishot.worker;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ScreenShotHelper;
import fi.dy.masa.minecraft.mods.multishot.Multishot;
import fi.dy.masa.minecraft.mods.multishot.gui.MsGui;

public class ScreenshotSaver
{
    private Minecraft mc;
    private Framebuffer fb;
    private boolean hasData;
    private long shotInterval; // Screenshot interval, in 0.1 seconds, used in checking if we manage to save the screenshots in time to not lag behind
    private int shotCounter; // The actual shot counter, increments linearly
    private int requestedShot; // The shot number requested by the main thread
    private int imgFormat;
    private File savePath;
    private String dateString;
    private String filenameExtension;
    private BufferedImage bufferedImage;

    public ScreenshotSaver(String basePath, int interval, int imgfmt)
    {
        this.mc = Minecraft.getMinecraft();
        this.fb = this.mc.getFramebuffer();
        this.shotInterval = interval;
        this.imgFormat = imgfmt;
        this.dateString = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date(System.currentTimeMillis()));

        if (this.imgFormat == 0)
        {
            this.filenameExtension = "png";
        }
        else
        {
            this.filenameExtension = "jpg";
        }

        // We save the screenshots in a separate sub directory each time, named after the start timestamp
        this.savePath = new File(basePath, this.dateString);

        if (this.savePath.isDirectory() == false)
        {
            if (this.savePath.mkdir() == false)
            {
                Multishot.logger.fatal("Error: Could not create directory '{}'", this.savePath.getPath());
            }
        }
    }

    private void takeScreenshot()
    {
        int width;
        int height;

        if (OpenGlHelper.isFramebufferEnabled())
        {
            width = this.fb.framebufferTextureWidth;
            height = this.fb.framebufferTextureHeight;
        }
        else
        {
            width = this.mc.displayWidth;
            height = this.mc.displayHeight;
        }

        this.bufferedImage = ScreenShotHelper.createScreenshot(width, height, this.fb);
        this.hasData = true;
    }

    public int saveToFile()
    {
        synchronized(this)
        {
            return this.saveToFileImpl();
        }
    }

    private int saveToFileImpl()
    {
        if (this.hasData == false)
        {
            return -1;
        }

        long timeStart = System.currentTimeMillis();

        if (this.requestedShot != (this.shotCounter + 1))
        {
            Multishot.logger.warn(String.format("saveScreenshot(): shotCounter mismatch: requested: %d, internal: %d", this.requestedShot, this.shotCounter + 1));
        }

        String fileName = String.format("%s_%06d.%s", this.dateString, this.shotCounter + 1, this.filenameExtension);

        File targetFile = new File(this.savePath, fileName);
        // Check that we are not overwriting anything, and increase the counter until we find a non-existing filename.
        // This should normally never happen. Maybe we should abort completely if this happens?
        while (targetFile.exists())
        {
            fileName = String.format("%s_%06d.%s", this.dateString, ++this.shotCounter + 1, this.filenameExtension);
            targetFile = new File(this.savePath, fileName);
        }

        try
        {
            if (this.imgFormat == 0) // PNG
            {
                ImageIO.write(this.bufferedImage, this.filenameExtension, targetFile);
            }
            else
            {
                ImageOutputStream ios = ImageIO.createImageOutputStream(targetFile);
                Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
                ImageWriter writer = iter.next();
                ImageWriteParam iwp = writer.getDefaultWriteParam();
                iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                writer.setOutput(ios);

                if (this.imgFormat == 1) // JPG, quality 75
                {
                    iwp.setCompressionQuality(0.75f);
                }
                else if (this.imgFormat == 2) // JPG, quality 80
                {
                    iwp.setCompressionQuality(0.80f);
                }
                else if (this.imgFormat == 3) // JPG, quality 85
                {
                    iwp.setCompressionQuality(0.85f);
                }
                else if (this.imgFormat == 4) // JPG, quality 90
                {
                    iwp.setCompressionQuality(0.90f);
                }
                else if (this.imgFormat == 5) // JPG, quality 95
                {
                    iwp.setCompressionQuality(0.95f);
                }

                writer.write(null, new IIOImage(this.bufferedImage, null, null), iwp);
                writer.dispose();
                ios.close();
            }
        }
        catch(Exception e)
        {
            Multishot.logger.fatal("Exception while saving screenshot:");
            e.printStackTrace();
        }

        long timeStop = System.currentTimeMillis();
        //System.out.printf("Multishot: Saving took %d ms\n", timeStop - timeStart);

        if ((timeStop - timeStart) >= (this.shotInterval * 100)) // shotInterval is in 0.1 seconds, aka 100ms
        {
            Multishot.logger.warn("Warning: Saving the screenshot took longer ({} ms) than the set interval ({} ms)!",
                    timeStop - timeStart, this.shotInterval * 100);
            Multishot.logger.warn("As a result, the expected timing will be skewed, and this will cause lag spikes! Try increasing the Interval.");
        }

        String msg = new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis()));
        msg = msg + String.format(": Saved screenshot as %s_%06d.%s", this.dateString, this.shotCounter, this.filenameExtension);
        MsGui.getGui().addGuiMessage(msg);

        this.shotCounter++;
        this.hasData = false;

        return this.shotCounter;
    }

    public void trigger(int requestedShot)
    {
        synchronized(this)
        {
            this.requestedShot = requestedShot;
            this.takeScreenshot();
        }
    }
}
