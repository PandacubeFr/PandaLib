package fr.pandacube.java.util.db2;

import java.util.UUID;

import fr.pandacube.java.util.db2.sql_tools.SQLElement;
import fr.pandacube.java.util.db2.sql_tools.SQLField;
import fr.pandacube.java.util.db2.sql_tools.SQLType;

public class SQLForumThread extends SQLElement {

	
	public SQLForumThread() { super(); }
	public SQLForumThread(int id) { super(id); }

	@Override
	protected String tableName() { return "pandacube_forum_thread"; }
	
	
	public static final SQLField<Integer> forumId = new SQLField<>("forumId", SQLType.INT, false);
	public static final SQLField<String> titre = new SQLField<>("titre", SQLType.VARCHAR(60), false);
	public static final SQLField<String> createur = new SQLField<>("createur", SQLType.CHAR(36), false);
	public static final SQLField<Integer> vu = new SQLField<>("vu", SQLType.INT, false);
	public static final SQLField<Long> time = new SQLField<>("time", SQLType.BIGINT, false);
	public static final SQLField<Boolean> anchored = new SQLField<>("anchored", SQLType.BOOLEAN, false);
	public static final SQLField<Boolean> locked = new SQLField<>("locked", SQLType.BOOLEAN, false);
	public static final SQLField<Integer> nbMessages = new SQLField<>("nbMessages", SQLType.INT, false);
	
 

	public UUID getCreateurId() {
		String id = (String)get(createur);
		return (id == null) ? null : UUID.fromString(id);
	}
	
	
	public void setCreateurId(UUID pName) {
		set(createur, (pName == null) ? (String)null : pName.toString());
	}
	
	
	
	
}
