package fi.dy.masa.minecraft.mods.multishot.motion;

import net.minecraft.client.entity.EntityClientPlayerMP;
import fi.dy.masa.minecraft.mods.multishot.Multishot;
import fi.dy.masa.minecraft.mods.multishot.libs.MsMathHelper;
import fi.dy.masa.minecraft.mods.multishot.reference.MsConstants;
import fi.dy.masa.minecraft.mods.multishot.state.MsClassReference;

public class MsMotion
{
	private MsPoint circleCenter = null;
	private MsPoint circleTarget = null;
	private MsPoint ellipseCenter = null;
	private MsPoint ellipseTarget = null;
	private MsPoint pathTargetLinear = null;
	private MsPoint pathTargetSmooth = null;
	private MsPoint[] pathLinear = null;
	private MsPoint[] pathSmooth = null;
	private double circleRadius = 0.0;
	private double circleStartAngle = 0.0;
	private double circleCurrentAngle = 0.0;
	private double circleAngularVelocity = 0.0;
	private double ellipseRadiusA = -1.0;
	private double ellipseRadiusB = -1.0;
	private MsPoint ellipsePointA = null;
	private MsPoint ellipsePointB = null;
	private boolean useTarget = false; // Do we lock the pitch angle to look directly at the center point?
	private int pathIndexClipboard = -1;
	public double targetYaw = 0.0;
	public double targetPitch = 0.0;
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

	public MsMotion()
	{
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

	private int getMotionMode()
	{
		return MsClassReference.getMsConfigs().getMotionMode();
	}

	public int addPathPoint(double x, double z, double y, float yaw, float pitch)
	{
		int mode = this.getMotionMode();
		int len = 0;
		MsPoint[] tmp = null;
		MsPoint[] src = null;

		if (mode == MsConstants.MOTION_MODE_PATH_LINEAR)
		{
			src = this.pathLinear;
		}
		else if (mode == MsConstants.MOTION_MODE_PATH_SMOOTH)
		{
			src = this.pathSmooth;
		}
		else
		{
			return -1;
		}

		if (src != null)
		{
			len = src.length;
		}

		tmp = new MsPoint[len + 1];
		for(int i = 0; i < len; i++)
		{
			tmp[i] = src[i];
		}
		tmp[len] = new MsPoint(x, z, y, yaw, pitch);

		if (mode == MsConstants.MOTION_MODE_PATH_LINEAR)
		{
			this.pathLinear = tmp;
		}
		else if (mode == MsConstants.MOTION_MODE_PATH_SMOOTH)
		{
			this.pathSmooth = tmp;
		}

		return len;
	}

	public void removePathPoint(int index)
	{
		int mode = this.getMotionMode();
		int len = 0;
		MsPoint[] tmp = null;
		MsPoint[] src = null;

		if (mode == MsConstants.MOTION_MODE_PATH_LINEAR)
		{
			src = this.pathLinear;
		}
		else if (mode == MsConstants.MOTION_MODE_PATH_SMOOTH)
		{
			src = this.pathSmooth;
		}
		if (src == null)
		{
			return;
		}
		len = src.length;

		if (index < 0 || index >= len)
		{
			MsClassReference.getGui().addMessage("Error: Could not remove point, invalid index: " + index);
			return;
		}
		if (len == 1 && index == 0)
		{
			MsClassReference.getGui().addMessage("Removed path point #" + index);
			if (mode == MsConstants.MOTION_MODE_PATH_LINEAR)
			{
				this.pathLinear = null;
			}
			else if (mode == MsConstants.MOTION_MODE_PATH_SMOOTH)
			{
				this.pathSmooth = null;
			}
			return;
		}

		tmp = new MsPoint[len - 1];
		for(int i = 0, j = 0; i < len; i++)
		{
			if (i != index)
			{
				tmp[j++] = src[i];
			}
			else
			{
				MsClassReference.getGui().addMessage("Removed path point #" + index);
			}
		}

		if (mode == MsConstants.MOTION_MODE_PATH_LINEAR)
		{
			this.pathLinear = tmp;
		}
		else if (mode == MsConstants.MOTION_MODE_PATH_SMOOTH)
		{
			this.pathSmooth = tmp;
		}
	}

	public void setUseTarget(boolean t)
	{
		this.useTarget = t;
	}

	public boolean getUseTarget()
	{
		return this.useTarget;
	}

	public void setCenterPointFromCurrentPos(EntityClientPlayerMP p)
	{
		if (p == null) {
			Multishot.logSevere("setCenterPointFromCurrentPos(): player was null");
			return;
		}

		int mode = this.getMotionMode();
		if (mode == MsConstants.MOTION_MODE_CIRCLE)
		{
			this.circleCenter = new MsPoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);
			MsClassReference.getGui().addMessage(String.format("Added circle center point at x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch));
		}
		else if (mode == MsConstants.MOTION_MODE_ELLIPSE)
		{
			this.ellipseCenter = new MsPoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);
			MsClassReference.getGui().addMessage(String.format("Added ellipse center point at x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch));
		}
	}

	public void setTargetPointFromCurrentPos(EntityClientPlayerMP p)
	{
		if (p == null) {
			Multishot.logSevere("setTargetPointFromCurrentPos(): player was null");
			return;
		}

		int mode = this.getMotionMode();
		if (mode == MsConstants.MOTION_MODE_CIRCLE)
		{
			this.circleTarget = new MsPoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);
			MsClassReference.getGui().addMessage(String.format("Added circle target point at x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch));
		}
		else if (mode == MsConstants.MOTION_MODE_ELLIPSE)
		{
			this.ellipseTarget = new MsPoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);
			MsClassReference.getGui().addMessage(String.format("Added ellipse target point at x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch));
		}
		else if (mode == MsConstants.MOTION_MODE_PATH_LINEAR)
		{
			this.pathTargetLinear = new MsPoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);
			MsClassReference.getGui().addMessage(String.format("Added Path (linear) target point at x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch));
		}
		else if (mode == MsConstants.MOTION_MODE_PATH_SMOOTH)
		{
			this.pathTargetSmooth = new MsPoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);
			MsClassReference.getGui().addMessage(String.format("Added Path (smooth) target point at x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch));
		}
	}

	public void addPathPointFromCurrentPos(EntityClientPlayerMP p)
	{
		if (p == null) {
			Multishot.logSevere("addPathPointFromCurrentPos(): player was null");
			return;
		}

		int mode = this.getMotionMode();
		int i;
		if (mode == MsConstants.MOTION_MODE_PATH_LINEAR || mode == MsConstants.MOTION_MODE_PATH_SMOOTH)
		{
			i = this.addPathPoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);
			MsClassReference.getGui().addMessage(String.format("Added point " + i + ": x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch));
		}
	}

	public void addPointFromCurrentPos(EntityClientPlayerMP p)
	{
		if (p == null) {
			Multishot.logSevere("addPointFromCurrentPos(): player was null");
			return;
		}

		int mode = this.getMotionMode();
		if (mode == MsConstants.MOTION_MODE_ELLIPSE)
		{
			this.ellipsePointA = new MsPoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);
			MsClassReference.getGui().addMessage(String.format("Added ellipse longer semi-axis point at x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch));
		}
		else if (mode == MsConstants.MOTION_MODE_PATH_LINEAR || mode == MsConstants.MOTION_MODE_PATH_SMOOTH)
		{
			this.addPathPointFromCurrentPos(p);
		}
	}

	public void removeCenterPoint()
	{
		int mode = this.getMotionMode();
		if (mode == MsConstants.MOTION_MODE_CIRCLE)
		{
			this.circleCenter = null;
			MsClassReference.getGui().addMessage("Removed circle center point");
		}
		else if (mode == MsConstants.MOTION_MODE_ELLIPSE)
		{
			this.ellipseCenter = null;
			MsClassReference.getGui().addMessage("Removed ellipse center point");
		}
	}

	public void removeTargetPoint()
	{
		int mode = this.getMotionMode();
		if (mode == MsConstants.MOTION_MODE_CIRCLE)
		{
			if (this.circleTarget != null)
			{
				this.circleTarget = null;
				MsClassReference.getGui().addMessage("Removed circle target point");
			}
		}
		else if (mode == MsConstants.MOTION_MODE_ELLIPSE)
		{
			if (this.ellipseTarget != null)
			{
				this.ellipseTarget = null;
				MsClassReference.getGui().addMessage("Removed ellipse target point");
			}
		}
		else if (mode == MsConstants.MOTION_MODE_PATH_LINEAR)
		{
			if (this.pathTargetLinear != null)
			{
				this.pathTargetLinear = null;
				MsClassReference.getGui().addMessage("Removed Path (linear) target point");
			}
		}
		else if (mode == MsConstants.MOTION_MODE_PATH_SMOOTH)
		{
			if (this.pathTargetSmooth != null)
			{
				this.pathTargetSmooth = null;
				MsClassReference.getGui().addMessage("Removed Path (smooth) target point");
			}
		}
	}

	public int getNearestPathPointIndex(double x, double z, double y)
	{
		int mode = this.getMotionMode();
		int index = 0;
		double mindist = 120000000.0;
		double dist;
		MsPoint[] path = null;

		if (mode == MsConstants.MOTION_MODE_PATH_LINEAR)
		{
			path = this.pathLinear;
		}
		else if (mode == MsConstants.MOTION_MODE_PATH_SMOOTH)
		{
			path = this.pathSmooth;
		}
		if (path == null || path.length == 0)
		{
			return -1;
		}

		int len = path.length;
		for (int i = 0; i < len; i++)
		{
			dist = MsMathHelper.distance3D(path[i].getX(), path[i].getZ(), path[i].getY(), x, z, y);
			if (dist < mindist)
			{
				mindist = dist;
				index = i;
			}
		}
		return index;
	}

	public void removeNearestPathPoint(EntityClientPlayerMP p)
	{
		if (p == null) {
			Multishot.logSevere("removeNearestPathPoint(): player was null");
			return;
		}

		int mode = this.getMotionMode();
		if (mode == MsConstants.MOTION_MODE_PATH_LINEAR || mode == MsConstants.MOTION_MODE_PATH_SMOOTH)
		{
			this.removePathPoint(this.getNearestPathPointIndex(p.posX, p.posZ, p.posY));
		}
	}

	public void storeNearestPathPointIndex(EntityClientPlayerMP p)
	{
		if (p == null) {
			Multishot.logSevere("storeNearestPathPointIndex(): player was null");
			return;
		}

		this.pathIndexClipboard = this.getNearestPathPointIndex(p.posX, p.posZ, p.posY);
		if (this.pathIndexClipboard >= 0)
		{
			MsClassReference.getGui().addMessage(String.format("Stored point #%d", this.pathIndexClipboard));
		}
		else
		{
			MsClassReference.getGui().addMessage(String.format("Error: No path points exist!"));
		}
	}

	public void replaceStoredPathPoint(EntityClientPlayerMP p)
	{
		if (p == null) {
			Multishot.logSevere("replaceStoredPathPoint(): player was null");
			return;
		}

		int mode = this.getMotionMode();

		if (this.pathIndexClipboard >= 0)
		{
			MsPoint[] path = null;

			if (mode == MsConstants.MOTION_MODE_PATH_LINEAR)
			{
				path = this.pathLinear;
			}
			else if (mode == MsConstants.MOTION_MODE_PATH_SMOOTH)
			{
				path = this.pathSmooth;
			}

			if (path != null && path.length > this.pathIndexClipboard)
			{
				path[this.pathIndexClipboard] = new MsPoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);
				MsClassReference.getGui().addMessage(String.format("Moved point #%d to: x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f",
						this.pathIndexClipboard, p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch));
				//this.pathIndexClipboard = -1;
			}
			else
			{
				MsClassReference.getGui().addMessage(String.format("Error: Can't move point, invalid index!"));
			}
		}
		else
		{
			MsClassReference.getGui().addMessage(String.format("Error: Can't move point, no point selected!"));
		}
	}

	public void removeAllPoints()
	{
		int mode = this.getMotionMode();
		if (mode == MsConstants.MOTION_MODE_CIRCLE || mode == MsConstants.MOTION_MODE_ELLIPSE)
		{
			this.removeCenterPoint();
		}
		else if (mode == MsConstants.MOTION_MODE_PATH_LINEAR)
		{
			this.pathLinear = null;
			MsClassReference.getGui().addMessage("All path points removed");
		}
		else if (mode == MsConstants.MOTION_MODE_PATH_SMOOTH)
		{
			this.pathSmooth = null;
			MsClassReference.getGui().addMessage("All path points removed");
		}
		this.removeTargetPoint();
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
		int mode = this.getMotionMode();
		if (mode == MsConstants.MOTION_MODE_PATH_LINEAR)
		{
			return this.pathTargetLinear;
		}
		else if (mode == MsConstants.MOTION_MODE_PATH_SMOOTH)
		{
			return this.pathTargetSmooth;
		}
		return null;
	}

	public MsPoint[] getPath()
	{
		int mode = this.getMotionMode();
		if (mode == MsConstants.MOTION_MODE_PATH_LINEAR)
		{
			return this.pathLinear;
		}
		else if (mode == MsConstants.MOTION_MODE_PATH_SMOOTH)
		{
			return this.pathSmooth;
		}
		return null;
	}

	public void linearSegmentInit(EntityClientPlayerMP p, MsPoint tgt)
	{
		if (p == null) {
			Multishot.logSevere("linearSegmentInit(): player was null");
			return;
		}
		if (tgt == null) {
			Multishot.logSevere("linearSegmentInit(): target was null");
			return;
		}

		this.segmentStart = new MsPoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);
		this.segmentEnd = tgt;
		this.segmentProgress = 0.0f; // 0..1
		this.segmentLength = MsMathHelper.distance3D(tgt.getX(), tgt.getZ(), tgt.getY(), p.posX, p.posZ, p.posY);
		this.segmentAngleH = Math.PI / 2.0d;
		this.segmentAngleV = Math.PI / 2.0d;
		double zDist = tgt.getZ() - p.posZ;
		if (zDist != 0.0d)
		{
			this.segmentAngleH = Math.atan2(tgt.getX() - p.posX, zDist);
		}
		double hDist = MsMathHelper.distance2D(tgt.getZ(), p.posZ, tgt.getX(), p.posX);
		if (hDist != 0.0d)
		{
			this.segmentAngleV = Math.atan2(tgt.getY() - p.posY, hDist);
		}
		this.segmentYawChange = tgt.getYaw() - (p.rotationYaw % 360.0f);
		if (this.segmentYawChange > 180.0f) { this.segmentYawChange -= 360.0f; }
		else if (this.segmentYawChange < -180.0f) { this.segmentYawChange += 360.0f; }
		this.segmentPitchChange = tgt.getPitch() - p.rotationPitch;
		// FIXME debug
		System.out.printf("tgt.getYaw(): %.3f p.rotationYaw: %.3f\n", tgt.getYaw(), p.rotationYaw);
		System.out.printf("tgt.getPitch(): %.3f p.rotationPitch: %.3f\n", tgt.getPitch(), p.rotationPitch);
		System.out.printf("segmentYawChange: %.3f segmentPitchChange: %.3f\n", this.segmentYawChange, this.segmentPitchChange);
	}

	public boolean linearSegmentMove(EntityClientPlayerMP p, int speed)
	{
		if (p == null) {
			Multishot.logSevere("linearSegmentMove(): player was null");
			return false;
		}

		double movement = (double)speed / 20000.0d; // Speed is in 1/1000 m/s, TPS is 20
		if (((this.segmentProgress * this.segmentLength) + movement) > this.segmentLength)
		{
			p.setPositionAndRotation(this.segmentEnd.getX(), this.segmentEnd.getY(), this.segmentEnd.getZ(), p.rotationYaw, p.rotationPitch);
			this.reOrientPlayerToAngle(p, (float)this.segmentEnd.getYaw(), (float)this.segmentEnd.getPitch());
			return true; // done for this segment
		}
		else
		{
			this.segmentProgress += (movement / this.segmentLength);
			double dist = this.segmentProgress * this.segmentLength;
			double x = (Math.sin(this.segmentAngleH) * dist * Math.cos(this.segmentAngleV)) + this.segmentStart.getX();
			double z = (Math.cos(this.segmentAngleH) * dist * Math.cos(this.segmentAngleV)) + this.segmentStart.getZ();
			double y = (dist * Math.sin(this.segmentAngleV)) + this.segmentStart.getY();
			p.setPositionAndRotation(x, y, z, p.rotationYaw, p.rotationPitch);
			float yaw = this.segmentStart.getYaw() + (this.segmentProgress * this.segmentYawChange);
			float pitch = this.segmentStart.getPitch() + (this.segmentProgress * this.segmentPitchChange);
			this.reOrientPlayerToAngle(p, yaw, pitch);
		}
		return false;
	}

	// This method re-orients the player to the given angle, by setting the per-tick angle increments,
	// which are then interpolated in the rendering phase to get a smooth rotation.
	private void reOrientPlayerToAngle(EntityClientPlayerMP p, float yaw, float pitch)
	{
		if (p == null) {
			Multishot.logSevere("reOrientPlayerToAngle(): player was null");
			return;
		}

		float yawInc = (yaw - p.rotationYaw ) % 360.0f;

		// Translate the increment to between -180..180 degrees
		if (yawInc > 180.0f) { yawInc -= 360.0f; }
		else if (yawInc < -180.0f) { yawInc += 360.0f; }

		// Store the initial values and the increments, which are used in the render event handler to interpolate the angle
		this.prevYaw = p.rotationYaw;
		this.prevPitch = p.rotationPitch;
		this.yawIncrement = yawInc;
		this.pitchIncrement = pitch - p.rotationPitch;
	}

	// This method re-orients the player to face the given point, by setting the per-tick angle increments,
	// which are then interpolated in the rendering phase to get a smooth rotation.
	private void reOrientPlayerToTargetPoint(EntityClientPlayerMP p, double tx, double tz, double ty)
	{
		if (p == null) {
			Multishot.logSevere("reOrientPlayerToTargetPoint(p, x, z, y): player was null");
			return;
		}

		double px = p.posX;
		double py = p.posY;
		double pz = p.posZ;
		// The angle in which the player sees the target point, in relation to the +z-axis
		this.targetYaw = Math.atan2(px - tx, tz - pz) * 180.0f / (float)Math.PI;
		this.targetPitch = (-Math.atan2(ty - py, MsMathHelper.distance2D(tx, tz, px, pz)) * 180.0D / Math.PI);
		this.reOrientPlayerToAngle(p, (float)this.targetYaw, (float)this.targetPitch);
	}

	private void reOrientPlayerToTargetPoint(EntityClientPlayerMP p, MsPoint tgt)
	{
		if (p == null) {
			Multishot.logSevere("reOrientPlayerToTargetPoint(p, tgt): player was null");
			return;
		}
		if (tgt == null) {
			Multishot.logSevere("reOrientPlayerToTargetPoint(p, tgt): target was null");
			return;
		}

		this.reOrientPlayerToTargetPoint(p, tgt.getX(), tgt.getZ(), tgt.getY());
	}

	public boolean startMotion(EntityClientPlayerMP p)
	{
		if (p == null) {
			Multishot.logSevere("startMotion(): Error: player was null");
			return false;
		}

		int mode = this.getMotionMode();
		this.prevYaw = p.rotationYaw;
		this.prevPitch = p.rotationPitch;

		if (mode == MsConstants.MOTION_MODE_LINEAR) // Linear
		{
		}
		else if (mode == MsConstants.MOTION_MODE_CIRCLE)
		{
			if (this.circleCenter == null)
			{
				MsClassReference.getGui().addMessage("startMotion(): Error: Circle center point not set!");
				return false;
			}
			double px = p.posX;
			double pz = p.posZ;
			double cx = this.circleCenter.getX();
			double cz = this.circleCenter.getZ();
			this.circleRadius = MsMathHelper.distance2D(cx, cz, px, pz);
			this.circleStartAngle = Math.atan2(cx - px, pz - cz); // The angle in which the center point sees the player, in relation to +z-axis
			this.circleCurrentAngle = this.circleStartAngle;
			this.circleAngularVelocity = ((double)MsClassReference.getMsConfigs().getMotionSpeed() / 20000.0) / this.circleRadius;
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
		else if (mode == MsConstants.MOTION_MODE_ELLIPSE)
		{
			if (this.ellipseCenter == null)
			{
				MsClassReference.getGui().addMessage("startMotion(): Error: Ellipse center point not set!");
				return false;
			}
			if (this.ellipseRadiusA <= 0.0 || this.ellipseRadiusB <= 0.0)
			{
				MsClassReference.getGui().addMessage("startMotion(): Error: Ellipse radiuses not set!");
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
		else if (mode == MsConstants.MOTION_MODE_PATH_LINEAR)
		{
			MsClassReference.getGui().addMessage("startMotion(): Error: Path (linear) not implemented yet!");
			return false;
		}
		else if (mode == MsConstants.MOTION_MODE_PATH_SMOOTH)
		{
			MsClassReference.getGui().addMessage("startMotion(): Error: Path (smooth) not implemented yet!");
			return false;
		}

		return true;
	}

	private void movePlayerLinear(EntityClientPlayerMP p)
	{
		if (p == null) {
			Multishot.logSevere("movePlayerLinear(): player was null");
			return;
		}

		double mx, my, mz;
		float yaw, pitch;
		mx = MsClassReference.getMsConfigs().getMotionX();
		mz = MsClassReference.getMsConfigs().getMotionZ();
		my = MsClassReference.getMsConfigs().getMotionY();
		yaw = MsClassReference.getMsConfigs().getRotationYaw();
		pitch = MsClassReference.getMsConfigs().getRotationPitch();
		//player.setPositionAndUpdate(pos.xCoord + x, pos.yCoord + y, pos.zCoord + z); // Does strange things...
		//player.setVelocity(mx, my, mz); // Doesn't work for values < 0.005
		//Vec3 pos = player.getPosition(1.0f);
		//player.setPositionAndRotation(pos.xCoord + mx, pos.yCoord + my, pos.zCoord + mz, player.rotationYaw + yaw, player.rotationPitch + pitch);
		p.moveEntity(mx, my, mz);
		//p.setPositionAndRotation(p.posX + mx, p.posY + my, p.posZ + mz, p.rotationYaw, p.rotationPitch);
		this.reOrientPlayerToAngle(p, p.rotationYaw + yaw, p.rotationPitch + pitch);
	}

	private void movePlayerCircular(EntityClientPlayerMP p)
	{
		if (p == null) {
			Multishot.logSevere("movePlayerCircular(): player was null");
			return;
		}

		this.circleCurrentAngle += this.circleAngularVelocity;
		double x = this.circleCenter.getX() - Math.sin(this.circleCurrentAngle) * this.circleRadius;
		double z = this.circleCenter.getZ() + Math.cos(this.circleCurrentAngle) * this.circleRadius;
		x = (x - p.posX);
		z = (z - p.posZ);
		p.moveEntity(x, 0.0, z);
		//p.setPositionAndRotation(x, p.posY, z, p.rotationYaw, p.rotationPitch);

		// If we have a target point set, re-orient the player to look at the target point
		if (this.getUseTarget() == true)
		{
			this.reOrientPlayerToTargetPoint(p, this.circleTarget);
		}
	}

	public void movePlayer(EntityClientPlayerMP p)
	{
		int mode = this.getMotionMode();

		if (mode == MsConstants.MOTION_MODE_LINEAR)
		{
			this.movePlayerLinear(p);
		}
		else if (mode == MsConstants.MOTION_MODE_CIRCLE)
		{
			this.movePlayerCircular(p);
		}
		else if (mode == MsConstants.MOTION_MODE_ELLIPSE)
		{
		}
		else if (mode == MsConstants.MOTION_MODE_PATH_LINEAR)
		{
		}
		else if (mode == MsConstants.MOTION_MODE_PATH_SMOOTH)
		{
		}
	}
}
