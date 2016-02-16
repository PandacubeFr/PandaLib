package fr.pandacube.java.util.db;

import java.sql.SQLException;
import java.util.UUID;

public class MPGroupUserElement extends SQLElement {
	
	private int groupId;
	private String playerId;

	
	public MPGroupUserElement(int gId, UUID pId) {
		super("pandacube_mp_group_user");
		setGroupId(gId);
		setPlayerId(pId);
	}
	
	protected MPGroupUserElement(int id, int gId, String pId) {
		super("pandacube_mp_group_user", id);
		setGroupId(gId);
		setPlayerId(UUID.fromString(pId));
	}

	
	
	@Override
	protected String[] getValues() {
		return new String[] {
				Integer.toString(groupId),
				playerId
		};
	}

	@Override
	protected String[] getFieldsName() {
		return new String[] {
				"groupId",
				"playerId"
		};
	}
	
	
	
	
	
	
	
	public int getGroupId() { return groupId; }
	public UUID getPlayerId() { return UUID.fromString(playerId); }

	public void setGroupId(int gId) { groupId = gId; }
	public void setPlayerId(UUID pId) {
		if (pId == null)
			throw new NullPointerException();
		this.playerId = pId.toString();
	}
	
	
	
	
	
	
	public PlayerElement getPlayerElement() throws SQLException {
		return ORM.getTable(PlayerTable.class)
				.getFirst("playerId LIKE '"+getPlayerId()+"'", "id ASC");
	}
	
	
	

}
