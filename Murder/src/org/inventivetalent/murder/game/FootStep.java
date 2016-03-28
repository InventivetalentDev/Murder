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

package org.inventivetalent.murder.game;

import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.inventivetalent.particle.ParticleEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class FootStep {

	public final UUID   uuid;
	public final Vector vector;
	public final Color  color;

	int fadeTimeout = 20;

	public FootStep(UUID uuid, Vector vector, Color color) {
		this.uuid = uuid;
		this.vector = vector;
		this.color = color;
	}

	public void display(Player receiver) {
		if (receiver == null || !receiver.isOnline()) { return; }

		Collection<Player> singleton = Collections.singleton(receiver);
		ParticleEffect.REDSTONE.sendColor(singleton, vector.getX(), vector.getY() + .025, vector.getZ(), color);
		if (fadeTimeout++ >= 16) {
			fadeTimeout=0;
			ParticleEffect.FOOTSTEP.send(singleton, vector.getX(), vector.getY() + .01, vector.getZ(), 0, 0, 0, 0, 1);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }

		FootStep footStep = (FootStep) o;

		if (vector != null ? !vector.equals(footStep.vector) : footStep.vector != null) { return false; }
		return color != null ? color.equals(footStep.color) : footStep.color == null;

	}

	@Override
	public int hashCode() {
		int result = vector != null ? vector.hashCode() : 0;
		result = 31 * result + (color != null ? color.hashCode() : 0);
		return result;
	}
}
