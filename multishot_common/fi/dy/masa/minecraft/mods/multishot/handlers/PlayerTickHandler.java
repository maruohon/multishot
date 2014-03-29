package fi.dy.masa.minecraft.mods.multishot.handlers;

import java.util.EnumSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.config.MultishotConfigs;
import fi.dy.masa.minecraft.mods.multishot.output.MultishotThread;
import fi.dy.masa.minecraft.mods.multishot.output.SaveScreenshot;
import fi.dy.masa.minecraft.mods.multishot.state.MultishotState;

@SideOnly(Side.CLIENT)
public class PlayerTickHandler implements ITickHandler
{
	private MultishotConfigs multishotConfigs = null;
	private long lastShotTime = 0;

	public PlayerTickHandler()
	{
		super();
		this.multishotConfigs = MultishotConfigs.getInstance();
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (MultishotState.getRecording() == true || MultishotState.getMotion() == true)
		{
			if (MultishotState.getControlsLocked() == true)
			{
				KeyBinding.unPressAllKeys();
				Minecraft.getMinecraft().setIngameNotInFocus();
			}
		}
		if (MultishotState.getMotion() == true)
		{
			double mx, my, mz;
			float yaw, pitch;
			mx = this.multishotConfigs.getMotionX();
			mz = this.multishotConfigs.getMotionZ();
			my = this.multishotConfigs.getMotionY();
			yaw = this.multishotConfigs.getRotationYaw();
			pitch = this.multishotConfigs.getRotationPitch();
			//player.setPositionAndUpdate(pos.xCoord + x, pos.yCoord + y, pos.zCoord + z); // Does strange things...
			//player.setVelocity(mx, my, mz); // Doesn't work for values < 0.005
			// FIXME: causes strange glitching up/down if sneaking while moving
			Vec3 pos = player.getPosition(1.0f);
			player.setPositionAndRotation(pos.xCoord + mx, pos.yCoord + my, pos.zCoord + mz, player.rotationYaw + yaw, player.rotationPitch + pitch);
			//player.moveEntity(mx, my, mz);
			//player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw + yaw, player.rotationPitch + pitch);
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
		if (MultishotState.getRecording() == true && this.multishotConfigs.getInterval() > 0 && MultishotThread.getInstance() != null)
		{
			long currentTime = System.currentTimeMillis();
			long interval = (long)this.multishotConfigs.getInterval() * 100;
			if ((currentTime - this.lastShotTime) >= interval)
			{
				this.lastShotTime = currentTime;
				SaveScreenshot.getInstance().trigger(MultishotState.getShotCounter());
				MultishotState.incrementShotCounter();
				//System.out.println("tickEnd() after trigger() call"); // FIXME debug
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public String getLabel()
	{
		return "Multishot: Player Tick";
	}
}
