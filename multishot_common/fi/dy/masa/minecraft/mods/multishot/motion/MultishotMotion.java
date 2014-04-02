package fi.dy.masa.minecraft.mods.multishot.motion;

import net.minecraft.client.entity.EntityClientPlayerMP;
import fi.dy.masa.minecraft.mods.multishot.config.MultishotConfigs;
import fi.dy.masa.minecraft.mods.multishot.gui.MultishotGui;

public class MultishotMotion
{
	private MultishotConfigs multishotConfigs = null;
	private MultishotGui multishotGui = null;
	private MsPoint circleCenter = null;
	private MsPoint circleTarget = null;
	private MsPoint ellipseCenter = null;
	private MsPoint ellipseTarget = null;
	private MsPoint[] path = null;
	private double circleRadius = 0.0;
	private double ellipseRadiusA = 0.0;
	private double ellipseRadiusB = 0.0;
	private boolean useTarget = false; // Do we lock the pitch angle to look directly at the center point?

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
		private double yaw;
		private double pitch;

		public MsPoint(double x, double z, double y, double yaw, double pitch)
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
		public double getYaw() { return this.yaw; }
		public double getPitch() { return this.pitch; }
	}

	public int addPathPoint(double x, double z, double y, double yaw, double pitch)
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
		if (mode == 1) {
			this.circleCenter = new MsPoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);
			this.multishotGui.addMessage(String.format("Added circle center point at x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch));
		}
		else if (mode == 2) {
			this.ellipseCenter = new MsPoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);
			this.multishotGui.addMessage(String.format("Added ellipse center point at x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch));
		}
	}

	public void setTargetPointFromCurrentPos(EntityClientPlayerMP p, int mode)
	{
		// mode: 0 = Linear, 1 = Circular, 2 = Elliptical, 3 = Path
		if (mode == 1) {
			this.circleTarget = new MsPoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);
			this.multishotGui.addMessage(String.format("Added circle target point at x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch));
		}
		else if (mode == 2) {
			this.ellipseTarget = new MsPoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);
			this.multishotGui.addMessage(String.format("Added ellipse target point at x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch));
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
		if (mode == 1 || mode == 2) {
			this.setCenterPointFromCurrentPos(p, mode);
		}
		else if (mode == 3) {
			this.addPathPointFromCurrentPos(p);
		}
	}

	public void removeCenterPoint(int mode)
	{
		// mode: 0 = Linear, 1 = Circular, 2 = Elliptical, 3 = Path
		if (mode == 1) {
			this.circleCenter = null;
			this.multishotGui.addMessage("Removed circle center point");
		}
		else if (mode == 2) {
			this.ellipseCenter = null;
			this.multishotGui.addMessage("Removed ellipse center point");
		}
	}

	public void removeTargetPoint(int mode)
	{
		// mode: 0 = Linear, 1 = Circular, 2 = Elliptical, 3 = Path
		if (mode == 1) {
			this.circleTarget = null;
			this.multishotGui.addMessage("Removed circle target point");
		}
		else if (mode == 2) {
			this.ellipseTarget = null;
			this.multishotGui.addMessage("Removed ellipse target point");
		}
	}

	public void removeNearestPoint(EntityClientPlayerMP p)
	{
		int index = 0;
		double mindist = 60000000.0;
		double dist;
		if (this.path == null || this.path.length == 0) {
			return;
		}
		for (int i = 0; i < this.path.length; i++)
		{
			dist = this.getDistance2D(this.path[i].getX(), this.path[i].getZ(), p.posX, p.posZ);
			if (dist < mindist)
			{
				mindist = dist;
				index = i;
			}
		}
		this.removePathPoint(index);
	}

	public void removeAllPoints(int mode)
	{
		// mode: 0 = Linear, 1 = Circular, 2 = Elliptical, 3 = Path
		if (mode == 1) {
			this.removeCenterPoint(mode);
			this.removeTargetPoint(mode);
		}
		else if (mode == 2) {
			this.removeCenterPoint(mode);
			this.removeTargetPoint(mode);
		}
		else if (mode == 3) {
			this.path = null;
			this.multishotGui.addMessage("All points removed");
		}
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

	public MsPoint[] getPath()
	{
		return this.path;
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

	public void movePlayer(EntityClientPlayerMP p, int mode)
	{
		// mode: 0 = Linear, 1 = Circular, 2 = Elliptical, 3 = Path
		if (mode == 0) { // Linear
			this.movePlayerLinear(p);
		}
		else if (mode == 1) { // Circular
		}
		else if (mode == 2) { // Elliptical
		}
		else if (mode == 3) { // Path
		}
	}

	public double getDistance2D(double x1, double z1, double x2, double z2)
	{
		return Math.sqrt(((x1 - x2) * (x1 - x2)) + ((z1 - z2) * (z1 - z2)));
	}

	public boolean startMotion(EntityClientPlayerMP p, int mode)
	{
		// mode: 0 = Linear, 1 = Circular, 2 = Elliptical, 3 = Path
		double px = p.posX;
		double pz = p.posZ;

		if (mode == 0) { // Linear
		}
		else if (mode == 1) { // Circular
			if (this.circleCenter == null) {
				this.multishotGui.addMessage("startMotion(): Error: Circle center point not set!");
				return false;
			}
			double cx = this.circleCenter.getX();
			double cz = this.circleCenter.getZ();
			this.circleRadius = this.getDistance2D(cx, cz, px, pz);
			if (this.circleTarget != null) {
				this.setUseTarget(true);
			}
			else {
				this.setUseTarget(false);
			}
		}
		else if (mode == 2) { // Elliptical
			if (this.ellipseCenter == null) {
				this.multishotGui.addMessage("startMotion(): Error: Ellipse center point not set!");
				return false;
			}
			if (this.ellipseRadiusA <= 0.0 || this.ellipseRadiusB <= 0.0) {
				this.multishotGui.addMessage("startMotion(): Error: Ellipse radiuses not set!");
				return false;
			}
			if (this.ellipseTarget != null) {
				this.setUseTarget(true);
			}
			else {
				this.setUseTarget(false);
			}
		}
		else if (mode == 3) { // Path
			this.multishotGui.addMessage("startMotion(): Error: Path mode not implemented yet!");
			return false;
		}

		return true;
	}
}
