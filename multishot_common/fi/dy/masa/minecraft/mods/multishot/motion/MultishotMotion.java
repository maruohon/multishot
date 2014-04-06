package fi.dy.masa.minecraft.mods.multishot.motion;

import net.minecraft.client.entity.EntityClientPlayerMP;
import fi.dy.masa.minecraft.mods.multishot.config.MultishotConfigs;
import fi.dy.masa.minecraft.mods.multishot.gui.MultishotGui;
import fi.dy.masa.minecraft.mods.multishot.libs.MsMathHelper;

public class MultishotMotion
{
	private MultishotConfigs multishotConfigs = null;
	private MultishotGui multishotGui = null;
	private MsPoint circleCenter = null;
	private MsPoint circleTarget = null;
	private MsPoint ellipseCenter = null;
	private MsPoint ellipseTarget = null;
	private MsPoint pathTarget = null;
	private MsPoint[] path = null;
	private double circleRadius = 0.0;
	private double circleStartAngle = 0.0;
	private double circleCurrentAngle = 0.0;
	private double circleAngularVelocity = 0.0;
	private double ellipseRadiusA = 0.0;
	private double ellipseRadiusB = 0.0;
	private double ellipseStartAngle = 0.0;
	private double ellipseCurrentAngle = 0.0;
	private boolean useTarget = false; // Do we lock the pitch angle to look directly at the center point?
	private int pathIndexClipboard = -1;
	public float yawIncrement = 0.0f;
	public float pitchIncrement = 0.0f;
	public float prevYaw = 0.0f;
	public float prevPitch = 0.0f;
	private MsPoint segmentStart = null;
	private MsPoint segmentEnd = null;
	private double segmentLength = 0.0;
	private double segmentAngleH = 0.0;
	private double segmentAngleV = 0.0;
	private float segmentProgress = 0.0f; // 0..1
	private float segmentYawChange = 0.0f;
	private float segmentPitchChange = 0.0f;

	public MultishotMotion(MultishotConfigs msCfg, MultishotGui msGui)
	{
		this.multishotConfigs = msCfg;
		this.multishotGui = msGui;
	}

	public class MsPoint
	{
		private double posX;
		private double posZ;
		private double posY;
		private float yaw;
		private float pitch;

		public MsPoint(double x, double z, double y, float yaw, float pitch)
		{
			this.posX = x;
			this.posZ = z;
			this.posY = y;
			this.yaw = yaw;
			this.pitch = pitch;
		}

		public double getX() { return this.posX; }
		public double getZ() { return this.posZ; }
		public double getY() { return this.posY; }
		public float getYaw() { return this.yaw; }
		public float getPitch() { return this.pitch; }
	}

	public int addPathPoint(double x, double z, double y, float yaw, float pitch)
	{
		int len = 0;
		MsPoint[] tmp;
		if (this.path != null)
		{
			len = this.path.length;
		}
		tmp = new MsPoint[len + 1];
		for(int i = 0; i < len; i++)
		{
			tmp[i] = this.path[i];
		}
		tmp[len] = new MsPoint(x, z, y, yaw, pitch);
		this.path = tmp;
		return len;
	}

	public void removePathPoint(int index)
	{
		int len = 0;
		MsPoint[] tmp;
		if (this.path == null)
		{
			return;
		}
		len = this.path.length;
		if (index < 0 || index >= len)
		{
			this.multishotGui.addMessage("MultishotMotion.removePoint(): Invalid index:" + index);
			return;
		}
		if (len == 1 && index == 0)
		{
			this.path = null;
			return;
		}
		tmp = new MsPoint[len - 1];
		for(int i = 0, j = 0; i < len; i++)
		{
			if (i != index)
			{
				tmp[j++] = this.path[i];
			}
			else
			{
				this.multishotGui.addMessage("Removed path point #" + index);
			}
		}
		this.path = tmp;
	}

	public void setUseTarget(boolean t)
	{
		this.useTarget = t;
	}

	public boolean getUseTarget()
	{
		return this.useTarget;
	}

	public void setCenterPointFromCurrentPos(EntityClientPlayerMP p, int mode)
	{
		// mode: 0 = Linear, 1 = Circular, 2 = Elliptical, 3 = Path
		if (mode == 1)
		{
			this.circleCenter = new MsPoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);
			this.multishotGui.addMessage(String.format("Added circle center point at x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch));
		}
		else if (mode == 2)
		{
			this.ellipseCenter = new MsPoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);
			this.multishotGui.addMessage(String.format("Added ellipse center point at x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch));
		}
	}

	public void setTargetPointFromCurrentPos(EntityClientPlayerMP p, int mode)
	{
		// mode: 0 = Linear, 1 = Circular, 2 = Elliptical, 3 = Path
		if (mode == 1)
		{
			this.circleTarget = new MsPoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);
			this.multishotGui.addMessage(String.format("Added circle target point at x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch));
		}
		else if (mode == 2)
		{
			this.ellipseTarget = new MsPoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);
			this.multishotGui.addMessage(String.format("Added ellipse target point at x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch));
		}
		else if (mode == 3)
		{
			this.pathTarget = new MsPoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);
			this.multishotGui.addMessage(String.format("Added path target point at x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch));
		}
	}

	public void addPathPointFromCurrentPos(EntityClientPlayerMP p)
	{
		int i;
		i = this.addPathPoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);
		this.multishotGui.addMessage(String.format("Added point " + i + ": x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch));
	}

	public void addPointFromCurrentPos(EntityClientPlayerMP p, int mode)
	{
		// mode: 0 = Linear, 1 = Circular, 2 = Elliptical, 3 = Path
		if (mode == 1 || mode == 2)
		{
			this.setCenterPointFromCurrentPos(p, mode);
		}
		else if (mode == 3)
		{
			this.addPathPointFromCurrentPos(p);
		}
	}

	public void removeCenterPoint(int mode)
	{
		// mode: 0 = Linear, 1 = Circular, 2 = Elliptical, 3 = Path
		if (mode == 1)
		{
			this.circleCenter = null;
			this.multishotGui.addMessage("Removed circle center point");
		}
		else if (mode == 2)
		{
			this.ellipseCenter = null;
			this.multishotGui.addMessage("Removed ellipse center point");
		}
	}

	public void removeTargetPoint(int mode)
	{
		// mode: 0 = Linear, 1 = Circular, 2 = Elliptical, 3 = Path
		if (mode == 1)
		{
			this.circleTarget = null;
			this.multishotGui.addMessage("Removed circle target point");
		}
		else if (mode == 2)
		{
			this.ellipseTarget = null;
			this.multishotGui.addMessage("Removed ellipse target point");
		}
		else if (mode == 3)
		{
			this.pathTarget = null;
			this.multishotGui.addMessage("Removed path target point");
		}
	}

	public int getNearestPathPointIndex(EntityClientPlayerMP p)
	{
		int index = 0;
		double mindist = 60000000.0;
		double dist;
		if (this.path == null || this.path.length == 0)
		{
			return -1;
		}
		for (int i = 0; i < this.path.length; i++)
		{
			dist = MsMathHelper.distance2D(this.path[i].getX(), this.path[i].getZ(), p.posX, p.posZ);
			if (dist < mindist)
			{
				mindist = dist;
				index = i;
			}
		}
		return index;
	}

	public void removeNearestPoint(EntityClientPlayerMP p)
	{
		this.removePathPoint(this.getNearestPathPointIndex(p));
	}

	public void storeNearestPathPointIndex(EntityClientPlayerMP p)
	{
		this.pathIndexClipboard = this.getNearestPathPointIndex(p);
		this.multishotGui.addMessage(String.format("Stored point index #%d", this.pathIndexClipboard));
	}

	public void replaceStoredPathPoint(EntityClientPlayerMP p)
	{
		if (this.pathIndexClipboard >= 0)
		{
			if (this.path != null && this.path.length > this.pathIndexClipboard)
			{
				this.path[this.pathIndexClipboard] = new MsPoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);
				this.multishotGui.addMessage(String.format("Moved point #%d to: x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f",
						this.pathIndexClipboard, p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch));
				//this.pathIndexClipboard = -1;
			}
		}
	}

	public void removeAllPoints(int mode)
	{
		// mode: 0 = Linear, 1 = Circular, 2 = Elliptical, 3 = Path
		if (mode == 1)
		{
			this.removeCenterPoint(mode);
		}
		else if (mode == 2)
		{
			this.removeCenterPoint(mode);
		}
		else if (mode == 3)
		{
			this.path = null;
			this.multishotGui.addMessage("All points removed");
		}
		this.removeTargetPoint(mode);
	}

	public MsPoint getCircleCenter()
	{
		return this.circleCenter;
	}

	public MsPoint getCircleTarget()
	{
		return this.circleTarget;
	}

	public MsPoint getEllipseCenter()
	{
		return this.ellipseCenter;
	}

	public MsPoint getEllipseTarget()
	{
		return this.ellipseTarget;
	}

	public MsPoint getPathTarget()
	{
		return this.pathTarget;
	}

	public MsPoint[] getPath()
	{
		return this.path;
	}

	public void linearSegmentInit(EntityClientPlayerMP p, MsPoint tgt)
	{
		this.segmentStart = new MsPoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);
		this.segmentEnd = tgt;
		this.segmentProgress = 0.0f; // 0..1
		this.segmentLength = MsMathHelper.distance3D(tgt.getX(), tgt.getZ(), tgt.getY(), p.posX, p.posZ, p.posY);
		this.segmentAngleH = Math.atan2(tgt.getZ() - p.posZ, tgt.getX() - p.posX);
		this.segmentAngleV = Math.atan2(tgt.getY() - p.posY, MsMathHelper.distance2D(tgt.getZ(), p.posZ, tgt.getX(), p.posX));
		this.segmentYawChange = tgt.getYaw() + 90.0f - (p.rotationYaw % 360.0f);
		if (this.segmentYawChange > 180.0) { this.segmentYawChange -= 360.0; }
		else if (this.segmentYawChange < -180.0) { this.segmentYawChange += 360.0; }
		this.segmentPitchChange = tgt.getPitch() - p.rotationPitch;
	}

	public int linearSegmentMove(EntityClientPlayerMP p, int speed)
	{
		double movement = (double)speed / 20000.0; // Speed is in 1/1000 m/s, TPS is 20
		if (((this.segmentProgress * this.segmentLength) + movement) > this.segmentLength)
		{
			p.setPositionAndRotation(this.segmentEnd.getX(), this.segmentEnd.getY(), this.segmentEnd.getZ(), p.rotationYaw, p.rotationPitch);
			this.reOrientPlayerToAngle(p, (float)this.segmentEnd.getYaw(), (float)this.segmentEnd.getPitch());
			return 1; // done for this segment
		}
		else
		{
			this.segmentProgress += (movement / this.segmentLength);
			double dist = this.segmentProgress * this.segmentLength;
			double x = (Math.cos(this.segmentAngleH) * dist * Math.cos(this.segmentAngleV)) + this.segmentStart.getX();
			double z = (Math.sin(this.segmentAngleH) * dist * Math.cos(this.segmentAngleV)) + this.segmentStart.getZ();
			double y = (dist * Math.sin(this.segmentAngleV)) + this.segmentStart.getY();
			p.setPositionAndRotation(x, y, z, p.rotationYaw, p.rotationPitch);
			float yaw = this.segmentStart.getYaw() + this.segmentProgress * this.segmentYawChange;
			float pitch = this.segmentStart.getPitch() + this.segmentProgress * this.segmentPitchChange;
			this.reOrientPlayerToAngle(p, yaw, pitch);
		}
		return 0;
	}

	// This method re-orients the player to the given angle, by setting the per-tick angle increments,
	// which are then interpolated in the rendering phase to get a smooth rotation.
	private void reOrientPlayerToAngle(EntityClientPlayerMP p, float yaw, float pitch)
	{
		float yawInc = (yaw + 90.0f - (p.rotationYaw % 360.0f)) % 360.0f;

		if (yawInc > 180.0f) { yawInc -= 360.0f; }
		else if (yawInc < -180.0f) { yawInc += 360.0f; }

		this.prevYaw = p.rotationYaw;
		this.prevPitch = p.rotationPitch;
		this.yawIncrement = yawInc;
		this.pitchIncrement = pitch - p.rotationPitch;
	}

	private void reOrientPlayerToTargetPoint(EntityClientPlayerMP p, MsPoint tgt)
	{
		this.reOrientPlayerToTargetPoint(p, tgt.getX(), tgt.getZ(), tgt.getY());
	}

	// This method re-orients the player to face the given point, by setting the per-tick angle increments,
	// which are then interpolated in the rendering phase to get a smooth rotation.
	private void reOrientPlayerToTargetPoint(EntityClientPlayerMP p, double tx, double tz, double ty)
	{
		double px = p.posX;
		double py = p.posY;
		double pz = p.posZ;
		float yawInc = ((float)Math.atan2(pz - tz, px - tx) * 180.0f / (float)Math.PI) + 90.0f - (p.rotationYaw % 360.0f);

		if (yawInc > 180.0f) { yawInc -= 360.0f; }
		else if (yawInc < -180.0f) { yawInc += 360.0f; }

		this.prevYaw = p.prevRotationYaw;
		this.prevPitch = p.prevRotationPitch;
		this.yawIncrement = yawInc;
		this.pitchIncrement = (-(float)Math.atan2(ty - py, MsMathHelper.distance2D(tx, tz, px, pz)) * 180.0f / (float)Math.PI) - p.rotationPitch;
	}

	public boolean startMotion(EntityClientPlayerMP p, int mode)
	{
		this.prevYaw = p.rotationYaw;
		this.prevPitch = p.rotationPitch;

		// mode: 0 = Linear, 1 = Circular, 2 = Elliptical, 3 = Path
		if (mode == 0) // Linear
		{
		}
		else if (mode == 1) // Circular
		{
			if (this.circleCenter == null)
			{
				this.multishotGui.addMessage("startMotion(): Error: Circle center point not set!");
				return false;
			}
			double px = p.posX;
			double pz = p.posZ;
			double cx = this.circleCenter.getX();
			double cz = this.circleCenter.getZ();
			this.circleRadius = MsMathHelper.distance2D(cx, cz, px, pz);
			this.circleStartAngle = Math.atan2(cz - pz, cx - px);
			this.circleCurrentAngle = this.circleStartAngle;
			this.circleAngularVelocity = ((double)this.multishotConfigs.getMotionSpeed() / 20000.0) / this.circleRadius;
			//System.out.printf("circleRadius: %f\n", this.circleRadius); // FIXME debug
			//System.out.printf("circleStartAngle: %f\n", this.circleStartAngle); // FIXME debug
			//System.out.printf("circleCurrentAngle: %f\n", this.circleCurrentAngle); // FIXME debug
			//System.out.printf("circleAngularVelocity: %f\n", this.circleAngularVelocity); // FIXME debug
			if (this.circleTarget != null)
			{
				this.setUseTarget(true);
				this.reOrientPlayerToTargetPoint(p, this.circleTarget);
			}
			else
			{
				this.setUseTarget(false);
			}
		}
		else if (mode == 2) // Elliptical
		{
			if (this.ellipseCenter == null)
			{
				this.multishotGui.addMessage("startMotion(): Error: Ellipse center point not set!");
				return false;
			}
			if (this.ellipseRadiusA <= 0.0 || this.ellipseRadiusB <= 0.0)
			{
				this.multishotGui.addMessage("startMotion(): Error: Ellipse radiuses not set!");
				return false;
			}
			if (this.ellipseTarget != null)
			{
				this.setUseTarget(true);
			}
			else
			{
				this.setUseTarget(false);
			}
		}
		else if (mode == 3) // Path
		{
			this.multishotGui.addMessage("startMotion(): Error: Path mode not implemented yet!");
			return false;
		}

		return true;
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
		//Vec3 pos = player.getPosition(1.0f);
		//player.setPositionAndRotation(pos.xCoord + mx, pos.yCoord + my, pos.zCoord + mz, player.rotationYaw + yaw, player.rotationPitch + pitch);
		p.moveEntity(mx, my, mz);
		p.setPositionAndRotation(p.posX, p.posY, p.posZ, p.rotationYaw, p.rotationPitch);
		this.reOrientPlayerToAngle(p, p.rotationYaw + yaw - 90.0f, p.rotationPitch + pitch);
	}

	private void movePlayerCircular(EntityClientPlayerMP p)
	{
		this.circleCurrentAngle += this.circleAngularVelocity;
		double x = this.circleCenter.getX() - Math.cos(this.circleCurrentAngle) * this.circleRadius;
		double z = this.circleCenter.getZ() - Math.sin(this.circleCurrentAngle) * this.circleRadius;
		p.setPositionAndRotation(x, p.posY, z, p.rotationYaw, p.rotationPitch);

		// If we have a target point set, re-orient the player to look at the target point
		if (this.getUseTarget() == true)
		{
			this.reOrientPlayerToTargetPoint(p, this.circleTarget);
		}
	}

	public void movePlayer(EntityClientPlayerMP p, int mode)
	{
		// mode: 0 = Linear, 1 = Circular, 2 = Elliptical, 3 = Path
		if (mode == 0) // Linear
		{
			this.movePlayerLinear(p);
		}
		else if (mode == 1) // Circular
		{
			this.movePlayerCircular(p);
		}
		else if (mode == 2) // Elliptical
		{
		}
		else if (mode == 3) // Path
		{
		}
	}
}
