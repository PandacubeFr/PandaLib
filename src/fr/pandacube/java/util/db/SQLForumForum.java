package fr.pandacube.java.util.db;

import fr.pandacube.java.util.db.sql_tools.SQLElement;
import fr.pandacube.java.util.db.sql_tools.SQLFKField;
import fr.pandacube.java.util.db.sql_tools.SQLField;
import fr.pandacube.java.util.db.sql_tools.SQLType;

public class SQLForumForum extends SQLElement<SQLForumForum> {

	public SQLForumForum() {
		super();
	}

	public SQLForumForum(int id) {
		super(id);
	}

	@Override
	protected String tableName() {
		return "pandacube_forum_forum";
	}

	public static final SQLFKField<SQLForumForum, Integer, SQLForumCategorie> catId = SQLFKField.idFK("catId", SQLType.INT, false,
			SQLForumCategorie.class);
	public static final SQLField<SQLForumForum, String> nom = new SQLField<>("nom", SQLType.VARCHAR(100), false);
	public static final SQLField<SQLForumForum, String> description = new SQLField<>("description", SQLType.TEXT, false);
	public static final SQLField<SQLForumForum, Integer> ordre = new SQLField<>("ordre", SQLType.INT, false);
	public static final SQLField<SQLForumForum, Integer> authView = new SQLField<>("authView", SQLType.INT, false);
	public static final SQLField<SQLForumForum, Integer> authPost = new SQLField<>("authPost", SQLType.INT, false);
	public static final SQLField<SQLForumForum, Integer> authThread = new SQLField<>("authThread", SQLType.INT, false);
	public static final SQLField<SQLForumForum, Integer> authAnchored = new SQLField<>("authAnchored", SQLType.INT, false);
	public static final SQLField<SQLForumForum, Integer> authModo = new SQLField<>("authModo", SQLType.INT, false);
	public static final SQLField<SQLForumForum, Integer> nbThreads = new SQLField<>("nbThreads", SQLType.INT, false);
	public static final SQLField<SQLForumForum, Integer> nbMessages = new SQLField<>("nbMessages", SQLType.INT, false);

}
