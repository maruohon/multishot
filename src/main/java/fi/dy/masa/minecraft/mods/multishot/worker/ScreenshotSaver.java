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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import fi.dy.masa.minecraft.mods.multishot.Multishot;
import fi.dy.masa.minecraft.mods.multishot.config.Configs;
import fi.dy.masa.minecraft.mods.multishot.gui.MsGui;
import fi.dy.masa.minecraft.mods.multishot.handlers.RenderEventHandler;
import fi.dy.masa.minecraft.mods.multishot.render.EntityRendererCustom;

public class ScreenshotSaver
{
    private Minecraft mc;
    private Framebuffer frameBuffer;
    private boolean hasData;
    private boolean useFreeCamera;
    private int width;
    private int height;
    private long shotInterval; // Screenshot interval, in 0.1 seconds, used in checking if we manage to save the screenshots in time to not lag behind
    private int shotCounter; // The actual shot counter, increments linearly
    private int requestedShot; // The shot number requested by the main thread
    private int imgFormat;
    private File savePath;
    private String dateString;
    private String filenameExtension;
    private static IntBuffer pixelBuffer;
    private static int[] pixelValues;

    public ScreenshotSaver(String basePath, int interval, int imgfmt, int width, int height, boolean useFreeCamera)
    {
        this.mc = Minecraft.getMinecraft();
        this.width = width;
        this.height = height;
        this.shotInterval = interval;
        this.imgFormat = imgfmt;
        this.useFreeCamera = useFreeCamera;
        this.dateString = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date(System.currentTimeMillis()));

        if (useFreeCamera)
        {
            this.frameBuffer = new Framebuffer(width, height, true);
            this.frameBuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        }
        else
        {
            this.frameBuffer = this.mc.getFramebuffer();
        }

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
            if (this.savePath.mkdirs() == false)
            {
                Multishot.logger.warn("Error: Could not create directory '{}'", this.savePath.getPath());
            }
        }
    }

    private void renderFreeCameraScene()
    {
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();

        this.frameBuffer.bindFramebuffer(true);

        //GlStateManager.clear(GL11.GL_STENCIL_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();

        GlStateManager.color(1f, 0f, 0f);

        Entity viewEntity = this.mc.getRenderViewEntity();
        this.mc.setRenderViewEntity(RenderEventHandler.instance().getCameraEntity());

        boolean hideGui = mc.gameSettings.hideGUI;
        int tp = this.mc.gameSettings.thirdPersonView;
        int w = this.mc.displayWidth;
        int h = this.mc.displayHeight;
        this.mc.displayWidth = this.width;
        this.mc.displayHeight = this.height;
        this.mc.gameSettings.hideGUI = true;
        this.mc.gameSettings.thirdPersonView = 0;

        if (Configs.freeCameraUseCustomRenderer)
        {
            try
            {
                EntityRendererCustom.getInstance().renderWorld(this.mc.entityRenderer, 1.0F);
            }
            catch (Throwable t)
            {
                Multishot.logger.error("Failed to render the free camera scene using the custom renderer!");
            }
        }
        else
        {
            this.mc.entityRenderer.renderWorld(1.0F, 0L);
        }

        this.mc.displayWidth = w;
        this.mc.displayHeight = h;
        this.mc.gameSettings.thirdPersonView = tp;
        this.mc.gameSettings.hideGUI = hideGui;
        this.mc.setRenderViewEntity(viewEntity);

        //EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);

        this.frameBuffer.unbindFramebuffer();
        GlStateManager.popMatrix();
        GlStateManager.color(1f, 1f, 1f);

        this.captureFrame(this.width, this.height, this.frameBuffer);
        //this.frameBuffer.unbindFramebuffer();
    }

    private void captureFrame(int width, int height, Framebuffer framebufferIn)
    {
        if (OpenGlHelper.isFramebufferEnabled())
        {
            width = framebufferIn.framebufferTextureWidth;
            height = framebufferIn.framebufferTextureHeight;
        }

        int i = width * height;

        if (pixelBuffer == null || pixelBuffer.capacity() < i)
        {
            pixelBuffer = BufferUtils.createIntBuffer(i);
            pixelValues = new int[i];
        }

        GlStateManager.glPixelStorei(3333, 1);
        GlStateManager.glPixelStorei(3317, 1);
        pixelBuffer.clear();

        if (OpenGlHelper.isFramebufferEnabled())
        {
            GlStateManager.bindTexture(framebufferIn.framebufferTexture);
            GlStateManager.glGetTexImage(3553, 0, 32993, 33639, pixelBuffer);
        }
        else
        {
            GlStateManager.glReadPixels(0, 0, width, height, 32993, 33639, pixelBuffer);
        }

        this.hasData = true;
        this.notify();
    }

    private BufferedImage createScreenshot(int width, int height, Framebuffer frameBuffer)
    {
        this.hasData = false;

        pixelBuffer.get(pixelValues);
        TextureUtil.processPixelValues(pixelValues, width, height);
        BufferedImage bufferedImage;

        if (OpenGlHelper.isFramebufferEnabled())
        {
            bufferedImage = new BufferedImage(frameBuffer.framebufferWidth, frameBuffer.framebufferHeight, 1);
            int j = frameBuffer.framebufferTextureHeight - frameBuffer.framebufferHeight;

            for (int k = j; k < frameBuffer.framebufferTextureHeight; ++k)
            {
                for (int l = 0; l < frameBuffer.framebufferWidth; ++l)
                {
                    bufferedImage.setRGB(l, k - j, pixelValues[k * frameBuffer.framebufferTextureWidth + l]);
                }
            }
        }
        else
        {
            bufferedImage = new BufferedImage(width, height, 1);
            bufferedImage.setRGB(0, 0, width, height, pixelValues, 0, width);
        }

        return bufferedImage;
    }

    private int saveToFileImpl()
    {
        long timeStart = System.currentTimeMillis();
        BufferedImage bufferedImage = this.createScreenshot(this.width, this.height, this.frameBuffer);

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
                ImageIO.write(bufferedImage, this.filenameExtension, targetFile);
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
        MsGui.getGui().addGuiMessage(msg);

        this.shotCounter++;

        return this.shotCounter;
    }

    public void deleteFrameBuffer()
    {
        if (this.useFreeCamera)
        {
            this.frameBuffer.deleteFramebuffer();
        }
    }

    public int saveToFile()
    {
        synchronized (this)
        {
            while (this.hasData == false)
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

            return this.saveToFileImpl();
        }
    }

    public void trigger(int requestedShot)
    {
        synchronized (this)
        {
            this.requestedShot = requestedShot;

            if (this.useFreeCamera)
            {
                this.renderFreeCameraScene();
            }
            else
            {
                this.captureFrame(this.width, this.height, this.mc.getFramebuffer());
            }
        }
    }
}
