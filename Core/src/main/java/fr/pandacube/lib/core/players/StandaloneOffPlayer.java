package fr.pandacube.lib.core.players;

import java.util.UUID;

/* package */ class StandaloneOffPlayer implements IOffPlayer {
	
	private final UUID uniqueId;
	
	public StandaloneOffPlayer(UUID id) {
		if (id == null) throw new IllegalArgumentException("id cannot be null");
		uniqueId = id;
	}

	@Override
	public UUID getUniqueId() {
		return uniqueId;
	}

	@Override
	public boolean isOnline() {
		return false;
	}

	@Override
	public IOnlinePlayer getOnlineInstance() {
		return null;
	}
	
	private String displayName = null;
	
	@Override
	public String getDisplayName() {
		if (displayName == null)
			displayName = getDisplayNameFromPermissionSystem();
		return displayName;
	}

}
