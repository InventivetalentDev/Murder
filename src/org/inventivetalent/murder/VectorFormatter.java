package org.inventivetalent.murder;

import org.bukkit.util.Vector;
import org.inventivetalent.pluginannotations.message.MessageFormatter;

import java.util.concurrent.Callable;

public class VectorFormatter extends MessageFormatter {

	private Callable<Vector> callable;

	public VectorFormatter(Callable<Vector> callable) {
		this.callable = callable;
	}

	public VectorFormatter(final Vector vector) {
		this.callable = new Callable<Vector>() {
			@Override
			public Vector call() throws Exception {
				return vector;
			}
		};
	}

	@Override
	public String format(String key, String message) {
		try {
			if (callable != null) {
				Vector vector = callable.call();
				if (vector != null) {
					return String.format(message, vector.getX(), vector.getY(), vector.getZ());
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return super.format(key, message);
	}
}
