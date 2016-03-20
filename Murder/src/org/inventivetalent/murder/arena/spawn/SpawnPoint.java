/*
 * Copyright 2013-2016 inventivetalent. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and contributors and should not be interpreted as representing official policies,
 *  either expressed or implied, of anybody else.
 */

package org.inventivetalent.murder.arena.spawn;

import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.World;

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

	public SpawnPoint(JsonObject jsonObject) {
		this.x = jsonObject.get("x").getAsDouble();
		this.y = jsonObject.get("y").getAsDouble();
		this.z = jsonObject.get("z").getAsDouble();
		this.type = SpawnType.valueOf(jsonObject.get("type").getAsString());
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
