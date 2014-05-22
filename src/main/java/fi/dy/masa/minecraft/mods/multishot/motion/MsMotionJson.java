package fi.dy.masa.minecraft.mods.multishot.motion;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cpw.mods.fml.common.FMLLog;
import fi.dy.masa.minecraft.mods.multishot.libs.MsStringHelper;
import fi.dy.masa.minecraft.mods.multishot.motion.MsMotion.MsPath;
import fi.dy.masa.minecraft.mods.multishot.motion.MsMotion.MsPoint;
import fi.dy.masa.minecraft.mods.multishot.state.MsClassReference;

public class MsMotionJson
{
	private MsMotion motion;

	public MsMotionJson(MsMotion m)
	{
		this.motion = m;
	}

	private JsonObject readJsonFile(String name)
	{
		File file = new File(name);

		if (file.exists() == true && file.canRead() == true)
		{
			String jsonStr;
			JsonElement jsonEl;
			try
			{
				jsonStr = FileUtils.readFileToString(file);
				JsonElement el = new JsonParser().parse(jsonStr);
				if (el.isJsonObject() == true)
				{
					return el.getAsJsonObject();
				}
			}
			catch (IOException e)
			{
				FMLLog.warning("Warning: Could not read file '" + name + "'" + e.getMessage());
				return null;
			}
			catch (Exception e)
			{
				FMLLog.warning("Warning: Exception while trying to read JSON from file '" + name + "' " + e.getMessage());
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
			z = json.get("z").getAsDouble();
			y = json.get("y").getAsDouble();
			yaw = json.get("yaw").getAsFloat();
			pitch = json.get("pitch").getAsFloat();
		}
		catch (Exception e)
		{
			FMLLog.severe("Error reading points from JSON object: " + e.getMessage());
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

	public boolean readPathPointsFromFile(int id)
	{
		String filePath = MsClassReference.getMsConfigs().getSavePath();
		String fileName = MsStringHelper.fixPath(filePath.concat("/").concat(String.format("path_points_%d.txt", id + 1)));
		JsonObject json = this.readJsonFile(fileName);
		MsPath path = this.motion.getPath(id);

		if (json == null)
		{
			return false;
		}

		path.clearPath();
		path.setTarget(this.getPointFromJson("target", json));

		JsonElement el = json.get("reverse");
		if (el != null && el.isJsonPrimitive() == true)
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
		String path = MsClassReference.getMsConfigs().getSavePath();
		String genericPointsFileName = MsStringHelper.fixPath(path.concat("/").concat("generic_points.txt"));
		JsonObject json = this.readJsonFile(genericPointsFileName);

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
			if (el != null && el.isJsonPrimitive() == true)
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
		point.addProperty("z", p.getZ());
		point.addProperty("y", p.getY());
		point.addProperty("yaw", p.getYaw());
		point.addProperty("pitch", p.getPitch());
		return point;
	}

	public void savePointsToFile()
	{
		String filePath = MsClassReference.getMsConfigs().getSavePath();
		String name = MsStringHelper.fixPath(filePath.concat("/").concat("generic_points.txt"));
		File file = new File(name);

		if (file.exists() == false)
		{
			try
			{
				file.createNewFile();
			}
			catch (IOException e)
			{
				FMLLog.warning("Error: Could not create file '" + name + "'");
			}
		}

		if (file.canWrite() == true)
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
				FileUtils.writeStringToFile(file, gson.toJson(json));
			}
			catch (IOException e)
			{
				FMLLog.warning("Error: Could not write points to file '" + name + "'");
			}
		}
	}

	public void savePathToFile(int id)
	{
		String filePath = MsClassReference.getMsConfigs().getSavePath();
		String name = MsStringHelper.fixPath(filePath.concat("/").concat(String.format("path_points_%d.txt", id + 1)));
		File file = new File(name);

		if (file.exists() == false)
		{
			try
			{
				file.createNewFile();
			}
			catch (IOException e)
			{
				FMLLog.warning("Error: Could not create file '" + name + "'");
			}
		}

		if (file.canWrite() == true)
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
				FileUtils.writeStringToFile(file, gson.toJson(json));
			}
			catch (IOException e)
			{
				FMLLog.warning("Error: Could not write path to file '" + name + "'");
			}
		}
	}
}
