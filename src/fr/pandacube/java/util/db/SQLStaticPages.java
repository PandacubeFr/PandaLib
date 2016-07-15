package fr.pandacube.java.util.db;

import fr.pandacube.java.util.db.sql_tools.SQLElement;
import fr.pandacube.java.util.db.sql_tools.SQLField;
import fr.pandacube.java.util.db.sql_tools.SQLType;

public class SQLStaticPages extends SQLElement<SQLStaticPages> {

	public SQLStaticPages() {
		super();
	}

	public SQLStaticPages(int id) {
		super(id);
	}

	@Override
	protected String tableName() {
		return "pandacube_static_pages";
	}

	public static final SQLField<SQLStaticPages, String> permalink = new SQLField<>("permalink", SQLType.VARCHAR(128), false);
	public static final SQLField<SQLStaticPages, String> titreHead = new SQLField<>("titreHead", SQLType.VARCHAR(128), false);
	public static final SQLField<SQLStaticPages, String> titreH2 = new SQLField<>("titreH2", SQLType.VARCHAR(255), false);
	public static final SQLField<SQLStaticPages, String> texte = new SQLField<>("texte", SQLType.TEXT, false);
	public static final SQLField<SQLStaticPages, String> permissions = new SQLField<>("permissions", SQLType.VARCHAR(255), true);

}
