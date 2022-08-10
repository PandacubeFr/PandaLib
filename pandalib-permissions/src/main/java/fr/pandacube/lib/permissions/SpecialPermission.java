package fr.pandacube.lib.permissions;

/**
 * Represents a permission node that is based on an arbitrary player state.
 * @param matcher predicate that tell if the provided permission is our special permission.
 * @param tester predicate that tell the value of this special permission, based on the parameters.
 */
public record SpecialPermission(PermissionMatcher matcher, PermissionTester tester) {

	/**
	 * Predicate that tell if the provided permission is our special permission.
	 */
	public interface PermissionMatcher {
		/**
		 * Tells if the provided permission is our special permission.
		 * @param permission the permission to test.
		 * @return true if the provided permission is our special permission, false otherwise.
		 */
		boolean match(String permission);
	}

	/**
	 * Predicate that tell the value of this special permission, based on the parameters.
	 */
	public interface PermissionTester {
		/**
		 * Tells the value of this special permission, based on the parameters.
		 * @param player the player to test the permission on.
		 * @param permission the permission to test.
		 * @param server the server on which the player is.
		 * @param world the world in which the player is.
		 * @return the value of this special permission, based on the parameters.
		 */
		boolean test(PermPlayer player, String permission, String server, String world);
	}
	
}
