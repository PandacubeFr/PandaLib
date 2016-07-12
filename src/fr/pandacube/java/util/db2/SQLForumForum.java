package fr.pandacube.java.util.db2;

import fr.pandacube.java.util.db2.sql_tools.SQLElement;
import fr.pandacube.java.util.db2.sql_tools.SQLFKField;
import fr.pandacube.java.util.db2.sql_tools.SQLField;
import fr.pandacube.java.util.db2.sql_tools.SQLType;

public class SQLForumForum extends SQLElement {

	
	public SQLForumForum() { super(); }
	public SQLForumForum(int id) { super(id); }

	@Override
	protected String tableName() { return "pandacube_forum_forum"; }
	
	
	public static final SQLFKField<Integer, SQLForumCategorie> catId = SQLFKField.idFK("catId", SQLType.INT, false, SQLForumCategorie.class);
	public static final SQLField<String>          nom          = new SQLField<>("nom", SQLType.VARCHAR(100), false);
	public static final SQLField<String>          description  = new SQLField<>("description", SQLType.TEXT, false);
	public static final SQLField<Integer>         ordre        = new SQLField<>("ordre",        SQLType.INT, false);
	public static final SQLField<Integer>         authView     = new SQLField<>("authView",     SQLType.INT, false);
	public static final SQLField<Integer>         authPost     = new SQLField<>("authPost",     SQLType.INT, false);
	public static final SQLField<Integer>         authThread   = new SQLField<>("authThread",   SQLType.INT, false);
	public static final SQLField<Integer>         authAnchored = new SQLField<>("authAnchored", SQLType.INT, false);
	public static final SQLField<Integer>         authModo     = new SQLField<>("authModo",     SQLType.INT, false);
	public static final SQLField<Integer>         nbThreads    = new SQLField<>("nbThreads",    SQLType.INT, false);
	public static final SQLField<Integer>         nbMessages   = new SQLField<>("nbMessages",   SQLType.INT, false);
	
	

}
