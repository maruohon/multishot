package fi.dy.masa.minecraft.mods.multishot.motion;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.entity.EntityClientPlayerMP;
import fi.dy.masa.minecraft.mods.multishot.Multishot;
import fi.dy.masa.minecraft.mods.multishot.config.MsConfigs;
import fi.dy.masa.minecraft.mods.multishot.libs.MsMathHelper;
import fi.dy.masa.minecraft.mods.multishot.reference.MsConstants;
import fi.dy.masa.minecraft.mods.multishot.state.MsClassReference;
import fi.dy.masa.minecraft.mods.multishot.state.MsState;

public class MsMotion
{
	private MsMotionJson jsonHandler;
	private MsPoint circleCenter = null;
	private MsPoint circleTarget = null;
	private double circleRadius = 0.0;
	private double circleStartAngle = 0.0;
	private double circleCurrentAngle = 0.0;
	private double circleAngularVelocity = 0.0;

	private MsPoint ellipseCenter = null;
	private MsPoint ellipseTarget = null;
	private MsPoint ellipsePointA = null;
	private MsPoint ellipsePointB = null;
	private double ellipseRadiusA = -1.0;
	private double ellipseRadiusB = -1.0;

	MsPaths paths = null;
	private int pathIndexClipboard = -1;

	private boolean useTarget = false; // Do we lock the camera to look at the target point

	public float yawIncrement = 0.0f;
	public float pitchIncrement = 0.0f;
	public float prevYaw = 0.0f;
	public float prevPitch = 0.0f;

	private MsPoint segmentStart = null;
	private MsPoint segmentEnd = null;
	private double segmentProgress = 0.0d; // 0..1
	private double segmentLength = 0.0d;
	private double segmentYawChange = 0.0d;
	private double segmentPitchChange = 0.0d;

	public MsMotion()
	{
		this.jsonHandler = new MsMotionJson(this);
		this.paths = new MsPaths();
		this.segmentStart = new MsPoint(0.0d, 0.0d, 0.0d, 0.0f, 0.0f);
		this.segmentEnd = new MsPoint(0.0d, 0.0d, 0.0d, 0.0f, 0.0f);
		this.readAllPointsFromFile();
	}

	public void reloadCurrentPath()
	{
		this.jsonHandler.readPathPointsFromFile(this.getPathIndex());
		int id = this.getPathIndex() + 1;
		int num = this.getPath().getNumPoints();
		MsClassReference.getGui().addMessage(String.format("Reloaded path #%d from file (%d points)", id, num));
	}

	public void readAllPointsFromFile()
	{
		this.jsonHandler.readAllPointsFromFile();
	}

	public void savePointsToFile()
	{
		this.jsonHandler.savePointsToFile();
	}

	public void saveCurrentPathToFile()
	{
		this.jsonHandler.savePathToFile(this.getPathIndex());
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
			this.replace(x, z, y, yaw, pitch);
		}

		public void replace(double x, double z, double y, float yaw, float pitch)
		{
			this.posX = x;
			this.posZ = z;
			this.posY = y;
			this.yaw = yaw;
			this.pitch = pitch;
		}

		public void copyFrom(MsPoint p)
		{
			this.replace(p.getX(), p.getZ(), p.getY(), p.getYaw(), p.getPitch());
		}

		public double getX() { return this.posX; }
		public double getZ() { return this.posZ; }
		public double getY() { return this.posY; }
		public float getYaw() { return this.yaw; }
		public float getPitch() { return this.pitch; }
	}

	public class MsPath
	{
		private List<MsPoint> points;
		private MsPoint target;
		private int current;
		private boolean reverse = false; // Reverse the order (when using next() and previous())

		public MsPath()
		{
			this.target = null;
			this.current = 0;
			this.reverse = false;
			this.points = new ArrayList<MsPoint>();
		}

		public void resetPosition()
		{
			this.current = 0;
		}

		public void clearPath()
		{
			try
			{
				this.target = null;
				this.current = 0;
				this.reverse = false;
				this.points.clear();
			}
			catch (Exception e)
			{
				Multishot.logSevere("Error clearing the path in clearPath(): " + e.getMessage());
			}
		}

		public void incrementPosition()
		{
			if (this.reverse == true)
			{
				if (--this.current < 0)
				{
					this.current = this.points.size() - 1;
				}
			}
			else
			{
				if (++this.current >= this.points.size())
				{
					this.current = 0;
				}
			}
		}

		public void setReverse(boolean val)
		{
			this.reverse = val;
		}

		public MsPoint getTarget()
		{
			return this.target;
		}

		public void setTarget(MsPoint p)
		{
			this.target = p;
		}

		public int getNumPoints()
		{
			if (this.points != null)
			{
				return this.points.size();
			}
			return 0;
		}

		public MsPoint getPoint(int index)
		{
			if (this.points == null || index >= this.points.size())
			{
				return null;
			}
			try
			{
				return this.points.get(index);
			}
			catch (Exception e)
			{
				Multishot.logSevere("Error in getPoint(): " + e.getMessage());
			}
			return null;
		}

		public void addPoint(MsPoint p)
		{
			try
			{
				this.points.add(p);
			}
			catch (Exception e)
			{
				Multishot.logSevere("Error adding a point in addPoint(p): " + e.getMessage());
			}
		}

		public void addPoint(double x, double z, double y, float yaw, float pitch)
		{
			try
			{
				this.points.add(new MsPoint(x, z, y, yaw, pitch));
			}
			catch (Exception e)
			{
				Multishot.logSevere("Error adding a point in addPoint(x, z, y, y, p): " + e.getMessage());
			}
		}

		public void addPoint(double x, double z, double y, float yaw, float pitch, int index)
		{
			if (this.points == null || index > this.points.size())
			{
				MsClassReference.getGui().addMessage(String.format("Error: Can't add point #%d, invalid index", index + 1));
				return;
			}
			try
			{
				this.points.add(index, new MsPoint(x, z, y, yaw, pitch));
			}
			catch (Exception e)
			{
				Multishot.logSevere("Error adding a point in addPoint(x, z, y, y, p, i): " + e.getMessage());
			}
		}

		public void removePoint(int index)
		{
			if (this.points == null || index >= this.points.size())
			{
				MsClassReference.getGui().addMessage(String.format("Error: Can't remove point #%d, invalid index", index + 1));
				return;
			}
			try
			{
				this.points.remove(index);
				MsClassReference.getGui().addMessage(String.format("Removed point #%d", index + 1));
			}
			catch (Exception e)
			{
				MsClassReference.getGui().addMessage(String.format("Error: Couldn't remove point #%d", index + 1));
				Multishot.logSevere("Error removing point '" + index + "' in removePoint(): " + e.getMessage());
			}
		}

		public void replacePoint(double x, double z, double y, float yaw, float pitch, int index)
		{
			if (this.points == null || index >= this.points.size())
			{
				MsClassReference.getGui().addMessage(String.format("Error: Can't replace point #%d, invalid index", index + 1));
				return;
			}

			this.getPoint(index).replace(x, z, y, yaw, pitch);
		}

		public int getNearestPointIndex(double x, double z, double y)
		{
			if (this.points == null || this.points.size() == 0)
			{
				return -1;
			}

			int index = 0;
			int len = this.points.size();
			double mindist;
			double dist;
			MsPoint p;

			p = this.points.get(0);
			mindist = MsMathHelper.distance3D(p.getX(), p.getZ(), p.getY(), x, z, y);

			for (int i = 1; i < len; i++)
			{
				p = this.points.get(i);
				dist = MsMathHelper.distance3D(p.getX(), p.getZ(), p.getY(), x, z, y);
				if (dist < mindist)
				{
					mindist = dist;
					index = i;
				}
			}
			return index;
		}

		private MsPoint getNextPrevious(boolean b)
		{
			if (this.points == null || this.points.size() == 0)
			{
				return null;
			}

			boolean next = b;
			int i = this.current;

			if (this.reverse == true)
			{
				next = ! next;
			}

			if (next == true)
			{
				if (++i >= this.points.size())
				{
					i = 0;
				}
			}
			else
			{
				if (--i > 0)
				{
					i = this.points.size() - 1;
				}
			}

			return this.getPoint(i);
		}

		public MsPoint getNext()
		{
			return this.getNextPrevious(true);
		}

		public MsPoint getPrevious()
		{
			return this.getNextPrevious(false);
		}

		public MsPoint getCurrent()
		{
			return this.getPoint(this.current);
		}
	}

	public class MsPaths
	{
		private MsPath[] paths;
		private MsPath activePath;
		private int activePathIndex;
		private static final int NUM_PATHS = 9;

		public MsPaths()
		{
			this.paths = new MsPath[NUM_PATHS];

			for (int i = 0; i < NUM_PATHS; i++)
			{
				this.paths[i] = new MsPath();
			}

			this.activePathIndex = 0;
			this.activePath = this.paths[0];
		}

		public void setActivePath(int i)
		{
			if (i < 0 || i >= NUM_PATHS)
			{
				Multishot.logSevere("setActivePath(): Invalid path number: " + i);
				return;
			}

			this.activePathIndex = i;
			this.activePath = this.paths[i];
		}

		public void selectNextPath()
		{
			int i = this.activePathIndex;
			if (++i >= NUM_PATHS)
			{
				i = 0;
			}
			this.activePathIndex = i;
			this.setActivePath(this.activePathIndex);
		}

		public void selectPreviousPath()
		{
			int i = this.activePathIndex;
			if (--i < 0)
			{
				i = NUM_PATHS - 1;
			}
			this.activePathIndex = i;
			this.setActivePath(this.activePathIndex);
		}

		public int getPathIndex()
		{
			return this.activePathIndex;
		}

		public MsPath getPath()
		{
			return this.activePath;
		}

		public MsPath getPath(int i)
		{
			if (i < 0 || i >= NUM_PATHS)
			{
				return null;
			}
			return this.paths[i];
		}
	}

	public MsPath getPath()
	{
		return this.paths.getPath();
	}

	public MsPath getPath(int i)
	{
		return this.paths.getPath(i);
	}

	public int getPathIndex()
	{
		return this.paths.getPathIndex();
	}

	public void setActivePath(int i)
	{
		this.paths.setActivePath(i);
	}

	public void selectNextPath()
	{
		this.paths.selectNextPath();
		int id = this.getPathIndex() + 1;
		int num = this.getPath().getNumPoints();
		MsClassReference.getGui().addMessage(String.format("Changed active path to #%d (%d points)", id, num));
		this.savePointsToFile();
	}

	public void selectPreviousPath()
	{
		this.paths.selectPreviousPath();
		int id = this.getPathIndex() + 1;
		int num = this.getPath().getNumPoints();
		MsClassReference.getGui().addMessage(String.format("Changed active path to #%d (%d points)", id, num));
		this.savePointsToFile();
	}

	public boolean getDoReorientation()
	{
		MsConfigs cfg = MsClassReference.getMsConfigs();
		if (this.getMotionMode() == MsConstants.MOTION_MODE_LINEAR && (cfg.getRotationYaw() != 0.0f || cfg.getRotationPitch() != 0.0f))
		{
			return true;
		}
		if (this.getMotionMode() == MsConstants.MOTION_MODE_CIRCLE && this.getUseTarget() == true)
		{
			return true;
		}
		if (this.getMotionMode() == MsConstants.MOTION_MODE_ELLIPSE && this.getUseTarget() == true)
		{
			return true;
		}
		if (this.getMotionMode() == MsConstants.MOTION_MODE_PATH_LINEAR || this.getMotionMode() == MsConstants.MOTION_MODE_PATH_SMOOTH)
		{
			return true;
		}
		return false;
	}

	private int getMotionMode()
	{
		return MsClassReference.getMsConfigs().getMotionMode();
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
			this.savePointsToFile();
		}
		else if (mode == MsConstants.MOTION_MODE_ELLIPSE)
		{
			this.ellipseCenter = new MsPoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);
			MsClassReference.getGui().addMessage(String.format("Added ellipse center point at x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch));
			this.savePointsToFile();
		}
	}

	public void setTargetPointFromCurrentPos(EntityClientPlayerMP p)
	{
		if (p == null) {
			Multishot.logSevere("setTargetPointFromCurrentPos(): player was null");
			return;
		}

		MsPoint pt = new MsPoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);

		int mode = this.getMotionMode();
		if (mode == MsConstants.MOTION_MODE_CIRCLE)
		{
			this.circleTarget = pt;
			MsClassReference.getGui().addMessage(String.format("Added circle target point at x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch));
			this.savePointsToFile();
		}
		else if (mode == MsConstants.MOTION_MODE_ELLIPSE)
		{
			this.ellipseTarget = pt;
			MsClassReference.getGui().addMessage(String.format("Added ellipse target point at x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch));
			this.savePointsToFile();
		}
		else if (mode == MsConstants.MOTION_MODE_PATH_LINEAR || mode == MsConstants.MOTION_MODE_PATH_SMOOTH)
		{
			this.getPath().setTarget(pt);
			MsClassReference.getGui().addMessage(String.format("Added path target point at x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch));
			this.saveCurrentPathToFile();
		}
	}

	public void addPathPointFromCurrentPos(EntityClientPlayerMP p)
	{
		if (p == null) {
			Multishot.logSevere("addPathPointFromCurrentPos(): player was null");
			return;
		}

		int mode = this.getMotionMode();
		if (mode == MsConstants.MOTION_MODE_PATH_LINEAR || mode == MsConstants.MOTION_MODE_PATH_SMOOTH)
		{
			this.getPath().addPoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);
			int i = this.getPath().getNumPoints();
			String msg = String.format("Added point #" + i + " at: x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f",
							p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch);
			MsClassReference.getGui().addMessage(msg);
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
			this.saveCurrentPathToFile();
		}
	}

	public void removeCenterPoint()
	{
		int mode = this.getMotionMode();
		if (mode == MsConstants.MOTION_MODE_CIRCLE)
		{
			if (this.circleCenter != null)
			{
				this.circleCenter = null;
				MsClassReference.getGui().addMessage("Removed circle center point");
				this.savePointsToFile();
			}
		}
		else if (mode == MsConstants.MOTION_MODE_ELLIPSE)
		{
			if (this.ellipseCenter != null)
			{
				this.ellipseCenter = null;
				MsClassReference.getGui().addMessage("Removed ellipse center point");
				this.savePointsToFile();
			}
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
				this.savePointsToFile();
			}
		}
		else if (mode == MsConstants.MOTION_MODE_ELLIPSE)
		{
			if (this.ellipseTarget != null)
			{
				this.ellipseTarget = null;
				MsClassReference.getGui().addMessage("Removed ellipse target point");
				this.savePointsToFile();
			}
		}
		else if (mode == MsConstants.MOTION_MODE_PATH_LINEAR || mode == MsConstants.MOTION_MODE_PATH_SMOOTH)
		{
			this.getPath().setTarget(null);
			MsClassReference.getGui().addMessage("Removed path target point");
			this.saveCurrentPathToFile();
		}
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
			this.getPath().removePoint(this.getPath().getNearestPointIndex(p.posX, p.posZ, p.posY));
			this.saveCurrentPathToFile();
		}
	}

	public void storeNearestPathPointIndex(EntityClientPlayerMP p)
	{
		if (p == null) {
			Multishot.logSevere("storeNearestPathPointIndex(): player was null");
			return;
		}

		this.pathIndexClipboard = this.getPath().getNearestPointIndex(p.posX, p.posZ, p.posY);

		if (this.pathIndexClipboard >= 0)
		{
			MsClassReference.getGui().addMessage(String.format("Stored point #%d", this.pathIndexClipboard + 1));
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
		if ((mode == MsConstants.MOTION_MODE_PATH_LINEAR || mode == MsConstants.MOTION_MODE_PATH_SMOOTH) == false) {
			return;
		}

		if (this.pathIndexClipboard < 0) {
			MsClassReference.getGui().addMessage(String.format("Error: Can't move point, no point selected!"));
			return;
		}

		if (this.pathIndexClipboard >= this.getPath().getNumPoints())
		{
			MsClassReference.getGui().addMessage(String.format("Error: Can't move point #%d, invalid index", this.pathIndexClipboard + 1));
			return;
		}

		this.getPath().replacePoint(p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch, this.pathIndexClipboard);

		MsClassReference.getGui().addMessage(String.format("Moved point #%d to: x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f",
				this.pathIndexClipboard + 1, p.posX, p.posZ, p.posY, p.rotationYaw, p.rotationPitch));
		this.saveCurrentPathToFile();
	}

	public void removeAllPoints()
	{
		int mode = this.getMotionMode();
		if (mode == MsConstants.MOTION_MODE_CIRCLE || mode == MsConstants.MOTION_MODE_ELLIPSE)
		{
			this.removeCenterPoint();
			this.removeTargetPoint();
		}
		else if (mode == MsConstants.MOTION_MODE_PATH_LINEAR || mode == MsConstants.MOTION_MODE_PATH_SMOOTH)
		{
			this.getPath().clearPath();
			MsClassReference.getGui().addMessage(String.format("Path #%d cleared", this.paths.getPathIndex() + 1));
			this.saveCurrentPathToFile();
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

	public void setCircleCenter(MsPoint p)
	{
		this.circleCenter = p;
	}

	public void setCircleTarget(MsPoint p)
	{
		this.circleTarget = p;
	}

	public void setEllipseCenter(MsPoint p)
	{
		this.ellipseCenter = p;
	}

	public void setEllipseTarget(MsPoint p)
	{
		this.ellipseTarget = p;
	}

	public boolean linearSegmentInit(EntityClientPlayerMP player, MsPoint tgt)
	{
		if (player == null) {
			Multishot.logSevere("linearSegmentInit(): player was null");
			return false;
		}
		if (tgt == null) {
			Multishot.logSevere("linearSegmentInit(): target was null");
			return false;
		}

		this.segmentStart.replace(player.posX, player.posZ, player.posY, player.rotationYaw, player.rotationPitch);
		this.segmentEnd.copyFrom(tgt);
		this.segmentProgress = 0.0f; // 0..1
		this.segmentLength = MsMathHelper.distance3D(tgt.getX(), tgt.getZ(), tgt.getY(), player.posX, player.posZ, player.posY);

		this.segmentYawChange = (tgt.getYaw() - player.rotationYaw) % 360.0f;
		if (this.segmentYawChange > 180.0f) { this.segmentYawChange -= 360.0f; }
		else if (this.segmentYawChange < -180.0f) { this.segmentYawChange += 360.0f; }
		this.segmentPitchChange = tgt.getPitch() - player.rotationPitch;
		// FIXME debug
		//System.out.printf("tgt.getYaw(): %.3f p.rotationYaw: %.3f\n", tgt.getYaw(), p.rotationYaw);
		//System.out.printf("tgt.getPitch(): %.3f p.rotationPitch: %.3f\n", tgt.getPitch(), p.rotationPitch);
		//System.out.printf("segmentYawChange: %.3f segmentPitchChange: %.3f\n", this.segmentYawChange, this.segmentPitchChange);
		//System.out.printf("segmentLength: %.3f segmentAngleH: %.3f segmentAngleV: %.3f\n", this.segmentLength, this.segmentAngleH, this.segmentAngleV);

		return true;
	}

	public boolean linearSegmentMove(EntityClientPlayerMP player, int speed)
	{
		if (player == null) {
			Multishot.logSevere("linearSegmentMove(): player was null");
			return false;
		}

		double movement = (double)speed / 20000.0d; // Speed is in 1/1000 m/s, TPS is 20
		if (((this.segmentProgress * this.segmentLength) + movement) > this.segmentLength)
		{
			player.setPositionAndRotation(this.segmentEnd.getX(), this.segmentEnd.getY(), this.segmentEnd.getZ(), player.rotationYaw, player.rotationPitch);
			this.reOrientPlayerToAngle(player, this.segmentEnd.getYaw(), this.segmentEnd.getPitch());
			this.segmentProgress = 1.0d;
			return true; // done for this segment
		}
		else
		{
			this.segmentProgress += (movement / this.segmentLength);
			double x = this.segmentStart.getX() + (this.segmentProgress * (this.segmentEnd.getX() - this.segmentStart.getX()));
			double z = this.segmentStart.getZ() + (this.segmentProgress * (this.segmentEnd.getZ() - this.segmentStart.getZ()));
			double y = this.segmentStart.getY() + (this.segmentProgress * (this.segmentEnd.getY() - this.segmentStart.getY()));
			player.setPositionAndRotation(x, y, z, player.rotationYaw, player.rotationPitch);

			float yaw = this.segmentStart.getYaw() + (float)(this.segmentProgress * this.segmentYawChange);
			float pitch = this.segmentStart.getPitch() + (float)(this.segmentProgress * this.segmentPitchChange);
			this.reOrientPlayerToAngle(player, yaw, pitch);
		}
		return false;
	}

	// This method re-orients the player to the given angle, by setting the per-tick angle increments
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

		// "The interpolated method"
		// Store the initial values and the increments, which are used in the render event handler to interpolate the angle
		//this.prevYaw = p.rotationYaw;
		//this.prevPitch = p.rotationPitch;
		//this.yawIncrement = yawInc;
		//this.pitchIncrement = pitch - p.rotationPitch;

		// "The direct method", this also seems to work now,
		// only the hand is a bit jittery, but then again the HUD is probably usually hidden anyway:
		p.rotationYaw += yawInc;
		p.rotationPitch = pitch;
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
		double yaw = Math.atan2(px - tx, tz - pz) * 180.0d / Math.PI;
		double pitch = (-Math.atan2(ty - py, MsMathHelper.distance2D(tx, tz, px, pz)) * 180.0d / Math.PI);
		this.reOrientPlayerToAngle(p, (float)yaw, (float)pitch);
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

	public void toggleMoveToStartPoint(EntityClientPlayerMP player)
	{
		int mode = MsClassReference.getMsConfigs().getMotionMode();
		if (MsState.getMotion() == false && (mode == MsConstants.MOTION_MODE_PATH_LINEAR || mode == MsConstants.MOTION_MODE_PATH_SMOOTH))
		{
			if (MsState.getMoveToStart() == true)
			{
				MsState.setMoveToStart(false);
				return;
			}
			if (this.getPath().getNumPoints() == 0)
			{
				MsClassReference.getGui().addMessage("Error: No path points set!");
				return;
			}

			// If we don't have a global target point, use the point's rotation angles
			if (this.getPath().getTarget() == null)
			{
				this.linearSegmentInit(player, this.getPath().getPoint(0));
			}
			else // Use global target point
			{
				MsPoint p0 = this.getPath().getPoint(0);
				MsPoint tgt = this.getPath().getTarget();
				double yaw = Math.atan2(p0.getX() - tgt.getX(), tgt.getZ() - p0.getZ()) * 180.0d / Math.PI;
				double pitch = (-Math.atan2(tgt.getY() - p0.getY(), MsMathHelper.distance2D(tgt.getX(), tgt.getZ(), p0.getX(), p0.getZ())) * 180.0d / Math.PI);
				MsPoint p = new MsPoint(p0.getX(), p0.getZ(), p0.getY(), (float)yaw, (float)pitch);
				this.linearSegmentInit(player, p);
			}
			MsState.setMoveToStart(true);
		}
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
			MsClassReference.getGui().addMessage("Ellipse mode not implemented yet");
			return false;
/*
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
*/
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
