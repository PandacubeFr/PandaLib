package fr.pandacube.java.util.db2;

import fr.pandacube.java.util.db2.sql_tools.SQLElement;
import fr.pandacube.java.util.db2.sql_tools.SQLField;
import fr.pandacube.java.util.db2.sql_tools.SQLType;

public class SQLMPGroup extends SQLElement {

	
	public SQLMPGroup() { super(); }
	public SQLMPGroup(int id) { super(id); }

	@Override
	protected String tableName() { return "pandacube_mp_group"; }
	
	
	public static final SQLField<String> groupName = new SQLField<>("groupName", SQLType.VARCHAR(16), false);

}
