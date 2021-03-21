package fr.pandacube.lib.core.permissions;

import java.util.Comparator;
import java.util.Objects;

public class ServerWorldKey implements Comparable<ServerWorldKey> {
	public final String server, world;
	ServerWorldKey(String s, String w) {
		server = s; world = w;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ServerWorldKey))
			return false;
		ServerWorldKey o = (ServerWorldKey) obj;
		return Objects.equals(server, o.server)
				&& Objects.equals(world, o.world);
	}
	@Override
	public int hashCode() {
		return Objects.hash(world, server);
	}
	@Override
	public int compareTo(ServerWorldKey o) {
		Comparator<String> compStrNullFirst = Comparator.nullsFirst(String::compareToIgnoreCase);
		return Comparator.comparing((ServerWorldKey k) -> k.server, compStrNullFirst)
				.thenComparing(k -> k.world, compStrNullFirst)
				.compare(this, o);
	}
}