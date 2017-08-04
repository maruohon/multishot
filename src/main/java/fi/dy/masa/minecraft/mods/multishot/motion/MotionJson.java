package fi.dy.masa.minecraft.mods.multishot.motion;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fi.dy.masa.minecraft.mods.multishot.Multishot;
import fi.dy.masa.minecraft.mods.multishot.config.Configs;
import fi.dy.masa.minecraft.mods.multishot.motion.Motion.MsPath;
import fi.dy.masa.minecraft.mods.multishot.motion.Motion.MsPoint;

public class MotionJson
{
    private Motion motion;

    public MotionJson(Motion motion)
    {
        this.motion = motion;
        File dir = Configs.getPointsDir();

        if (dir.exists() == false || dir.isDirectory() == false)
        {
            try
            {
                dir.mkdirs();
            }
            catch (Exception e)
            {
                Multishot.logger.fatal("Error creating the directories for the point files: " + e.getMessage());
            }
        }
    }

    private JsonObject readJsonFile(File file)
    {
        if (file.exists() && file.canRead())
        {
            try
            {
                JsonElement el = new JsonParser().parse(new FileReader(file));

                if (el.isJsonObject())
                {
                    return el.getAsJsonObject();
                }
            }
            catch (IOException e)
            {
                Multishot.logger.warn("Warning: Could not read file '{}'", file.getAbsolutePath(), e);
                return null;
            }
            catch (Exception e)
            {
                Multishot.logger.warn("Warning: Exception while trying to read JSON from file '{}' ", file.getAbsolutePath(), e);
                return null;
            }
        }
        return null;
    }

    private MsPoint getPointFromJson(JsonObject json)
    {
        double x, z, y;
        float yaw, pitch;

        if (json == null)
        {
            return null;
        }

        try
        {
            x = json.get("x").getAsDouble();
            y = json.get("y").getAsDouble();
            z = json.get("z").getAsDouble();
            yaw = json.get("yaw").getAsFloat();
            pitch = json.get("pitch").getAsFloat();
        }
        catch (Exception e)
        {
            Multishot.logger.warn("Error reading points from JSON object: " + e.getMessage());
            return null;
        }

        return this.motion.new MsPoint(x, z, y, yaw, pitch);
    }

    private MsPoint getPointFromJson(String name, JsonObject json)
    {
        if (json == null)
        {
            return null;
        }

        JsonElement el = json.get(name);

        if (el == null || el.isJsonObject() == false)
        {
            return null;
        }

        return this.getPointFromJson(el.getAsJsonObject());
    }

    public boolean readPathPointsFromFile(int pathId)
    {
        File pointFile = new File(Configs.getPointsDir(), String.format("path_points_%d.json", pathId + 1));
        JsonObject json = this.readJsonFile(pointFile);
        MsPath path = this.motion.getPath(pathId);

        if (json == null)
        {
            return false;
        }

        path.clearPath();
        path.setTarget(this.getPointFromJson("target", json));

        JsonElement el = json.get("reverse");

        if (el != null && el.isJsonPrimitive())
        {
            path.setReverse(el.getAsBoolean());
        }

        el = json.get("points");

        if (el == null || el.isJsonArray() == false)
        {
            return false;
        }

        JsonArray arr = el.getAsJsonArray();

        int len = arr.size();
        MsPoint p;

        for (int i = 0; i < len; i++)
        {
            el = arr.get(i);

            if (el == null || el.isJsonObject() == false)
            {
                break;
            }

            p = this.getPointFromJson(el.getAsJsonObject());

            if (p == null)
            {
                break;
            }

            path.addPoint(p);
        }

        return true;
    }

    public void readAllPointsFromFile()
    {
        JsonObject json = this.readJsonFile(new File(Configs.getPointsDir(), "generic_points.json"));

        this.motion.setCircleCenter(this.getPointFromJson("circleCenter", json));
        this.motion.setCircleTarget(this.getPointFromJson("circleTarget", json));
        this.motion.setEllipseCenter(this.getPointFromJson("ellipseCenter", json));
        this.motion.setEllipseTarget(this.getPointFromJson("ellipseTarget", json));
        //this.ellipsePointA = this.getPointFromJson("ellipsePointA", json);
        //this.ellipsePointB = this.getPointFromJson("ellipsePointB", json);

        // Read paths from files
        for (int i = 0; i < 9; i++)
        {
            this.readPathPointsFromFile(i);
        }

        if (json != null)
        {
            JsonElement el = json.get("activePath");

            if (el != null && el.isJsonPrimitive())
            {
                int id = el.getAsInt() - 1;
                if (id >= 0 && id <= 9)
                {
                    this.motion.setActivePath(id);
                }
            }
        }
    }

    private JsonObject createPointAsJson(MsPoint p)
    {
        if (p == null)
        {
            return null;
        }

        JsonObject point = new JsonObject();
        point.addProperty("x", p.getX());
        point.addProperty("y", p.getY());
        point.addProperty("z", p.getZ());
        point.addProperty("yaw", p.getYaw());
        point.addProperty("pitch", p.getPitch());
        return point;
    }

    public void savePointsToFile()
    {
        File file = new File(Configs.getPointsDir(), "generic_points.json");

        if (file.exists() == false)
        {
            try
            {
                file.createNewFile();
            }
            catch (IOException e)
            {
                Multishot.logger.warn("Error: Could not create file '{}'", file.getAbsolutePath());
            }
        }

        if (file.canWrite())
        {
            JsonObject json = new JsonObject();

            json.addProperty("activePath", this.motion.getPathIndex() + 1);
            json.add("circleCenter", this.createPointAsJson(this.motion.getCircleCenter()));
            json.add("circleTarget", this.createPointAsJson(this.motion.getCircleTarget()));
            json.add("ellipseCenter", this.createPointAsJson(this.motion.getEllipseCenter()));
            json.add("ellipseTarget", this.createPointAsJson(this.motion.getEllipseTarget()));
            //this.addPointToJson(json, "circleCenter", this.motion.getCircleCenter());
            //this.addPointToJson(json, "circleTarget", this.motion.getCircleTarget());
            //this.addPointToJson(json, "ellipseCenter", this.motion.getEllipseCenter());
            //this.addPointToJson(json, "ellipseTarget", this.motion.getEllipseTarget());
            //this.addPointToJson(json, "ellipsePointA", this.motion.getEllipsePointA());
            //this.addPointToJson(json, "ellipsePointB", this.motion.getEllipsePointB());

            try
            {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                FileWriter writer = new FileWriter(file);
                writer.write(gson.toJson(json));
                writer.close();
            }
            catch (IOException e)
            {
                Multishot.logger.warn("Error: Could not write points to file '{}'", file.getAbsolutePath());
            }
        }
    }

    public void savePathToFile(int id)
    {
        File file = new File(Configs.getPointsDir(), String.format("path_points_%d.json", id + 1));

        if (file.exists() == false)
        {
            try
            {
                file.createNewFile();
            }
            catch (IOException e)
            {
                Multishot.logger.warn("Error: Could not create file '{}'", file.getAbsolutePath());
            }
        }

        if (file.canWrite())
        {
            JsonObject json = new JsonObject();
            JsonArray points = new JsonArray();

            MsPath path = this.motion.getPath(id);
            json.addProperty("reverse", path.getReverse());
            json.add("target", this.createPointAsJson(path.getTarget()));

            MsPoint p;
            int len = path.getNumPoints();

            for(int i = 0; i < len; i++)
            {
                p = path.getPoint(i);

                if (p == null)
                {
                    break;
                }

                points.add(this.createPointAsJson(path.getPoint(i)));
            }

            json.add("points", points);

            try
            {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                FileWriter writer = new FileWriter(file);
                writer.write(gson.toJson(json));
                writer.close();
            }
            catch (IOException e)
            {
                Multishot.logger.warn("Error: Could not write path to file '{}'", file.getAbsolutePath());
            }
        }
    }
}
