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
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.Multishot;

@SideOnly(Side.CLIENT)
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
    private int width;
    private int height;
    private static IntBuffer pixelBuffer = null;
    private static int[] pixelValues = null;

    public ScreenshotSaver(String basePath, int interval, int imgfmt)
    {
        this.mc = Minecraft.getMinecraft();
        this.fb = this.mc.getFramebuffer();
        this.width = this.mc.displayWidth;
        this.height = this.mc.displayHeight;
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
            GlStateManager.bindTexture(this.fb.framebufferTexture);
            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
        }
        else
        {
            GL11.glReadPixels(0, 0, this.width, this.height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
        }

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

        pixelBuffer.get(pixelValues);
        TextureUtil.processPixelValues(pixelValues, this.width, this.height);
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
                ios.close();

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
        MsThread.addGuiMessage(msg);

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
