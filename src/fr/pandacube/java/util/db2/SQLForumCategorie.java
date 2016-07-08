package fr.pandacube.java.util.db2;

import fr.pandacube.java.util.db2.sql_tools.SQLElement;
import fr.pandacube.java.util.db2.sql_tools.SQLField;
import fr.pandacube.java.util.db2.sql_tools.SQLType;

public class SQLForumCategorie extends SQLElement {
	
	public SQLForumCategorie() { super(); }
	public SQLForumCategorie(int id) { super(id); }

	@Override
	protected String tableName() { return "pandacube_forum_categorie"; }
	
	
	public static final SQLField<String>  nom   = new SQLField<>("nom",   SQLType.VARCHAR(100), false);
	public static final SQLField<Integer> ordre = new SQLField<>("ordre", SQLType.INT,          false);
	

}
