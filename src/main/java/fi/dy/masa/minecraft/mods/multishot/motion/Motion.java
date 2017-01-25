package fi.dy.masa.minecraft.mods.multishot.motion;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import fi.dy.masa.minecraft.mods.multishot.Multishot;
import fi.dy.masa.minecraft.mods.multishot.config.Configs;
import fi.dy.masa.minecraft.mods.multishot.gui.MsGui;
import fi.dy.masa.minecraft.mods.multishot.handlers.RenderEventHandler;
import fi.dy.masa.minecraft.mods.multishot.reference.Constants;
import fi.dy.masa.minecraft.mods.multishot.state.State;
import fi.dy.masa.minecraft.mods.multishot.util.MathHelper;
import fi.dy.masa.minecraft.mods.multishot.worker.RecordingHandler;

@SuppressWarnings("unused")
public class Motion
{
    private static Motion instance;

    private MotionJson jsonHandler;
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

    // This is used to indicate that the current motion is just moving to the path start point:
    private boolean stateMoveToStart = false;
    // This indicates if we want to start the actual motion after we reach the start point:
    private boolean startMotion = false;
    private Entity renderViewEntity;

    public Motion()
    {
        instance = this;
        this.stateMoveToStart = false;
        this.startMotion = false;
        this.jsonHandler = new MotionJson(this);
        this.paths = new MsPaths();
        this.segmentStart = new MsPoint(0.0d, 0.0d, 0.0d, 0.0f, 0.0f);
        this.segmentEnd = new MsPoint(0.0d, 0.0d, 0.0d, 0.0f, 0.0f);
        this.readAllPointsFromFile();
    }

    public static Motion getMotion()
    {
        return instance;
    }

    protected void addMessage(String msg)
    {
        MsGui.getGui().addGuiMessage(msg);
    }

    public void reloadCurrentPath()
    {
        int id = this.getPathIndex() + 1;

        if (this.jsonHandler.readPathPointsFromFile(this.getPathIndex()) == true)
        {
            this.addMessage(I18n.format("multishot.gui.message.reloaded.path", id, this.getPath().getNumPoints()));
        }
        else
        {
            this.addMessage(I18n.format("multishot.gui.message.reload.path.fail", id));
        }
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

        public MsPoint(Entity entity)
        {
            this(entity.posX, entity.posZ, entity.posY, entity.rotationYaw, entity.rotationPitch);
        }

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

        public void setYaw (float val)
        {
            this.yaw = val;
        }

        public void setPitch (float val)
        {
            this.pitch = val;
        }

        public boolean equals(MsPoint other)
        {
            return (this.posX == other.posX
                && this.posY == other.posY
                && this.posZ == other.posZ
                && this.yaw == other.yaw
                && this.pitch == other.pitch);
        }
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
            if (this.reverse == false || this.getNumPoints() == 0)
            {
                this.current = 0;
            }
            else
            {
                this.current = this.getNumPoints() - 1;
            }
        }

        public void clearPath()
        {
            this.target = null;
            this.current = 0;
            this.reverse = false;
            if (this.points != null)
            {
                this.points.clear();
            }
        }

        public void incrementPosition()
        {
            if (this.getNumPoints() == 0)
            {
                this.current = 0;
                return;
            }

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

        public boolean getReverse()
        {
            return this.reverse;
        }

        public void setReverse(boolean val)
        {
            this.reverse = val;
        }

        public void reverse()
        {
            this.reverse = ! this.reverse;
            String rev = I18n.format(this.reverse ? "multishot.gui.message.reversed" : "multishot.gui.message.normal");
            Motion.getMotion().addMessage(I18n.format("Set the path traveling direction to") + ": " + rev);
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
            if (this.points == null || index < 0 || index >= this.points.size())
            {
                return null;
            }

            return this.points.get(index);
        }

        public void addPoint(MsPoint p)
        {
            if (this.points == null)
            {
                Multishot.logger.warn("Error adding a point in addPoint(p)");
                return;
            }

            this.points.add(p);
        }

        public void addPoint(Entity entity)
        {
            if (this.points == null)
            {
                Multishot.logger.warn("Error adding a point in addPoint(x, z, y, y, p)");
                return;
            }

            this.points.add(new MsPoint(entity));
        }

        public void addPoint(Entity entity, int index)
        {
            if (this.points == null || index > this.points.size())
            {
                Motion.getMotion().addMessage(I18n.format("multishot.gui.message.add.point.invalid.index", index + 1));
                return;
            }

            this.points.add(index, new MsPoint(entity));

        }

        public void removePoint(int index)
        {
            if (this.points == null || index < 0 || index >= this.points.size())
            {
                Motion.getMotion().addMessage(I18n.format("multishot.gui.message.remove.point.invalid.index", index + 1));
                return;
            }

            this.points.remove(index);
            Motion.getMotion().addMessage(I18n.format("multishot.gui.message.removed.point", index + 1));
        }

        public void replacePoint(double x, double z, double y, float yaw, float pitch, int index)
        {
            MsPoint point = this.getPoint(index);
            if (point == null)
            {
                Motion.getMotion().addMessage(I18n.format("multishot.gui.message.replace.point.invalid.index", index + 1));
                return;
            }

            point.replace(x, z, y, yaw, pitch);
        }

        public int getNearestPointIndex(Entity entity)
        {
            return this.getNearestPointIndex(entity.posX, entity.posZ, entity.posY);
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
            mindist = MathHelper.distance3D(p.getX(), p.getZ(), p.getY(), x, z, y);

            for (int i = 1; i < len; i++)
            {
                p = this.points.get(i);
                dist = MathHelper.distance3D(p.getX(), p.getZ(), p.getY(), x, z, y);
                if (dist < mindist)
                {
                    mindist = dist;
                    index = i;
                }
            }
            return index;
        }

        private MsPoint getNextPrevious(boolean next)
        {
            if (this.points == null || this.points.size() == 0)
            {
                return null;
            }

            int i = this.current;
            if (next == true || this.reverse == true)
            {
                if (++i >= this.points.size())
                {
                    i = 0;
                }
            }
            else
            {
                if (--i < 0)
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

        public MsPoint getFirst()
        {
            if (this.reverse == false || this.getNumPoints() == 0)
            {
                return this.getPoint(0);
            }

            return this.getPoint(this.getNumPoints() - 1);
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
                Multishot.logger.fatal("setActivePath(): Invalid path number: " + i);
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

    public void reversePath()
    {
        int mode = Configs.getConfig().getMotionMode();
        if (mode == Constants.MOTION_MODE_PATH_LINEAR || mode == Constants.MOTION_MODE_PATH_SMOOTH)
        {
            this.getPath().reverse();
            this.saveCurrentPathToFile();
        }
    }

    public void selectNextPath()
    {
        this.paths.selectNextPath();
        int id = this.getPathIndex() + 1;
        int num = this.getPath().getNumPoints();
        this.addMessage(I18n.format("multishot.gui.message.changed.active.path", id, num));
        this.savePointsToFile();
    }

    public void selectPreviousPath()
    {
        this.paths.selectPreviousPath();
        int id = this.getPathIndex() + 1;
        int num = this.getPath().getNumPoints();
        this.addMessage(I18n.format("multishot.gui.message.changed.active.path", id, num));
        this.savePointsToFile();
    }

    public boolean getDoPlayerReorientation()
    {
        if (this.getMotionMode() == Constants.MOTION_MODE_LINEAR && (Configs.getConfig().getRotationYaw() != 0.0f || Configs.getConfig().getRotationPitch() != 0.0f))
        {
            return true;
        }
        if (this.getMotionMode() == Constants.MOTION_MODE_CIRCLE && this.getUseTarget() == true)
        {
            return true;
        }
        if (this.getMotionMode() == Constants.MOTION_MODE_ELLIPSE && this.getUseTarget() == true)
        {
            return true;
        }
        if (this.getMotionMode() == Constants.MOTION_MODE_PATH_LINEAR || this.getMotionMode() == Constants.MOTION_MODE_PATH_SMOOTH)
        {
            return true;
        }

        return false;
    }

    private int getMotionMode()
    {
        return Configs.getConfig().getMotionMode();
    }

    public void setUseTarget(boolean t)
    {
        this.useTarget = t;
    }

    public boolean getUseTarget()
    {
        return this.useTarget;
    }

    public void setCenterPointFromCurrentPos(Entity entity)
    {
        if (entity == null)
        {
            Multishot.logger.warn("setCenterPointFromCurrentPos(): entity was null");
            return;
        }

        int mode = this.getMotionMode();
        if (mode == Constants.MOTION_MODE_CIRCLE)
        {
            this.circleCenter = new MsPoint(entity);
            this.addMessage(I18n.format("multishot.gui.message.added.circle.center") + String.format(" x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", entity.posX, entity.posZ, entity.posY, entity.rotationYaw, entity.rotationPitch));
            this.savePointsToFile();
        }
        else if (mode == Constants.MOTION_MODE_ELLIPSE)
        {
            this.ellipseCenter = new MsPoint(entity);
            this.addMessage(I18n.format("multishot.gui.message.added.ellipse.center") + String.format(" x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", entity.posX, entity.posZ, entity.posY, entity.rotationYaw, entity.rotationPitch));
            this.savePointsToFile();
        }
    }

    public void setTargetPointFromCurrentPos(Entity entity)
    {
        if (entity == null)
        {
            Multishot.logger.warn("setTargetPointFromCurrentPos(): entity was null");
            return;
        }

        MsPoint pt = new MsPoint(entity);

        int mode = this.getMotionMode();
        if (mode == Constants.MOTION_MODE_CIRCLE)
        {
            this.circleTarget = pt;
            this.addMessage(I18n.format("multishot.gui.message.added.circle.target.point") + String.format(" x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", entity.posX, entity.posZ, entity.posY, entity.rotationYaw, entity.rotationPitch));
            this.savePointsToFile();
        }
        else if (mode == Constants.MOTION_MODE_ELLIPSE)
        {
            this.ellipseTarget = pt;
            this.addMessage(I18n.format("multishot.gui.message.added.ellipse.target.point") + String.format(" x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", entity.posX, entity.posZ, entity.posY, entity.rotationYaw, entity.rotationPitch));
            this.savePointsToFile();
        }
        else if (mode == Constants.MOTION_MODE_PATH_LINEAR || mode == Constants.MOTION_MODE_PATH_SMOOTH)
        {
            this.getPath().setTarget(pt);
            this.addMessage(I18n.format("multishot.gui.message.added.path.target.point") + String.format(" x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", entity.posX, entity.posZ, entity.posY, entity.rotationYaw, entity.rotationPitch));
            this.saveCurrentPathToFile();
        }
    }

    public void addPathPointFromCurrentPos(Entity entity)
    {
        if (entity == null)
        {
            Multishot.logger.warn("addPathPointFromCurrentPos(): entity was null");
            return;
        }

        int mode = this.getMotionMode();
        if (mode == Constants.MOTION_MODE_PATH_LINEAR || mode == Constants.MOTION_MODE_PATH_SMOOTH)
        {
            this.getPath().addPoint(entity);
            int i = this.getPath().getNumPoints();
            this.addMessage(I18n.format("multishot.gui.message.added.point", i) + String.format(" x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", entity.posX, entity.posZ, entity.posY, entity.rotationYaw, entity.rotationPitch));
        }
    }

    public void addPointFromCurrentPos(Entity entity)
    {
        if (entity == null)
        {
            Multishot.logger.warn("addPointFromCurrentPos(): entity was null");
            return;
        }

        int mode = this.getMotionMode();
        if (mode == Constants.MOTION_MODE_ELLIPSE)
        {
            this.ellipsePointA = new MsPoint(entity);
            this.addMessage(I18n.format("multishot.gui.message.added.ellipse.longer.axis") + String.format(" x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", entity.posX, entity.posZ, entity.posY, entity.rotationYaw, entity.rotationPitch));
        }
        else if (mode == Constants.MOTION_MODE_PATH_LINEAR || mode == Constants.MOTION_MODE_PATH_SMOOTH)
        {
            this.addPathPointFromCurrentPos(entity);
            this.saveCurrentPathToFile();
        }
    }

    public void insertPathPoint(Entity entity, boolean before)
    {
        if (entity == null)
        {
            Multishot.logger.warn("insertPathPoint(): entity was null");
            return;
        }

        int mode = this.getMotionMode();
        if (mode == Constants.MOTION_MODE_PATH_LINEAR || mode == Constants.MOTION_MODE_PATH_SMOOTH)
        {
            int nearest = this.getPath().getNearestPointIndex(entity);
            int i = 0;
            if (before == true && nearest >= 0)
            {
                i = nearest;
            }
            else if (before == false)
            {
                i = nearest + 1;
            }

            this.getPath().addPoint(entity, i);
            this.addMessage(I18n.format("multishot.gui.message.inserted.point", i + 1) + String.format(" x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", entity.posX, entity.posZ, entity.posY, entity.rotationYaw, entity.rotationPitch));
        }
    }

    public void removeCenterPoint()
    {
        int mode = this.getMotionMode();
        if (mode == Constants.MOTION_MODE_CIRCLE)
        {
            if (this.circleCenter != null)
            {
                this.circleCenter = null;
                this.addMessage(I18n.format("multishot.gui.message.removed.circle.center"));
                this.savePointsToFile();
            }
        }
        else if (mode == Constants.MOTION_MODE_ELLIPSE)
        {
            if (this.ellipseCenter != null)
            {
                this.ellipseCenter = null;
                this.addMessage(I18n.format("multishot.gui.message.removed.ellipse.center"));
                this.savePointsToFile();
            }
        }
    }

    public void removeTargetPoint()
    {
        int mode = this.getMotionMode();
        if (mode == Constants.MOTION_MODE_CIRCLE)
        {
            if (this.circleTarget != null)
            {
                this.circleTarget = null;
                this.addMessage(I18n.format("multishot.gui.message.removed.circle.target"));
                this.savePointsToFile();
            }
        }
        else if (mode == Constants.MOTION_MODE_ELLIPSE)
        {
            if (this.ellipseTarget != null)
            {
                this.ellipseTarget = null;
                this.addMessage(I18n.format("multishot.gui.message.removed.ellipse.target"));
                this.savePointsToFile();
            }
        }
        else if (mode == Constants.MOTION_MODE_PATH_LINEAR || mode == Constants.MOTION_MODE_PATH_SMOOTH)
        {
            this.getPath().setTarget(null);
            this.addMessage(I18n.format("multishot.gui.message.removed.path.target"));
            this.saveCurrentPathToFile();
        }
    }

    public void removeNearestPathPoint(Entity entity)
    {
        if (entity == null)
        {
            Multishot.logger.warn("removeNearestPathPoint(): entity was null");
            return;
        }

        int mode = this.getMotionMode();
        if (mode == Constants.MOTION_MODE_PATH_LINEAR || mode == Constants.MOTION_MODE_PATH_SMOOTH)
        {
            this.getPath().removePoint(this.getPath().getNearestPointIndex(entity));
            this.saveCurrentPathToFile();
        }
    }

    public void storeNearestPathPointIndex(Entity entity)
    {
        if (entity == null)
        {
            Multishot.logger.warn("storeNearestPathPointIndex(): entity was null");
            return;
        }

        this.pathIndexClipboard = this.getPath().getNearestPointIndex(entity);

        if (this.pathIndexClipboard >= 0)
        {
            this.addMessage(I18n.format("multishot.gui.message.stored.point", this.pathIndexClipboard + 1));
        }
        else
        {
            this.addMessage(I18n.format("multishot.gui.message.error.no.points"));
        }
    }

    public void replaceStoredPathPoint(Entity entity)
    {
        if (entity == null)
        {
            Multishot.logger.warn("replaceStoredPathPoint(): entity was null");
            return;
        }

        int mode = this.getMotionMode();
        if ((mode == Constants.MOTION_MODE_PATH_LINEAR || mode == Constants.MOTION_MODE_PATH_SMOOTH) == false)
        {
            return;
        }

        if (this.pathIndexClipboard < 0)
        {
            this.addMessage(I18n.format("multishot.gui.message.error.cant.move.no.point.selected"));
            return;
        }

        if (this.pathIndexClipboard >= this.getPath().getNumPoints())
        {
            this.addMessage(I18n.format("multishot.gui.message.error.cant.move.invalid.index", this.pathIndexClipboard + 1));
            return;
        }

        this.getPath().replacePoint(entity.posX, entity.posZ, entity.posY, entity.rotationYaw, entity.rotationPitch, this.pathIndexClipboard);

        this.addMessage(I18n.format("multishot.gui.message.moved.point", this.pathIndexClipboard + 1) + String.format(" x=%.2f z=%.2f y=%.2f yaw=%.2f pitch=%.2f", entity.posX, entity.posZ, entity.posY, entity.rotationYaw, entity.rotationPitch));
        this.saveCurrentPathToFile();
    }

    public void removeAllPoints()
    {
        int mode = this.getMotionMode();
        if (mode == Constants.MOTION_MODE_CIRCLE || mode == Constants.MOTION_MODE_ELLIPSE)
        {
            this.removeCenterPoint();
            this.removeTargetPoint();
        }
        else if (mode == Constants.MOTION_MODE_PATH_LINEAR || mode == Constants.MOTION_MODE_PATH_SMOOTH)
        {
            this.getPath().clearPath();
            this.addMessage(I18n.format("multishot.gui.message.path.cleared", this.paths.getPathIndex() + 1));
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

    private boolean linearSegmentInit(Entity entity, MsPoint end, MsPoint tgt)
    {
        if (entity == null)
        {
            Multishot.logger.fatal("linearSegmentInit(): entity was null");
            return false;
        }
        if (end == null)
        {
            Multishot.logger.fatal("linearSegmentInit(): end was null");
            return false;
        }
        if (tgt == null)
        {
            tgt = end;
        }

        this.segmentStart = new MsPoint(entity);
        this.segmentEnd.copyFrom(end);

        // If we want to be looking at a global target point, we need to calculate the angles at the segment's end point
        if (end.equals(tgt) == false)
        {
            float yaw = (float)(Math.atan2(end.getX() - tgt.getX(), tgt.getZ() - end.getZ()) * 180.0d / Math.PI);
            // Note: Since 1.8 the camera is actually at eye height, not at the player's y-coordinate
            float pitch = (float)(-Math.atan2(tgt.getY() - end.getY(), MathHelper.distance2D(tgt.getX(), tgt.getZ(), end.getX(), end.getZ())) * 180.0d / Math.PI);

            this.segmentEnd.setYaw(yaw);
            this.segmentEnd.setPitch(pitch);
        }

        this.segmentProgress = 0.0f; // 0..1
        this.segmentLength = MathHelper.distance3D(end.getX(), end.getZ(), end.getY(), entity.posX, entity.posZ, entity.posY);

        this.segmentYawChange = (this.segmentEnd.getYaw() - entity.rotationYaw) % 360.0f;
        if (this.segmentYawChange > 180.0f) { this.segmentYawChange -= 360.0f; }
        else if (this.segmentYawChange < -180.0f) { this.segmentYawChange += 360.0f; }
        this.segmentPitchChange = this.segmentEnd.getPitch() - entity.rotationPitch;
        // FIXME debug
        //System.out.printf("tgt.getYaw(): %.3f p.rotationYaw: %.3f\n", tgt.getYaw(), p.rotationYaw);
        //System.out.printf("tgt.getPitch(): %.3f p.rotationPitch: %.3f\n", tgt.getPitch(), p.rotationPitch);
        //System.out.printf("segmentYawChange: %.3f segmentPitchChange: %.3f\n", this.segmentYawChange, this.segmentPitchChange);
        //System.out.printf("segmentLength: %.3f segmentAngleH: %.3f segmentAngleV: %.3f\n", this.segmentLength, this.segmentAngleH, this.segmentAngleV);

        return true;
    }

    private boolean linearSegmentInit(Entity entity, MsPoint end)
    {
        return this.linearSegmentInit(entity, end, end);
    }

    private boolean linearSegmentMove(Entity entity, MsPoint tgt, int speed)
    {
        if (entity == null)
        {
            Multishot.logger.warn("linearSegmentMove(): entity was null");
            return false;
        }

        double movement = (double)Math.abs(speed) / 20000.0d; // Speed is in 1/1000 m/s, TPS is 20
        float yaw = 0.0f;
        float pitch = 0.0f;
        boolean retVal = false; // default: this segment is still unfinished

        if (((this.segmentProgress * this.segmentLength) + movement) >= this.segmentLength)
        {
            entity.setPosition(this.segmentEnd.getX(), this.segmentEnd.getY(), this.segmentEnd.getZ());
            //player.moveEntity(this.segmentEnd.getX() - player.posX, this.segmentEnd.getY() - player.posY, this.segmentEnd.getZ() - player.posZ);
            yaw = this.segmentEnd.getYaw();
            pitch = this.segmentEnd.getPitch();
            this.segmentProgress = 1.0d;
            retVal = true; // done for this segment
        }
        else
        {
            this.segmentProgress += (movement / this.segmentLength);
            double x = this.segmentStart.getX() + (this.segmentProgress * (this.segmentEnd.getX() - this.segmentStart.getX()));
            double z = this.segmentStart.getZ() + (this.segmentProgress * (this.segmentEnd.getZ() - this.segmentStart.getZ()));
            double y = this.segmentStart.getY() + (this.segmentProgress * (this.segmentEnd.getY() - this.segmentStart.getY()));
            entity.setPosition(x, y, z);
            //player.moveEntity(x - player.posX, y - player.posY, z - player.posZ);

            yaw = this.segmentStart.getYaw() + (float)(this.segmentProgress * this.segmentYawChange);
            pitch = this.segmentStart.getPitch() + (float)(this.segmentProgress * this.segmentPitchChange);
        }

        if (tgt == null)
        {
            this.reOrientEntityToAngle(entity, yaw, pitch);
        }
        else
        {
            this.reOrientEntityToTargetPoint(entity, tgt);
        }

        return retVal;
    }

    // This method re-orients the player to the given angle, by setting the per-tick angle increments
    private void reOrientEntityToAngle(Entity entity, float yaw, float pitch)
    {
        if (entity == null)
        {
            Multishot.logger.fatal("reOrientPlayerToAngle(): entity was null");
            return;
        }

        float yawInc = (yaw - entity.rotationYaw) % 360.0f;

        // Translate the increment to between -180..180 degrees
        if (yawInc > 180.0f) { yawInc -= 360.0f; }
        else if (yawInc < -180.0f) { yawInc += 360.0f; }

        // "The interpolated method"
        // Store the initial values and the increments, which are used in the render event handler to interpolate the angle
        this.prevYaw = entity.rotationYaw;
        this.prevPitch = entity.rotationPitch;
        this.yawIncrement = yawInc;
        this.pitchIncrement = pitch - entity.rotationPitch;

        // "The direct method", this also seems to work now,
        // only the hand is a bit jittery, but then again the HUD is probably usually hidden anyway:
        //p.rotationYaw += yawInc;
        //p.rotationPitch = pitch;

        if (Configs.getConfig().getUseFreeCamera())
        {
            EntityPlayer camera = (EntityPlayer) entity;

            //entity.prevPosX = entity.posX;
            //entity.prevPosY = entity.posY;
            //entity.prevPosZ = entity.posZ;
            //entity.lastTickPosX = entity.posX;
            //entity.lastTickPosY = entity.posY;
            //entity.lastTickPosZ = entity.posZ;

            entity.prevRotationYaw = entity.rotationYaw;
            entity.prevRotationPitch = entity.rotationPitch;
            camera.prevRotationYawHead = camera.rotationYawHead;
            camera.prevRenderYawOffset = camera.renderYawOffset;

            entity.rotationYaw += this.yawIncrement;
            entity.rotationPitch += this.pitchIncrement;
            entity.setRotationYawHead(entity.rotationYaw);

            //entity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ,
            //      entity.rotationYaw + this.yawIncrement, entity.rotationPitch + this.pitchIncrement);
        }
    }

    // This method re-orients the player to face the given point, by setting the per-tick angle increments,
    // which are then interpolated in the rendering phase to get a smooth rotation.
    private void reOrientEntityToTargetPoint(Entity entity, double tx, double tz, double ty)
    {
        if (entity == null)
        {
            Multishot.logger.warn("reOrientPlayerToTargetPoint(p, x, z, y): entity was null");
            return;
        }

        double px = entity.posX;
        double py = entity.posY;
        double pz = entity.posZ;
        // The angle in which the player sees the target point, in relation to the +z-axis
        double yaw = Math.atan2(px - tx, tz - pz) * 180.0d / Math.PI;
        double pitch = (-Math.atan2(ty - py, MathHelper.distance2D(tx, tz, px, pz)) * 180.0d / Math.PI);
        this.reOrientEntityToAngle(entity, (float)yaw, (float)pitch);
    }

    private void reOrientEntityToTargetPoint(Entity entity, MsPoint tgt)
    {
        if (entity == null)
        {
            Multishot.logger.warn("reOrientPlayerToTargetPoint(p, tgt): entity was null");
            return;
        }
        if (tgt == null)
        {
            Multishot.logger.warn("reOrientPlayerToTargetPoint(p, tgt): target was null");
            return;
        }

        this.reOrientEntityToTargetPoint(entity, tgt.getX(), tgt.getZ(), tgt.getY());
    }

    private boolean startMotion(Entity entity)
    {
        if (entity == null)
        {
            Multishot.logger.fatal("startMotion(): Error: entity was null");
            return false;
        }

        int mode = this.getMotionMode();
        if (mode == Constants.MOTION_MODE_LINEAR) // Linear
        {
        }
        else if (mode == Constants.MOTION_MODE_CIRCLE)
        {
            if (this.circleCenter == null)
            {
                this.addMessage("multishot.gui.message.error.startmotion.no.circle.center");
                return false;
            }
            double px = entity.posX;
            double pz = entity.posZ;
            double cx = this.circleCenter.getX();
            double cz = this.circleCenter.getZ();
            this.circleRadius = MathHelper.distance2D(cx, cz, px, pz);
            this.circleStartAngle = Math.atan2(cx - px, pz - cz); // The angle in which the center point sees the player, in relation to +z-axis
            this.circleCurrentAngle = this.circleStartAngle;
            this.circleAngularVelocity = ((double)Configs.getConfig().getMotionSpeed() / 20000.0) / this.circleRadius;
            if (this.circleTarget != null)
            {
                this.setUseTarget(true);
                this.reOrientEntityToTargetPoint(entity, this.circleTarget);
            }
            else
            {
                this.setUseTarget(false);
            }
        }
        else if (mode == Constants.MOTION_MODE_ELLIPSE)
        {
            this.addMessage("Ellipse mode not implemented yet");
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
        else if (mode == Constants.MOTION_MODE_PATH_LINEAR)
        {
            this.addMessage("startMotion(): Error: Path (linear) not implemented yet!");
            return false;
        }
        else if (mode == Constants.MOTION_MODE_PATH_SMOOTH)
        {
            this.addMessage("startMotion(): Error: Path (smooth) not implemented yet!");
            return false;
        }

        State.setMotion(true);
        return true;
    }

    private void stopMotion()
    {
        State.setMotion(false);
        this.stateMoveToStart = false;
        this.startMotion = false;

        if (State.getRecording())
        {
            RecordingHandler.getInstance().stopRecording();
        }

        Minecraft.getMinecraft().setIngameFocus();
    }

    public void toggleMotion(EntityPlayer player)
    {
        EntityPlayer entity = this.getCameraEntity(player);
        setCameraEntityPositionFromPlayer(entity, player);

        // Start motion mode
        if (State.getMotion() == false)
        {
            // This is part of "The interpolated method" rotation method
            this.prevYaw = entity.rotationYaw;
            this.prevPitch = entity.rotationPitch;

            int mode = this.getMotionMode();
            // Path modes and ellipse mode use the move-to-start-point mechanic before starting the actual motion
            // TODO: Ellipse mode and Path (smooth) mode
            if (mode == Constants.MOTION_MODE_PATH_LINEAR)
            {
                if (Configs.getConfig().getUseFreeCamera())
                {
                    setCameraEntityPositionFromPoint(entity, this.getPath().getFirst());
                }

                this.toggleMoveToStartPoint(player);
                // Start actual motion after move to start point is done:
                this.startMotion = true; // This needs to be set after toggleMoveToStartPoint()
                return;
            }
            // Linear and circle modes don't use the start point stuff, they start the actual motion right away
            // Check if we have all the necessary points defined for the motion to start
            if (this.startMotion(entity))
            {
                // If the interval is not OFF, starting motion mode also starts the recording mode
                if (Configs.getConfig().getInterval() > 0)
                {
                    RecordingHandler.getInstance().startRecording();
                }
            }
        }
        // Stop motion mode
        else
        {
            this.stopMotion();
            State.setPaused(false);
        }
    }

    private void toggleMoveToPoint(EntityPlayer player, MsPoint point)
    {
        EntityPlayer entity = this.getCameraEntity(player);
        int mode = Configs.getConfig().getMotionMode();

        // TODO Ellipse mode and path (smooth) mode
        if (mode != Constants.MOTION_MODE_PATH_LINEAR)
        {
            return;
        }

        // Already active, toggling to disable
        if (this.stateMoveToStart == true)
        {
            State.setMotion(false);
            this.stateMoveToStart = false;
            this.startMotion = false;
            return;
        }
        // Not allowed to activate "move to start" while actual motion is active
        if (State.getMotion() == true)
        {
            return;
        }
        if (this.getPath().getNumPoints() == 0)
        {
            this.addMessage(I18n.format("multishot.gui.message.error.no.path.points.set"));
            return;
        }

        // Motion not active, activating "move to start" mode
        // The per-point camera angle vs. global target point is handled in linearSegmentInit()
        if (this.linearSegmentInit(entity, point, this.getPath().getTarget()) == true)
        {
            this.stateMoveToStart = true;
            State.setMotion(true);
        }
    }

    public void toggleMoveToStartPoint(EntityPlayer player)
    {
        this.toggleMoveToPoint(player, this.getPath().getFirst());
    }

    public void toggleMoveToClosestPoint(EntityPlayer player)
    {
        int nearest = this.getPath().getNearestPointIndex(player);
        this.toggleMoveToPoint(player, this.getPath().getPoint(nearest));
    }

    private void moveEntityLinear(Entity entity)
    {
        if (entity == null)
        {
            Multishot.logger.fatal("movePlayerLinear(): player was null");
            return;
        }

        double mx, my, mz;
        float yaw, pitch;
        Configs mscfg = Configs.getConfig();
        mx = mscfg.getMotionX();
        mz = mscfg.getMotionZ();
        my = mscfg.getMotionY();
        yaw = mscfg.getRotationYaw();
        pitch = mscfg.getRotationPitch();
        //player.setPositionAndUpdate(pos.xCoord + x, pos.yCoord + y, pos.zCoord + z); // Does strange things...
        //player.setVelocity(mx, my, mz); // Doesn't work for values < 0.005
        //Vec3 pos = player.getPosition(1.0f);
        //player.setPositionAndRotation(pos.xCoord + mx, pos.yCoord + my, pos.zCoord + mz, player.rotationYaw + yaw, player.rotationPitch + pitch);
        entity.move(MoverType.SELF, mx, my, mz);
        //p.setPositionAndRotation(p.posX + mx, p.posY + my, p.posZ + mz, p.rotationYaw, p.rotationPitch);
        this.reOrientEntityToAngle(entity, entity.rotationYaw + yaw, entity.rotationPitch + pitch);
    }

    private void moveEntityCircular(Entity entity)
    {
        if (entity == null)
        {
            Multishot.logger.fatal("movePlayerCircular(): player was null");
            return;
        }

        this.circleCurrentAngle += this.circleAngularVelocity;
        double x = this.circleCenter.getX() - Math.sin(this.circleCurrentAngle) * this.circleRadius;
        double z = this.circleCenter.getZ() + Math.cos(this.circleCurrentAngle) * this.circleRadius;
        x = (x - entity.posX);
        z = (z - entity.posZ);

        entity.move(MoverType.SELF, x, 0.0, z);
        //p.setPositionAndRotation(x, p.posY, z, p.rotationYaw, p.rotationPitch);

        // If we have a target point set, re-orient the player to look at the target point
        if (this.getUseTarget())
        {
            this.reOrientEntityToTargetPoint(entity, this.circleTarget);
        }
    }

    private void moveEntityPathSegment(Entity entity, MsPath path)
    {
        // If this segment finished, initialize the next one
        if (this.linearSegmentMove(entity, path.getTarget(), Configs.getConfig().getMotionSpeed()) == true)
        {
            path.incrementPosition();
            this.linearSegmentInit(entity, path.getCurrent(), path.getTarget());
        }
    }

    private void moveEntityToStartPoint(Entity entity)
    {
        // FIXME: Which speed should we use for this movement? Currently set to 5.0 m/s
        if (this.linearSegmentMove(entity, null, 5000))
        {
            this.stateMoveToStart = false;

            if (this.startMotion == true)
            {
                this.startMotion = false;

                // Initialize the path stuff
                this.getPath().resetPosition();
                this.getPath().incrementPosition();
                this.linearSegmentInit(entity, this.getPath().getCurrent(), this.getPath().getTarget());

                // If the interval is not OFF, starting the actual motion mode also starts the recording mode
                if (Configs.getConfig().getInterval() > 0)
                {
                    RecordingHandler.getInstance().startRecording();
                }
            }
            else
            {
                State.setMotion(false);
                Minecraft.getMinecraft().setIngameFocus();
            }
        }
    }

    public void moveCameraEntity(EntityPlayer player)
    {
        synchronized(this)
        {
            Entity entity = this.getCameraEntity(player);

            /*if (Configs.getConfig().getUseFreeCamera())
            {
                entity.ticksExisted++;
            }*/

            if (this.stateMoveToStart)
            {
                this.moveEntityToStartPoint(entity);
                return;
            }

            int mode = this.getMotionMode();
            if (mode == Constants.MOTION_MODE_LINEAR)
            {
                this.moveEntityLinear(entity);
            }
            else if (mode == Constants.MOTION_MODE_CIRCLE)
            {
                this.moveEntityCircular(entity);
            }
            else if (mode == Constants.MOTION_MODE_ELLIPSE)
            {
            }
            else if (mode == Constants.MOTION_MODE_PATH_LINEAR)
            {
                this.moveEntityPathSegment(entity, this.getPath());
            }
            else if (mode == Constants.MOTION_MODE_PATH_SMOOTH)
            {
            }
        }
    }

    public static void setCameraEntityPositionFromPlayer(EntityPlayer camera, EntityPlayer player)
    {
        if (camera != null && player != null && camera != player)
        {
            setCameraEntityPosition(camera, player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch, player.rotationYawHead);
        }
    }

    private static void setCameraEntityPositionFromPoint(EntityPlayer camera, MsPoint point)
    {
        setCameraEntityPosition(camera, point.getX(), point.getY(), point.getZ(), point.getYaw(), point.getPitch(), point.getYaw());
    }

    public static void setCameraEntityPosition(EntityPlayer camera, double x, double y, double z, float yaw, float pitch, float yawHead)
    {
        camera.setLocationAndAngles(x, y, z, yaw, pitch);
        camera.setLocationAndAngles(x, y, z, yaw, pitch);

        camera.prevRotationYaw = yaw;
        camera.prevRotationPitch = pitch;
        camera.prevRotationYawHead = yawHead;
        camera.prevRenderYawOffset = 0f;

        camera.rotationYaw = yaw;
        camera.rotationPitch = pitch;
        camera.setRotationYawHead(yaw);
    }

    public EntityPlayer getCameraEntity(EntityPlayer player)
    {
        if (Configs.getConfig().getUseFreeCamera())
        {
            EntityPlayer camera = RenderEventHandler.instance().getCameraEntity();
            return camera != null ? camera : player;
        }

        return player;
    }
}
