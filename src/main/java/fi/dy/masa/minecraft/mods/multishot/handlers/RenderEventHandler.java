package fi.dy.masa.minecraft.mods.multishot.handlers;

import java.util.UUID;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
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
        this.recordingHandler = new RecordingHandler(this);
        instance = this;
    }

    public static RenderEventHandler instance()
    {
        return instance;
    }

    public EntityPlayer getCameraEntity()
    {
        return this.cameraEntity;
    }

    public void trigger(int shotNumber)
    {
        this.shotNumber = shotNumber;
        this.trigger = true;
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        if (event.getWorld().isRemote)
        {
            this.createCameraEntity(event.getWorld());
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event)
    {
        if (event.getWorld().isRemote)
        {
            if (State.getMotion())
            {
                Motion.getMotion().toggleMotion(this.mc.player);
            }

            if (State.getRecording())
            {
                RecordingHandler.getInstance().stopRecording();
            }

            if (this.mc.world == null)
            {
                this.cameraEntity = null;
            }
        }
    }

    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event)
    {
        if (Configs.getConfig().getUseFreeCamera() && (State.getMotion() || State.getRecording()))
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

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event)
    {
        // Don't render from within the renderWorld() call of the free camera entity
        if (this.renderingFreeCamera == false && State.getHideGui() == false)
        {
            // Render the free camera entity for the player
            if (Configs.getConfig().getUseFreeCamera() && (State.getMotion() || State.getRecording()))
            {
                if (this.cameraEntity != null)
                {
                    this.mc.getRenderManager().renderEntityStatic(this.cameraEntity, event.getPartialTicks(), false);
                }
            }
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
        if (Configs.getConfig().getUseFreeCamera() == false &&
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

    private void createCameraEntity(World world)
    {
        EntityPlayer player = this.mc.player;

        if (Configs.getConfig().getUseFreeCamera())
        {
            GameProfile profile = new GameProfile(UUID.fromString("30297bff-8431-4d08-b76a-9acfaa6829f8"), "Camera"); //player.getGameProfile();
            EntityPlayerCamera camera = new EntityPlayerCamera(world, profile);
            camera.noClip = true;

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
