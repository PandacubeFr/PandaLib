package fr.pandacube.java.util.db;

import fr.pandacube.java.util.db.sql_tools.ORM;
import fr.pandacube.java.util.db.sql_tools.ORMException;
import fr.pandacube.java.util.db.sql_tools.SQLElement;
import fr.pandacube.java.util.db.sql_tools.SQLElementList;
import fr.pandacube.java.util.db.sql_tools.SQLField;
import fr.pandacube.java.util.db.sql_tools.SQLOrderBy;
import fr.pandacube.java.util.db.sql_tools.SQLType;
import fr.pandacube.java.util.db.sql_tools.SQLWhereComp;
import fr.pandacube.java.util.db.sql_tools.SQLWhereComp.SQLComparator;

public class SQLMPGroup extends SQLElement<SQLMPGroup> {

	public SQLMPGroup() {
		super();
	}

	public SQLMPGroup(int id) {
		super(id);
	}

	@Override
	protected String tableName() {
		return "pandacube_mp_group";
	}

	public static final SQLField<SQLMPGroup, String> groupName = new SQLField<>("groupName", SQLType.VARCHAR(16), false);

	public SQLElementList<SQLMPGroupUser> getGroupUsers() throws ORMException {
		return ORM.getAll(SQLMPGroupUser.class, new SQLWhereComp(SQLMPGroupUser.groupId, SQLComparator.EQ, getId()),
				new SQLOrderBy().addField(ORM.getSQLIdField(SQLMPGroupUser.class)), null, null);
	}

	public static SQLMPGroup getByName(String name) throws ORMException {
		if (name == null) return null;

		return ORM.getFirst(SQLMPGroup.class, new SQLWhereComp(groupName, SQLComparator.EQ, name), null);
	}

}
