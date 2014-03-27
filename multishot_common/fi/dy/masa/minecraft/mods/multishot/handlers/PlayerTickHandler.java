package fi.dy.masa.minecraft.mods.multishot.handlers;

import java.util.EnumSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import fi.dy.masa.minecraft.mods.multishot.config.MultishotConfigs;
import fi.dy.masa.minecraft.mods.multishot.state.MultishotState;

public class PlayerTickHandler implements ITickHandler
{
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
			mx = MultishotConfigs.getInstance().getMotionX();
			mz = MultishotConfigs.getInstance().getMotionZ();
			my = MultishotConfigs.getInstance().getMotionY();
			yaw = MultishotConfigs.getInstance().getRotationYaw();
			pitch = MultishotConfigs.getInstance().getRotationPitch();
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
