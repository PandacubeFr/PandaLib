package fr.pandacube.java.util.db2;

import java.util.UUID;

import fr.pandacube.java.util.db2.sql_tools.SQLElement;
import fr.pandacube.java.util.db2.sql_tools.SQLField;
import fr.pandacube.java.util.db2.sql_tools.SQLType;

public class SQLPlayerIgnore extends SQLElement {
	
	
	public SQLPlayerIgnore() { super(); }
	public SQLPlayerIgnore(int id) { super(id); }

	@Override
	protected String tableName() { return "pandacube_player_ignore"; }
	
	
	public static final SQLField<String> ignorer = new SQLField<>("ignorer", SQLType.CHAR(36), false);
	public static final SQLField<String> ignored = new SQLField<>("ignored", SQLType.CHAR(36), false);
	

	
	public UUID getIgnorerId() {
		String id = (String)get(ignorer);
		return (id == null) ? null : UUID.fromString(id);
	}
	
	
	public void setIgnorerId(UUID pName) {
		set(ignorer, (pName == null) ? (String)null : pName.toString());
	}
	

	
	public UUID getIgnoredId() {
		String id = (String)get(ignored);
		return (id == null) ? null : UUID.fromString(id);
	}
	
	
	public void setIgnoredId(UUID pName) {
		set(ignored, (pName == null) ? (String)null : pName.toString());
	}
	
	
	
}
