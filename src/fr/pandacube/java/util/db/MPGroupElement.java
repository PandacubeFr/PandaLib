package fr.pandacube.java.util.db;

import java.sql.SQLException;
import java.util.List;

import fr.pandacube.java.util.PlayerFinder;

public class MPGroupElement extends SQLElement {
	
	private String groupName;
	
	
	
	public MPGroupElement(String name) {
		super("pandacube_mp_group");
		setGroupName(name);
	}
	
	protected MPGroupElement(int id, String name) {
		super("pandacube_mp_group", id);
		setGroupName(name);
	}
	
	
	
	
	
	

	@Override
	protected String[] getValues() {
		return new String[] {
				groupName
		};
	}

	@Override
	protected String[] getFieldsName() {
		return new String[] {
				"groupName"
		};
	}

	
	
	public String getGroupName() { return groupName; }

	public void setGroupName(String name) {
		if (name == null)
			throw new NullPointerException();
		if (!PlayerFinder.isValidPlayerName(name))
			throw new IllegalArgumentException("Le nom d'un groupe doit respecter le pattern d'un pseudo valide");
		groupName = name;
	}
	
	
	
	public List<MPGroupUserElement> getUsers() throws SQLException {
		return ORM.getTable(MPGroupUserTable.class)
				.getAll("groupId = "+getId(), "id ASC", null, null);
	}
	
}
