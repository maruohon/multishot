package fi.dy.masa.minecraft.mods.multishot.handlers;

import java.util.UUID;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import fi.dy.masa.minecraft.mods.multishot.config.Configs;
import fi.dy.masa.minecraft.mods.multishot.motion.Motion;
import fi.dy.masa.minecraft.mods.multishot.state.State;
import fi.dy.masa.minecraft.mods.multishot.util.EntityPlayerCamera;
import fi.dy.masa.minecraft.mods.multishot.worker.RecordingHandler;

public class RenderEventHandler
{
    private static RenderEventHandler instance;
    private RecordingHandler recordingHandler;
    private Minecraft mc;
    private Entity viewEntityOriginal;
    private EntityPlayerCamera cameraEntity;
    private boolean renderingFreeCamera;
    private int shotNumber;
    private boolean trigger;

    public RenderEventHandler()
    {
        this.mc = Minecraft.getMinecraft();
        this.recordingHandler = new RecordingHandler();
        instance = this;
    }

    public static RenderEventHandler instance()
    {
        return instance;
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event)
    {
        World world = event.getWorld();

        if (world.isRemote && this.cameraEntity != null && this.cameraEntity.getEntityWorld() == world)
        {
            Motion.getMotion().stopMotion();
            State.setPaused(false);
            this.cameraEntity = null;
        }
    }

    @SubscribeEvent
    public void onGetFOV(EntityViewRenderEvent.FOVModifier event)
    {
        if (State.getRecording())
        {
            // Always set the FoV, to prevent sprinting or speed effects from affecting the camera's FoV.
            if ((Configs.getUseFreeCamera() && event.getEntity() == this.cameraEntity) ||
                (Configs.getUseFreeCamera() == false))
            {
                // 0..140 is somewhat "sane"
                event.setFOV(70.0f - ((float) Configs.getZoom() * 69.9f / 100.0f));
            }
        }
    }

    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event)
    {
        if (Configs.getUseFreeCamera() && (State.getMotion() || State.getRecording()))
        {
            RenderManager manager = this.mc.getRenderManager();

            // This (plus the onRenderLivingPre to reset this) fixes the player not getting rendered
            // in the free camera view in single player because of the !entity.isUser() check in RenderPlayer.doRender().
            if (this.renderingFreeCamera && event.getEntityPlayer().isUser())
            {
                this.viewEntityOriginal = manager.renderViewEntity;
                manager.renderViewEntity = this.mc.player;
            }
        }
    }

    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.Pre<EntityPlayer> event)
    {
        if (this.viewEntityOriginal != null)
        {
            this.mc.getRenderManager().renderViewEntity = this.viewEntityOriginal;
            this.viewEntityOriginal = null;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderWorldLast(RenderWorldLastEvent event)
    {
        // Render the free camera entity for the player, but be sure to not render
        // from within the renderWorld() call of the free camera entity
        if (Configs.getUseFreeCamera() &&
            (State.getMotion() || State.getRecording()) &&
            this.renderingFreeCamera == false &&
            State.getHideGui() == false)
        {
            this.mc.getRenderManager().renderEntityStatic(this.getCameraEntity(), event.getPartialTicks(), false);
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if (event.phase == Phase.START || this.mc.isGamePaused())
        {
            return;
        }

        if (State.getRecording())
        {
            if (this.trigger)
            {
                this.renderingFreeCamera = true;
                this.recordingHandler.trigger(this.shotNumber);
                this.renderingFreeCamera = false;
                this.trigger = false;
            }
        }

        Motion motion = Motion.getMotion();

        // "The interpolated method", see MsMotion.reOrientPlayerToAngle() and MsMotion.toggleMotion() for the other bits of this code
        // Update the player rotation and pitch here in smaller steps, so that the camera doesn't jitter so terribly
        if (Configs.getUseFreeCamera() == false &&
            State.getMotion() && State.getPaused() == false && motion.getDoPlayerReorientation())
        {
            float partialTicks = event.renderTickTime;
            float yaw = motion.prevYaw + (motion.yawIncrement * partialTicks);
            float pitch = motion.prevPitch + (motion.pitchIncrement * partialTicks);
            //if (yaw > 180.0f) { yaw -= 360.0f; }
            //else if (yaw < -180.0f) { yaw += 360.0f; }

            EntityPlayer p = this.mc.player;
            //p.setLocationAndAngles(p.posX, p.posY, p.posZ, yaw, pitch);
            p.rotationYaw = yaw;
            p.prevRotationYaw = yaw;
            p.rotationPitch = pitch;
            p.prevRotationPitch = pitch;
        }
    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent event)
    {
        for (MarkerColor color : MarkerColor.values())
        {
            event.getMap().registerSprite(getMarkerTexture(color.getModelLocation()));
        }
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event)
    {
        for (MarkerColor color : MarkerColor.values())
        {
            ModelResourceLocation resource = color.getModelLocation();
            event.getModelRegistry().putObject(resource, this.bakeMarkerModel(resource));
        }
    }

    private IBakedModel bakeMarkerModel(ModelResourceLocation key)
    {
        ResourceLocation texture = getMarkerTexture(key);
        IModel model = new ItemLayerModel(ImmutableList.<ResourceLocation>of(texture));
        return model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());
    }

    public static ResourceLocation getMarkerTexture(ModelResourceLocation key)
    {
        return new ResourceLocation(key.getNamespace(), "markers/" + key.getPath());
    }

    public void trigger(int shotNumber)
    {
        this.shotNumber = shotNumber;
        this.trigger = true;
    }

    /**
     * Gets (and creates if necessary) the camera player entity.<br>
     * <b>Note:</b> If the free camera mode is disabled, then this will return null!
     * @return
     */
    @Nullable
    public EntityPlayer getCameraEntity()
    {
        return this.getOrCreateCameraEntity(this.mc.world);
    }

    private EntityPlayer getOrCreateCameraEntity(World world)
    {
        if (this.cameraEntity == null && world != null && Configs.getUseFreeCamera())
        {
            GameProfile profile = new GameProfile(UUID.fromString("30297bff-8431-4d08-b76a-9acfaa6829f8"), "Camera"); //player.getGameProfile();
            EntityPlayerCamera camera = new EntityPlayerCamera(world, profile);
            camera.noClip = true;
            EntityPlayer player = this.mc.player;

            if (player != null)
            {
                camera.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
                camera.setRotationYawHead(player.rotationYaw);
            }
            //camera.setPositionAndRotationDirect(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch, 3, false);
            //camera.setPosition(player.posX, player.posY, player.posZ);
            //camera.rotationYaw = player.rotationYaw;
            //camera.rotationPitch = player.rotationPitch;
            this.cameraEntity = camera;
        }

        return this.cameraEntity;
    }

    public enum MarkerColor
    {
        BLUE    ("multishot:marker_blue"),
        RED     ("multishot:marker_red"),
        ORANGE  ("multishot:marker_orange"),
        YELLOW  ("multishot:marker_yellow"),
        CYAN    ("multishot:marker_cyan");

        private final ModelResourceLocation location;

        private MarkerColor(String resource)
        {
            this.location = new ModelResourceLocation(resource, "normal");
        }

        public ModelResourceLocation getModelLocation()
        {
            return this.location;
        }
    }

    /*
        // for debugging: (RenderGameOverlayEvent event)
        // FIXME debug stuff:
        // Note: the text will only get rendered when using the RenderGameOverlayEvent, not using the RenderWorldLastEvent
        // Then again, we can't use RenderGameOverlayEvent to actually do the rotation stuff, because that event won't
        // happen when the HUD is hidden (with F1).
        if (MsScreenBase.isCtrlKeyDown())
        {
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            String s1 = String.format("ms prevYaw: %f", MsClassReference.getMotion().prevYaw);
            String s2 = String.format("mc prevYaw: %f", this.mc.thePlayer.prevRotationYaw);
            String s3 = String.format("rotationYaw: %f", this.mc.thePlayer.rotationYaw);
            String s4 = String.format("yawInc: %f", MsClassReference.getMotion().yawIncrement);
            String s5 = String.format("yaw: %f", yaw);
            String s6 = String.format("MC yaw: %f", this.mc.thePlayer.rotationYaw);
            String s7 = String.format("MC pitch: %f", this.mc.thePlayer.rotationPitch);
            //String s6 = String.format("targetAtan2: %f", MsClassReference.getMotion().targetAtan2);
            //String s7 = String.format("targetAtan2Deg: %f", MsClassReference.getMotion().targetAtan2Deg);
            this.mc.fontRenderer.drawStringWithShadow(s1, 5, 20, 0xffffffff);
            this.mc.fontRenderer.drawStringWithShadow(s2, 5, 30, 0xffffffff);
            this.mc.fontRenderer.drawStringWithShadow(s3, 5, 40, 0xffffffff);
            this.mc.fontRenderer.drawStringWithShadow(s4, 5, 50, 0xffffffff);
            this.mc.fontRenderer.drawStringWithShadow(s5, 5, 60, 0xffffffff);
            this.mc.fontRenderer.drawStringWithShadow(s6, 5, 70, 0xffffffff);
            this.mc.fontRenderer.drawStringWithShadow(s7, 5, 80, 0xffffffff);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
            //System.out.printf("ms prevYaw: %f mc prevYaw: %f yawInc: %f yaw: %f\n", MsClassReference.getMotion().prevYaw, this.mc.thePlayer.prevRotationYaw, MsClassReference.getMotion().yawIncrement, yaw);
        }
*/

}
