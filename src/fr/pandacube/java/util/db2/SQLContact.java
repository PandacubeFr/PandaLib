package fr.pandacube.java.util.db2;

import java.util.UUID;

import fr.pandacube.java.util.db2.sql_tools.SQLElement;
import fr.pandacube.java.util.db2.sql_tools.SQLField;
import fr.pandacube.java.util.db2.sql_tools.SQLFKField;
import fr.pandacube.java.util.db2.sql_tools.SQLType;

public class SQLContact extends SQLElement {
	
	public SQLContact() { super(); }
	public SQLContact(int id) { super(id); }
	
	@Override
	protected String tableName() { return "pandacube_contact"; }
	
	
	
	
	
	public static final SQLField<Integer> time                  = new SQLField<>(  "time",     SQLType.INT,          false);
	public static final SQLFKField<String, SQLPlayer>  playerId = new SQLFKField<>("playerId", SQLType.CHAR(36),     true, SQLPlayer.class, SQLPlayer.playerId);
	public static final SQLField<String>  userName              = new SQLField<>(  "userName", SQLType.VARCHAR(50),  true);
	public static final SQLField<String>  userMail              = new SQLField<>(  "userMail", SQLType.VARCHAR(50),  true);
	public static final SQLField<String>  titre                 = new SQLField<>(  "titre",    SQLType.VARCHAR(100), false);
	public static final SQLField<String>  texte                 = new SQLField<>(  "texte",    SQLType.TEXT,         false);
	public static final SQLField<Boolean> hidden                = new SQLField<>(  "hidden",   SQLType.BOOLEAN,      false, (Boolean)false);
	
	
	
	public UUID getPlayerId() {
		String id = (String)get(playerId);
		return (id == null) ? null : UUID.fromString(id);
	}
	
	
	public void setPlayerId(UUID pName) {
		set(playerId, (pName == null) ? (String)null : pName.toString());
	}
	
	
	
}
