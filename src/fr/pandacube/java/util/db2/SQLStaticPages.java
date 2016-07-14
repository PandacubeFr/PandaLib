package fr.pandacube.java.util.db2;

import fr.pandacube.java.util.db2.sql_tools.SQLElement;
import fr.pandacube.java.util.db2.sql_tools.SQLField;
import fr.pandacube.java.util.db2.sql_tools.SQLType;

public class SQLStaticPages extends SQLElement {

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

	public static final SQLField<String> permalink = new SQLField<>("permalink", SQLType.VARCHAR(128), false);
	public static final SQLField<String> titreHead = new SQLField<>("titreHead", SQLType.VARCHAR(128), false);
	public static final SQLField<String> titreH2 = new SQLField<>("titreH2", SQLType.VARCHAR(255), false);
	public static final SQLField<String> texte = new SQLField<>("texte", SQLType.TEXT, false);
	public static final SQLField<String> permissions = new SQLField<>("permissions", SQLType.VARCHAR(255), true);

}
