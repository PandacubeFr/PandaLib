package fr.pandacube.lib.permissions;

import java.util.Comparator;
import java.util.Objects;

public class ServerWorldKey implements Comparable<ServerWorldKey> {
	public final String server, world;
	ServerWorldKey(String s, String w) {
		server = s; world = w;
	}
	@Override
	public boolean equals(Object obj) {
		return obj instanceof ServerWorldKey o
				&& Objects.equals(server, o.server)
				&& Objects.equals(world, o.world);
	}
	@Override
	public int hashCode() {
		return Objects.hash(world, server);
	}
	
	private static final Comparator<String> STR_NULL_FIRST_COMPARATOR = Comparator.nullsFirst(String::compareToIgnoreCase);
	@Override
	public int compareTo(ServerWorldKey o) {
		return Comparator.comparing((ServerWorldKey k) -> k.server, STR_NULL_FIRST_COMPARATOR)
				.thenComparing(k -> k.world, STR_NULL_FIRST_COMPARATOR)
				.compare(this, o);
	}
}