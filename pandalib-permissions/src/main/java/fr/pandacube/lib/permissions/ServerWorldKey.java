package fr.pandacube.lib.permissions;

import java.util.Comparator;
import java.util.Objects;

/**
 * A pair of string representing a server and world name, used to organize and filter the permission data of a player or
 * group.
 * @param server the server name, can be null.
 * @param world the world name, can be null.
 */
public record ServerWorldKey(String server, String world) implements Comparable<ServerWorldKey> {

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ServerWorldKey(String oServer, String oWorld)
				&& Objects.equals(server, oServer)
				&& Objects.equals(world, oWorld);
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