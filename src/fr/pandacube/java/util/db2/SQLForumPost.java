package fr.pandacube.java.util.db2;

import java.util.UUID;

import fr.pandacube.java.util.db2.sql_tools.SQLElement;
import fr.pandacube.java.util.db2.sql_tools.SQLFKField;
import fr.pandacube.java.util.db2.sql_tools.SQLField;
import fr.pandacube.java.util.db2.sql_tools.SQLType;

public class SQLForumPost extends SQLElement {

	
	public SQLForumPost() { super(); }
	public SQLForumPost(int id) { super(id); }

	@Override
	protected String tableName() { return "pandacube_forum_post"; }
	
	
	public static final SQLField<String>                    createur = new SQLField<>("createur",  SQLType.CHAR(36), false);
	public static final SQLField<String>                    texte    = new SQLField<>("texte",     SQLType.TEXT,     false);
	public static final SQLField<Integer>                   time     = new SQLField<>("time",      SQLType.INT,      false);
	public static final SQLFKField<Integer, SQLForumThread> threadId = SQLFKField.idFK("threadId", SQLType.INT,      false, SQLForumThread.class);
	public static final SQLField<Boolean>                  moderated = new SQLField<>("moderated", SQLType.BOOLEAN,  false);
	
	
	
	public UUID getCreateurId() {
		String id = (String)get(createur);
		return (id == null) ? null : UUID.fromString(id);
	}
	
	
	public void setCreateurId(UUID pName) {
		set(createur, (pName == null) ? (String)null : pName.toString());
	}
	

}
