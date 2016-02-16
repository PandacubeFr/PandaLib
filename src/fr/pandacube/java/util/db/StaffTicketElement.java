package fr.pandacube.java.util.db;

import java.util.UUID;

public class StaffTicketElement extends SQLElement {
	

	private String playerId;
	private String message;
	private long creationTime;
	private String staffPlayerId = null;

	
	public StaffTicketElement(UUID pId, String m, long creaTime) {
		super("pandacube_staff_ticket");
		setPlayerId(pId);
		setMessage(m);
		setCreationTime(creaTime);
		
	}
	protected StaffTicketElement(int id, String pId, String m, long creaTime) {
		super("pandacube_staff_ticket", id);
		setPlayerId(UUID.fromString(pId));
		setMessage(m);
		setCreationTime(creaTime);
	}
	

	@Override
	protected String[] getValues() {
		return new String[] {
				playerId,
				message,
				Long.toString(creationTime),
				staffPlayerId,
		};
	}

	@Override
	protected String[] getFieldsName() {
		return new String[] {
				"playerId",
				"message",
				"creationTime",
				"staffPlayerId"
		};
	}
	public UUID getPlayerId() {
		return UUID.fromString(playerId);
	}
	public void setPlayerId(UUID pId) {
		if (pId == null) throw new IllegalArgumentException("playerName can't be null");
		this.playerId = pId.toString();
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		if (message == null) throw new IllegalArgumentException("message can't be null");
		this.message = message;
	}
	public long getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}
	public UUID getStaffPlayer() {
		if (staffPlayerId == null) return null;
		return UUID.fromString(staffPlayerId);
	}
	public void setStaffPlayer(UUID staffId) {
		if (staffId == null) staffPlayerId = null;
		else staffPlayerId = staffId.toString();
	}

}
