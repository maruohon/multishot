package fi.dy.masa.minecraft.mods.multishot.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import fi.dy.masa.minecraft.mods.multishot.motion.Motion;
import fi.dy.masa.minecraft.mods.multishot.state.State;

public class MotionHandler
{
    private Minecraft mc;

    public MotionHandler()
    {
        this.mc = Minecraft.getMinecraft();
    }

    @SubscribeEvent
    // for debugging: (RenderGameOverlayEvent event)
    public void updatePlayerRotation(RenderWorldLastEvent event)
    {
        if (this.mc.isGamePaused() == true)
        {
            return;
        }

        Motion motion = Motion.getMotion();
        float yaw = motion.prevYaw + (motion.yawIncrement * event.partialTicks);
        float pitch = motion.prevPitch + (motion.pitchIncrement * event.partialTicks);
        //if (yaw > 180.0f) { yaw -= 360.0f; }
        //else if (yaw < -180.0f) { yaw += 360.0f; }

        // "The interpolated method", see MsMotion.reOrientPlayerToAngle() and MsMotion.toggleMotion() for the other bits of this code
        // Update the player rotation and pitch here in smaller steps, so that the camera doesn't jitter so terribly
        if (State.getMotion() == true && motion.getDoReorientation() == true)
        {
            EntityPlayer p = this.mc.thePlayer;
            p.rotationYaw = yaw;
            p.prevRotationYaw = yaw;
            p.rotationPitch = pitch;
            p.prevRotationPitch = pitch;
        }

/*
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
}
