package fr.pandacube.lib.permissions;

/**
 * Represents a permission node that is based on an arbitrary player state.
 */
public record SpecialPermission(PermissionMatcher match, PermissionTester tester) {
	
	public interface PermissionMatcher {
		boolean match(String permission);
	}
	
	public interface PermissionTester {
		boolean test(PermPlayer player, String permission, String server, String world);
	}
	
}
