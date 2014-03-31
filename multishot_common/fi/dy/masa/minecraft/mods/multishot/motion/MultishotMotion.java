package fi.dy.masa.minecraft.mods.multishot.motion;

import net.minecraft.client.entity.EntityClientPlayerMP;
import fi.dy.masa.minecraft.mods.multishot.config.MultishotConfigs;

public class MultishotMotion
{
	private MultishotConfigs multishotConfigs = null;
	private Point center = null;
	private Point[] path = null;

	public MultishotMotion(MultishotConfigs msCfg)
	{
		this.multishotConfigs = msCfg;
	}

	private class Point
	{
		private double posX;
		private double posZ;
		private double posY;

		public Point(double x, double z, double y)
		{
			this.posX = x;
			this.posZ = z;
			this.posY = y;
		}

		public double getX() { return this.posX; }
		public double getZ() { return this.posZ; }
		public double getY() { return this.posY; }
	}

	public void addPoint(double x, double z, double y)
	{
		int len = 0;
		Point[] tmp;
		if (this.path != null)
		{
			len = this.path.length;
		}
		tmp = new Point[len + 1];
		for(int i = 0; i < len; i++)
		{
			tmp[i] = this.path[i];
		}
		tmp[len] = new Point(x, z, y);
		this.path = tmp;
	}

	public void removePoint(int index)
	{
		int len = 0;
		Point[] tmp;
		if (this.path != null)
		{
			len = this.path.length;
		}
		if (index < 0 || index >= len)
		{
			System.out.println("MultishotMotion.removePoint(): Invalid index:" + index);
			return;
		}
		if (len == 1 && index == 0)
		{
			this.path = null;
			return;
		}
		tmp = new Point[len - 1];
		for(int i = 0, j = 0; i < len; i++)
		{
			if (i != index)
			{
				tmp[j++] = this.path[i];
			}
		}
		this.path = tmp;
	}

	public void addPointFromCurrentPos(EntityClientPlayerMP p)
	{
		this.addPoint(p.posX, p.posZ, p.posY);
	}

	public void setCenterPointFromCurrentPos(EntityClientPlayerMP p)
	{
		this.center = new Point(p.posX, p.posZ, p.posY);
	}

	private void movePlayerLinear(EntityClientPlayerMP p)
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
		//Vec3 pos = player.getPosition(1.0f);
		//player.setPositionAndRotation(pos.xCoord + mx, pos.yCoord + my, pos.zCoord + mz, player.rotationYaw + yaw, player.rotationPitch + pitch);
		p.moveEntity(mx, my, mz);
		p.setPositionAndRotation(p.posX, p.posY, p.posZ, p.rotationYaw + yaw, p.rotationPitch + pitch);
	}

	public void movePlayer(EntityClientPlayerMP p)
	{
		if (this.multishotConfigs.getMotionMode() == 0) // 0 = Linear
		{
			this.movePlayerLinear(p);
		}
	}
}
