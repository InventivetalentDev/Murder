package org.inventivetalent.murder.arena.spawn;

import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class SpawnPoint {

	public final double    x;
	public final double    y;
	public final double    z;
	public final SpawnType type;

	public SpawnPoint(double x, double y, double z, SpawnType type) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
	}

	public SpawnPoint(Vector vector, SpawnType type) {
		this(vector.getX(), vector.getY(), vector.getZ(), type);
	}

	public SpawnPoint(JsonObject jsonObject) {
		this.x = jsonObject.get("x").getAsDouble();
		this.y = jsonObject.get("y").getAsDouble();
		this.z = jsonObject.get("z").getAsDouble();
		this.type = SpawnType.valueOf(jsonObject.get("type").getAsString());
	}

	public Vector getVector() {
		return new Vector(x, y, z);
	}

	public Location getLocation(World world) {
		return new Location(world, x, y, z);
	}

	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty("x", x);
		jsonObject.addProperty("y", y);
		jsonObject.addProperty("z", z);
		jsonObject.addProperty("type", type.name());

		return jsonObject;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }

		SpawnPoint that = (SpawnPoint) o;

		if (Double.compare(that.x, x) != 0) { return false; }
		if (Double.compare(that.y, y) != 0) { return false; }
		if (Double.compare(that.z, z) != 0) { return false; }
		return type == that.type;

	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		result = 31 * result + (type != null ? type.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "SpawnPoint{" +
				"x=" + x +
				", y=" + y +
				", z=" + z +
				", type=" + type +
				'}';
	}
}
