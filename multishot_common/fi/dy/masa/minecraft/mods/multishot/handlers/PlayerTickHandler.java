package fi.dy.masa.minecraft.mods.multishot.handlers;

import java.util.EnumSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
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
	private Minecraft mc = null;
	private long lastCheckTime = 0;
	private long shotTimer = 0;

	public PlayerTickHandler()
	{
		super();
		this.multishotConfigs = MultishotConfigs.getInstance();
		this.mc = Minecraft.getMinecraft();
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		if (MultishotState.getRecording() == true || MultishotState.getMotion() == true)
		{
			if (MultishotState.getControlsLocked() == true)
			{
				KeyBinding.unPressAllKeys();
				this.mc.setIngameNotInFocus();
			}
		}
		if (MultishotState.getMotion() == true)
		{
			EntityPlayer player = this.mc.thePlayer;
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
			//Vec3 pos = player.getPosition(1.0f);
			//player.setPositionAndRotation(pos.xCoord + mx, pos.yCoord + my, pos.zCoord + mz, player.rotationYaw + yaw, player.rotationPitch + pitch);
			player.moveEntity(mx, my, mz);
			player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw + yaw, player.rotationPitch + pitch);
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
		if (MultishotState.getRecording() == true && this.multishotConfigs.getInterval() > 0 && MultishotThread.getInstance() != null)
		{
			if (MultishotState.getPaused() == false)
			{
				if (this.multishotConfigs.getActiveTimer() != 0)
				{
					// We hit the number of shots set in the current timed configuration
					if (SaveScreenshot.getInstance() != null && SaveScreenshot.getInstance().getCounter() >= this.multishotConfigs.getActiveTimerNumShots())
					{
						MultishotState.setRecording(false);
						if (MultishotState.getMultishotThread() != null)
						{
							MultishotState.getMultishotThread().setStop();
							SaveScreenshot.clearInstance();
							if ((MultishotState.getMotion() == false && MultishotState.getRecording() == false) ||
									MultishotState.getControlsLocked() == false)
							{
								this.mc.setIngameFocus();
							}
						}
						this.mc.gameSettings.fovSetting = MultishotState.getFov(); // Restore the normal FoV value
						return;
					}
				}
				long currentTime = System.currentTimeMillis();
				if (currentTime < this.lastCheckTime)
				{
					// Time ran backwards, estimate 50ms has passed since the last time based on the tick system
					this.lastCheckTime = currentTime;
					this.shotTimer += 50;
				}
				else if ((currentTime - this.lastCheckTime) >= 50)
				{
					this.shotTimer += currentTime - this.lastCheckTime;
				}
				else
				{
					// Less than 50ms from the last check: Assume the tick system is more accurate than our measurement
					this.shotTimer += 50;
				}
				this.lastCheckTime = currentTime;
				long interval = (long)this.multishotConfigs.getInterval() * 100;
				if (this.shotTimer >= interval)
				{
					SaveScreenshot.getInstance().trigger(MultishotState.getShotCounter());
					MultishotState.incrementShotCounter();
					this.shotTimer = 0;
				}
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
