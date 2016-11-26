package fr.pandacube.java.util.db;

import fr.pandacube.java.util.db.sql_tools.SQLElement;
import fr.pandacube.java.util.db.sql_tools.SQLField;
import fr.pandacube.java.util.db.sql_tools.SQLType;

public class SQLForumCategorie extends SQLElement<SQLForumCategorie> {

	public SQLForumCategorie() {
		super();
	}

	public SQLForumCategorie(int id) {
		super(id);
	}

	@Override
	protected String tableName() {
		return "pandacube_forum_categorie";
	}

	public static final SQLField<SQLForumCategorie, String> nom = new SQLField<>("nom", SQLType.VARCHAR(100), false);
	public static final SQLField<SQLForumCategorie, Integer> ordre = new SQLField<>("ordre", SQLType.INT, false);

}
