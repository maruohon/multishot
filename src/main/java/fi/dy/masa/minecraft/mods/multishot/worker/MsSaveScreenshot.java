package fi.dy.masa.minecraft.mods.multishot.worker;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;
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
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import fi.dy.masa.minecraft.mods.multishot.Multishot;
import fi.dy.masa.minecraft.mods.multishot.gui.MsGui;
import fi.dy.masa.minecraft.mods.multishot.reference.MsReference;

@SideOnly(Side.CLIENT)
public class MsSaveScreenshot
{
    private Minecraft mc;
    private Framebuffer fb;
    private static MsSaveScreenshot instance = null;
    private String threadName;
    private boolean saving;
    private boolean trigger;
    private long shotInterval; // Screenshot interval, in 0.1 seconds, used in checking if we manage to save the screenshots in time to not lag behind
    private int shotCounter; // The actual shot counter, increments linearly
    private int requestedShot; // The shot number requested by the main thread
    private int imgFormat;
    private String basePath;
    private String savePath;
    private String dateString;
    private String filenameExtension;
    private int width;
    private int height;
    private static IntBuffer pixelBuffer = null;
    private static int[] pixelValues = null;

    public MsSaveScreenshot(String path, int interval, int imgfmt)
    {
        this.mc = Minecraft.getMinecraft();
        this.fb = this.mc.getFramebuffer();
        instance = this;
        this.saving = false;
        this.trigger = false;
        this.shotInterval = interval;
        this.imgFormat = imgfmt;
        this.shotCounter = 0;
        this.basePath = path;
        this.dateString = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date(System.currentTimeMillis()));
        if (this.imgFormat == 0) { this.filenameExtension = "png"; }
        else { this.filenameExtension = "jpg"; }
        if (this.basePath.endsWith("/") == false)
        {
            this.basePath = this.basePath.concat("/");
        }
        // We save the screenshots in a separate sub directory each time, named after the start timestamp
        this.savePath = this.basePath.concat(dateString + "/");
        File multishotDir = new File(this.savePath);
        if (multishotDir.isDirectory() == false)
        {
            if (multishotDir.mkdir() == false)
            {
                System.out.println(MsReference.MOD_NAME + ": Error: Could not create directory '" + this.savePath + "'");
            }
        }
        this.width = this.mc.displayWidth;
        this.height = this.mc.displayHeight; 
    }

    synchronized public static MsSaveScreenshot getInstance()
    {
        return instance;
    }

    synchronized public static void clearInstance()
    {
        instance = null;
    }

    synchronized public int getCounter()
    {
        return this.shotCounter;
    }

    synchronized private void readBuffer()
    {
        //System.out.println("readBuffer() start"); // FIXME debug
        if (OpenGlHelper.isFramebufferEnabled())
        {
            this.width = this.fb.framebufferTextureWidth;
            this.height = this.fb.framebufferTextureHeight;
        }
        else
        {
            this.width = this.mc.displayWidth;
            this.height = this.mc.displayHeight;
        }

        int size = this.width * this.height;
        if (pixelBuffer == null || pixelBuffer.capacity() < size)
        {
            pixelBuffer = BufferUtils.createIntBuffer(size);
            pixelValues = new int[size];
        }

        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        pixelBuffer.clear();

        if (OpenGlHelper.isFramebufferEnabled())
        {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.fb.framebufferTexture);
            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
        }
        else
        {
            GL11.glReadPixels(0, 0, this.width, this.height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
        }

        //System.out.println("readBuffer() end"); // FIXME debug
    }

    synchronized public void saveToFile()
    {
        long timeStart = System.currentTimeMillis();
        //this.readBuffer(); // This is done in trigger() so that the buffer read is done in the main thread

        this.saving = true;

        if (this.requestedShot != (this.shotCounter + 1))
        {
            System.out.printf(MsReference.MOD_NAME + ": saveScreenshot(): shotCounter mismatch: requested: %d, internal: %d\n", this.requestedShot, this.shotCounter + 1);
        }

        pixelBuffer.get(pixelValues);
        TextureUtil.func_147953_a(pixelValues, this.width, this.height);

        //System.out.println("saveScreenshot(): before BufferedImage"); // FIXME debug
        BufferedImage bufferedImage = null;

        if (OpenGlHelper.isFramebufferEnabled())
        {
            bufferedImage = new BufferedImage(this.fb.framebufferWidth, this.fb.framebufferHeight, BufferedImage.TYPE_INT_RGB);
            int l = this.fb.framebufferTextureHeight - this.fb.framebufferHeight;

            for (int i = l; i < this.fb.framebufferTextureHeight; ++i)
            {
                for (int j = 0; j < this.fb.framebufferWidth; ++j)
                {
                    bufferedImage.setRGB(j, i - l, pixelValues[i * this.fb.framebufferTextureWidth + j]);
                }
            }
        }
        else
        {
            bufferedImage = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
            bufferedImage.setRGB(0, 0, this.width, this.height, pixelValues, 0, this.width);
        }

        String fullPath = String.format("%s%s_%06d.%s", this.savePath, this.dateString, this.shotCounter + 1, this.filenameExtension);

        File targetFile = new File(fullPath);
        // Check that we are not overwriting anything, and increase the counter until we find a non-existing filename.
        // FIXME this is still a bad way of doing this. For one, this should normally never happen. Maybe we should abort completely if this happens?
        while (targetFile.exists() == true)
        {
            fullPath = String.format("%s%s_%06d.%s", this.savePath, this.dateString, ++this.shotCounter + 1, this.filenameExtension);
            targetFile = new File(fullPath);
        }

        try
        {
            if (this.imgFormat == 0) // PNG
            {
                ImageOutputStream ios = ImageIO.createImageOutputStream(targetFile);
                Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("png");
                ImageWriter writer = iter.next();
                ImageWriteParam iwp = writer.getDefaultWriteParam();
                //iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                //iwp.setCompressionQuality(0.85f);
                //System.out.println("PNG quality: " + iwp.getCompressionQuality());
                writer.setOutput(ios);
                writer.write(null, new IIOImage(bufferedImage, null, null), iwp);
                writer.dispose();

                //ImageIO.write(bufferedImage, this.filenameExtension, targetFile);
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

                writer.write(null, new IIOImage(bufferedImage, null, null), iwp);
                writer.dispose();
            }
        }
        catch(Exception e)
        {
            Multishot.logSevere("Exception while saving screenshot:");
            e.printStackTrace();
        }

        long timeStop = System.currentTimeMillis();
        //System.out.printf("Multishot: Saving took %d ms\n", timeStop - timeStart); // FIXME debug
        if ((timeStop - timeStart) >= (this.shotInterval * 100)) // shotInterval is in 0.1 seconds, aka 100ms
        {
            System.out.println(MsReference.MOD_NAME + ": Warning: Saving the screenshot took longer than the set interval!");
            System.out.println(MsReference.MOD_NAME + ": As a result, the expected timing will be skewed! Try increasing the Interval.");
        }
        this.saving = false;
        this.shotCounter++;
        String msg = new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis()));
        msg = msg.concat(String.format(": Saved screenshot as %s_%06d.%s", this.dateString, this.shotCounter, this.filenameExtension));
        MsGui.getInstance().addMessage(msg);
        notify();
    }

    synchronized public void trigger(int shotNum)
    {
        while (this.saving == true)
        {
            try
            {
                System.out.println(MsReference.MOD_NAME + ": Warning: Waiting for trigger to become available, this will cause lag. Try increasing the Interval.");
                wait();
            }
            catch (InterruptedException e)
            {
                System.out.println(this.threadName + " interrupted in trigger()");
            }
        }
        this.readBuffer();
        this.requestedShot = shotNum;
        this.trigger = true;
        notify();
    }

    synchronized public boolean triggerActivated()
    {
        if (this.trigger == false)
        {
            try
            {
                // Wait for a notify, or max 100ms
                // (the limit is here so that we don't get stuck waiting for the trigger infinitely, when stop has been requested in the thread class)
                wait(100);
            }
            catch (InterruptedException e)
            {
                System.out.println(this.threadName + " interrupted in triggerActivated()");
            }
        }
        boolean tmp;
        tmp = this.trigger;
        this.trigger = false;
        return tmp;
    }
}
