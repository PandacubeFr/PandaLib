package fr.pandacube.java.util.db;

import java.util.UUID;

import fr.pandacube.java.util.db.sql_tools.SQLElement;
import fr.pandacube.java.util.db.sql_tools.SQLFKField;
import fr.pandacube.java.util.db.sql_tools.SQLField;
import fr.pandacube.java.util.db.sql_tools.SQLType;

public class SQLForumThread extends SQLElement<SQLForumThread> {

	public SQLForumThread() {
		super();
	}

	public SQLForumThread(int id) {
		super(id);
	}

	@Override
	protected String tableName() {
		return "pandacube_forum_thread";
	}

	public static final SQLFKField<SQLForumThread, Integer, SQLForumForum> forumId = SQLFKField.idFK("forumId", SQLType.INT, false,
			SQLForumForum.class);
	public static final SQLField<SQLForumThread, String> titre = new SQLField<>("titre", SQLType.VARCHAR(60), false);
	public static final SQLFKField<SQLForumThread, String, SQLPlayer> createur = new SQLFKField<>("createur", SQLType.CHAR(36), false,
			SQLPlayer.playerId);
	public static final SQLField<SQLForumThread, Integer> vu = new SQLField<>("vu", SQLType.INT, false);
	public static final SQLField<SQLForumThread, Long> time = new SQLField<>("time", SQLType.BIGINT, false);
	public static final SQLField<SQLForumThread, Boolean> anchored = new SQLField<>("anchored", SQLType.BOOLEAN, false);
	public static final SQLField<SQLForumThread, Boolean> locked = new SQLField<>("locked", SQLType.BOOLEAN, false);
	public static final SQLField<SQLForumThread, Integer> nbMessages = new SQLField<>("nbMessages", SQLType.INT, false);

	public UUID getCreateurId() {
		String id = get(createur);
		return (id == null) ? null : UUID.fromString(id);
	}

	public void setCreateurId(UUID pName) {
		set(createur, (pName == null) ? (String) null : pName.toString());
	}

}
