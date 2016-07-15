package fr.pandacube.java.util.db;

import java.util.UUID;

import fr.pandacube.java.util.db.sql_tools.SQLElement;
import fr.pandacube.java.util.db.sql_tools.SQLFKField;
import fr.pandacube.java.util.db.sql_tools.SQLField;
import fr.pandacube.java.util.db.sql_tools.SQLType;

public class SQLForumPost extends SQLElement<SQLForumPost> {

	public SQLForumPost() {
		super();
	}

	public SQLForumPost(int id) {
		super(id);
	}

	@Override
	protected String tableName() {
		return "pandacube_forum_post";
	}

	public static final SQLField<SQLForumPost, String> createur = new SQLField<>("createur", SQLType.CHAR(36), false);
	public static final SQLField<SQLForumPost, String> texte = new SQLField<>("texte", SQLType.TEXT, false);
	public static final SQLField<SQLForumPost, Integer> time = new SQLField<>("time", SQLType.INT, false);
	public static final SQLFKField<SQLForumPost, Integer, SQLForumThread> threadId = SQLFKField.idFK("threadId", SQLType.INT, false,
			SQLForumThread.class);
	public static final SQLField<SQLForumPost, Boolean> moderated = new SQLField<>("moderated", SQLType.BOOLEAN, false);

	public UUID getCreateurId() {
		String id = get(createur);
		return (id == null) ? null : UUID.fromString(id);
	}

	public void setCreateurId(UUID pName) {
		set(createur, (pName == null) ? (String) null : pName.toString());
	}

}
