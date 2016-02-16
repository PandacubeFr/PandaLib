package fr.pandacube.java.util.db;

import java.util.UUID;

public class PlayerIgnoreElement extends SQLElement {

	private String ignore;
	private String ignored;
	
	
	public PlayerIgnoreElement(UUID ignore, UUID ignored) {
		super("pandacube_player_ignore");
		setIgnore(ignore);
		setIgnored(ignored);
	}
	
	
	protected PlayerIgnoreElement(int id, String ignore, String ignored) {
		super("pandacube_player_ignore", id);
		this.ignore = ignore;
		this.ignored = ignored;
	}


	@Override
	protected String[] getValues() {
		return new String[] {
				ignore,
				ignored
		};
	}


	@Override
	protected String[] getFieldsName() {
		return new String[] {
				"ignorer",
				"ignored"
		};
	}


	public UUID getIgnore() {
		return UUID.fromString(ignore);
	}


	public void setIgnore(UUID i) {
		if (i == null)
			throw new IllegalArgumentException("i can't be null");
		ignore = i.toString();
	}


	public UUID getIgnored() {
		return UUID.fromString(ignored);
	}


	public void setIgnored(UUID i) {
		if (i == null)
			throw new IllegalArgumentException("i can't be null");
		ignored = i.toString();
	}

}
