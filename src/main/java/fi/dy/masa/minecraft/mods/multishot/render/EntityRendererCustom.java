package fi.dy.masa.minecraft.mods.multishot.render;

import java.lang.invoke.MethodHandle;
import org.lwjgl.util.glu.Project;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.minecraft.mods.multishot.Multishot;
import fi.dy.masa.minecraft.mods.multishot.config.Configs;
import fi.dy.masa.minecraft.mods.multishot.util.MethodHandleUtils;
import fi.dy.masa.minecraft.mods.multishot.util.MethodHandleUtils.UnableToFindMethodHandleException;

public class EntityRendererCustom
{
    private MethodHandle methodHandle_EntityRenderer_renderCloudsCheck;
    private MethodHandle methodHandle_EntityRenderer_renderRainSnow;
    private MethodHandle methodHandle_EntityRenderer_setupCameraTransform;
    private MethodHandle methodHandle_EntityRenderer_setupFog;
    private MethodHandle methodHandle_EntityRenderer_updateFogColor;
    private MethodHandle methodHandle_EntityRenderer_updateLightmap;

    private static EntityRendererCustom instance;
    private final Minecraft mc;
    private int frameCount;

    private EntityRendererCustom()
    {
        this.mc = Minecraft.getMinecraft();

        try
        {
            this.methodHandle_EntityRenderer_renderCloudsCheck =
                MethodHandleUtils.getMethodHandleVirtual(
                    EntityRenderer.class, new String[] { "func_180437_a", "renderCloudsCheck" },
                        RenderGlobal.class, float.class, int.class, double.class, double.class, double.class);
        }
        catch (UnableToFindMethodHandleException e)
        {
            Multishot.logger.error("Failed to get a MethodHandle for EntityRenderer#renderCloudsCheck", e);
        }

        try
        {
            this.methodHandle_EntityRenderer_renderRainSnow =
                MethodHandleUtils.getMethodHandleVirtual(
                    EntityRenderer.class, new String[] { "func_78474_d", "renderRainSnow" }, float.class);
        }
        catch (UnableToFindMethodHandleException e)
        {
            Multishot.logger.error("Failed to get a MethodHandle for EntityRenderer#renderRainSnow", e);
        }

        try
        {
            this.methodHandle_EntityRenderer_setupCameraTransform =
                MethodHandleUtils.getMethodHandleVirtual(
                    EntityRenderer.class, new String[] { "func_78479_a", "setupCameraTransform" }, float.class, int.class);

        }
        catch (UnableToFindMethodHandleException e)
        {
            Multishot.logger.error("Failed to get a MethodHandle for EntityRenderer#setupCameraTransform", e);
        }

        try
        {
            this.methodHandle_EntityRenderer_setupFog =
                MethodHandleUtils.getMethodHandleVirtual(
                    EntityRenderer.class, new String[] { "func_78468_a", "setupFog" }, int.class, float.class);
        }
        catch (UnableToFindMethodHandleException e)
        {
            Multishot.logger.error("Failed to get a MethodHandle for EntityRenderer#setupFog", e);
        }

        try
        {
            this.methodHandle_EntityRenderer_updateFogColor =
                MethodHandleUtils.getMethodHandleVirtual(
                    EntityRenderer.class, new String[] { "func_78466_h", "updateFogColor" }, float.class);
        }
        catch (UnableToFindMethodHandleException e)
        {
            Multishot.logger.error("Failed to get a MethodHandle for EntityRenderer#updateFogColor", e);
        }

        try
        {
            this.methodHandle_EntityRenderer_updateLightmap =
                MethodHandleUtils.getMethodHandleVirtual(
                    EntityRenderer.class, new String[] { "func_78472_g", "updateLightmap" }, float.class);
        }
        catch (UnableToFindMethodHandleException e)
        {
            Multishot.logger.error("Failed to get a MethodHandle for EntityRenderer#updateLightmap", e);
        }
    }

    public static EntityRendererCustom getInstance()
    {
        if (instance == null)
        {
            instance = new EntityRendererCustom();
        }

        return instance;
    }

    public void renderWorld(EntityRenderer renderer, float partialTicks) throws Throwable
    {
        this.methodHandle_EntityRenderer_updateLightmap.invokeExact(renderer, partialTicks);
        //renderer.updateLightmap(partialTicks);

        if (this.mc.getRenderViewEntity() == null)
        {
            this.mc.setRenderViewEntity(this.mc.player);
        }

        //renderer.getMouseOver(partialTicks);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.5F);
        this.mc.profiler.startSection("MultiShot_renderWorld");

        if (this.mc.gameSettings.anaglyph)
        {
            EntityRenderer.anaglyphField = 0;
            GlStateManager.colorMask(false, true, true, false);
            this.renderWorldPass(renderer, 0, partialTicks, 0L);
            EntityRenderer.anaglyphField = 1;
            GlStateManager.colorMask(true, false, false, false);
            this.renderWorldPass(renderer, 1, partialTicks, 0L);
            GlStateManager.colorMask(true, true, true, false);
        }
        else
        {
            this.renderWorldPass(renderer, 2, partialTicks, 0L);
        }

        this.mc.profiler.endSection();
    }

    private void renderWorldPass(EntityRenderer renderer, int pass, float partialTicks, long finishTimeNano) throws Throwable
    {
        RenderGlobal renderglobal = this.mc.renderGlobal;
        ParticleManager particlemanager = this.mc.effectRenderer;

        GlStateManager.enableCull();

        this.mc.profiler.endStartSection("clear");
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);

        if (Configs.freeCameraRenderFog)
        {
            this.methodHandle_EntityRenderer_updateFogColor.invokeExact(renderer, partialTicks);
            //renderer.updateFogColor(partialTicks);
        }

        GlStateManager.clear(16640);

        this.mc.profiler.endStartSection("camera");

        this.methodHandle_EntityRenderer_setupCameraTransform.invokeExact(renderer, partialTicks, pass);
        //renderer.setupCameraTransform(partialTicks, pass);

        ActiveRenderInfo.updateRenderInfo(this.mc.player, this.mc.gameSettings.thirdPersonView == 2);
        this.mc.profiler.endStartSection("frustum");
        ClippingHelperImpl.getInstance();
        this.mc.profiler.endStartSection("culling");

        ICamera icamera = new Frustum();
        Entity entity = this.mc.getRenderViewEntity();
        double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        icamera.setPosition(posX, posY, posZ);

        if (this.mc.gameSettings.renderDistanceChunks >= 4)
        {
            this.methodHandle_EntityRenderer_setupFog.invokeExact(renderer, -1, partialTicks);
            //renderer.setupFog(-1, partialTicks);

            this.mc.profiler.endStartSection("sky");
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();

            float fov = Configs.getFOV();
            float width = (float) this.mc.displayWidth;
            float height = (float) this.mc.displayHeight;
            Project.gluPerspective(fov, width / height, 0.05F, this.getFarPlaneDistance() * 2.0F);
            GlStateManager.matrixMode(5888);

            renderglobal.renderSky(partialTicks, pass);

            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            Project.gluPerspective(fov, width / height, 0.05F, this.getFarPlaneDistance() * MathHelper.SQRT_2);
            GlStateManager.matrixMode(5888);
        }

        this.methodHandle_EntityRenderer_setupFog.invokeExact(renderer, 0, partialTicks);
        //renderer.setupFog(0, partialTicks);
        GlStateManager.shadeModel(7425);

        if (Configs.freeCameraRenderClouds && entity.posY + entity.getEyeHeight() < 128.0D)
        {
            this.methodHandle_EntityRenderer_renderCloudsCheck.invokeExact(renderer, renderglobal, partialTicks, pass, posX, posY, posZ);
            //renderer.renderCloudsCheck(renderglobal, partialTicks, pass, posX, posY, posZ);
        }

        this.mc.profiler.endStartSection("prepareterrain");
        this.methodHandle_EntityRenderer_setupFog.invokeExact(renderer, 0, partialTicks);
        //renderer.setupFog(0, partialTicks);

        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        RenderHelper.disableStandardItemLighting();
        this.mc.profiler.endStartSection("terrain_setup");

        // was: renderer.frameCount++
        renderglobal.setupTerrain(entity, (double)partialTicks, icamera, this.frameCount++, this.mc.player.isSpectator());

        if (pass == 0 || pass == 2)
        {
            this.mc.profiler.endStartSection("updatechunks");
            this.mc.renderGlobal.updateChunks(finishTimeNano);
        }

        this.mc.profiler.endStartSection("terrain");
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.disableAlpha();

        renderglobal.renderBlockLayer(BlockRenderLayer.SOLID, (double)partialTicks, pass, entity);
        GlStateManager.enableAlpha();

        renderglobal.renderBlockLayer(BlockRenderLayer.CUTOUT_MIPPED, (double)partialTicks, pass, entity);
        this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);

        renderglobal.renderBlockLayer(BlockRenderLayer.CUTOUT, (double)partialTicks, pass, entity);

        this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        GlStateManager.shadeModel(7424);
        GlStateManager.alphaFunc(516, 0.1F);

        //if (! renderer.debugView)
        {
            GlStateManager.matrixMode(5888);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            RenderHelper.enableStandardItemLighting();
            this.mc.profiler.endStartSection("entities");
            net.minecraftforge.client.ForgeHooksClient.setRenderPass(0);

            renderglobal.renderEntities(entity, icamera, partialTicks);

            net.minecraftforge.client.ForgeHooksClient.setRenderPass(0);
            RenderHelper.disableStandardItemLighting();
            renderer.disableLightmap();
        }

        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();

        /*
        if (flag && this.mc.objectMouseOver != null && !entity.isInsideOfMaterial(Material.WATER))
        {
            EntityPlayer entityplayer = (EntityPlayer)entity;
            GlStateManager.disableAlpha();
            this.mc.mcProfiler.endStartSection("outline");
            if (!net.minecraftforge.client.ForgeHooksClient.onDrawBlockHighlight(renderglobal, entityplayer, mc.objectMouseOver, 0, partialTicks))
            renderglobal.drawSelectionBox(entityplayer, this.mc.objectMouseOver, 0, partialTicks);
            GlStateManager.enableAlpha();
        }

        if (this.mc.debugRenderer.shouldRender())
        {
            this.mc.debugRenderer.renderDebug(partialTicks, finishTimeNano);
        }
        */

        this.mc.profiler.endStartSection("destroyProgress");
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);

        renderglobal.drawBlockDamageTexture(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), entity, partialTicks);

        this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        GlStateManager.disableBlend();

        //if (! renderer.debugView)
        {
            renderer.enableLightmap();

            if (Configs.freeCameraRenderSpecialParticles)
            {
                this.mc.profiler.endStartSection("litParticles");
                particlemanager.renderLitParticles(entity, partialTicks);
            }

            RenderHelper.disableStandardItemLighting();

            this.methodHandle_EntityRenderer_setupFog.invokeExact(renderer, 0, partialTicks);
            //renderer.setupFog(0, partialTicks);
            this.mc.profiler.endStartSection("particles");

            particlemanager.renderParticles(entity, partialTicks);
            renderer.disableLightmap();
        }

        GlStateManager.enableCull(); // MultiShot: moved to above depthMask()

        if (Configs.freeCameraRenderWeather)
        {
            GlStateManager.depthMask(false);
            this.mc.profiler.endStartSection("weather");

            this.methodHandle_EntityRenderer_renderRainSnow.invokeExact(renderer, partialTicks);
            //renderer.renderRainSnow(partialTicks);
        }

        GlStateManager.depthMask(true);

        renderglobal.renderWorldBorder(entity, partialTicks);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.alphaFunc(516, 0.1F);

        this.methodHandle_EntityRenderer_setupFog.invokeExact(renderer, 0, partialTicks);
        //renderer.setupFog(0, partialTicks);
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.shadeModel(7425);
        this.mc.profiler.endStartSection("translucent");

        renderglobal.renderBlockLayer(BlockRenderLayer.TRANSLUCENT, (double)partialTicks, pass, entity);

        //if (! renderer.debugView) //Only render if render pass 0 happens as well.
        {
            RenderHelper.enableStandardItemLighting();
            this.mc.profiler.endStartSection("entities");
            net.minecraftforge.client.ForgeHooksClient.setRenderPass(1);

            renderglobal.renderEntities(entity, icamera, partialTicks);

            // restore blending function changed by RenderGlobal.preRenderDamagedBlocks
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            net.minecraftforge.client.ForgeHooksClient.setRenderPass(-1);
            RenderHelper.disableStandardItemLighting();
        }

        GlStateManager.shadeModel(7424);
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.disableFog();

        if (Configs.freeCameraRenderClouds && entity.posY + entity.getEyeHeight() >= 128.0D)
        {
            this.mc.profiler.endStartSection("aboveClouds");
            this.methodHandle_EntityRenderer_renderCloudsCheck.invokeExact(renderer, renderglobal, partialTicks, pass, posX, posY, posZ);
            //renderer.renderCloudsCheck(renderglobal, partialTicks, pass, posX, posY, posZ);
        }

        // This is disabled to avoid rendering modded overlays and stuff in the free camera view
        //this.mc.mcProfiler.endStartSection("forge_render_last");
        //net.minecraftforge.client.ForgeHooksClient.dispatchRenderLast(renderglobal, partialTicks);

        this.mc.profiler.endStartSection("hand");

        /*
        if (renderer.renderHand)
        {
            GlStateManager.clear(256);
            renderer.renderHand(partialTicks, pass);
        }
        */
    }

    private float getFarPlaneDistance()
    {
        return (float) (this.mc.gameSettings.renderDistanceChunks * 16);
    }
}
