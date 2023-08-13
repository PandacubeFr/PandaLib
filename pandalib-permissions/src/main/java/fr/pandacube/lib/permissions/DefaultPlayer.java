package fr.pandacube.lib.permissions;

import fr.pandacube.lib.util.Log;

import java.util.UUID;

/**
 * Represents a dummy player in the permission system, that have no specific data, only inheriting from the default
 * groups.
 *
 * The current implementation provides a player named {@code default.0} with an uuid of
 * {@code fffdef17-ffff-b0ff-ffff-ffffffffffff}.
 * Trying to set a permission data for this player will log a warning.
 */
/* package */ final class DefaultPlayer extends PermPlayer {


	// a static UUID that ensure it will not collide with any player, either online or offline or floodgate:
	// the version bits are set to B (11), that is offline mode (3) + alt (8) account, and alt account counter
	// set to 0 that is usually impossible due to the counter starting at 1.
	/* package */ static final UUID ID = new UUID(0xfffdef17_ffff_b0ffL, -1L);

	/* package */ static final String NAME = "default.0";


	/* package */ DefaultPlayer() {
		super(ID);
	}

	@Override
	public String getName() {
		return NAME;
	}

	public void setGroup(String group) {
		warnDefaultPlayerSetData();
	}

	public void addGroup(String group) {
		warnDefaultPlayerSetData();
	}

	public void removeGroup(String group) {
		warnDefaultPlayerSetData();
	}

	@Override
	public void setSelfPrefix(String prefix) {
		warnDefaultPlayerSetData();
	}

	@Override
	public void setSelfSuffix(String suffix) {
		warnDefaultPlayerSetData();
	}

	@Override
	public void addSelfPermission(String permission, String server, String world) {
		warnDefaultPlayerSetData();
	}

	@Override
	public void removeSelfPermission(String permission, String server, String world) {
		warnDefaultPlayerSetData();
	}

	private void warnDefaultPlayerSetData() {
		Log.warning(new UnsupportedOperationException("Trying to set permission data of default player"));
	}
}