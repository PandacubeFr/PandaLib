package fr.pandacube.java.util.db;

import fr.pandacube.java.util.db.sql_tools.SQLElement;
import fr.pandacube.java.util.db.sql_tools.SQLField;
import fr.pandacube.java.util.db.sql_tools.SQLType;

public class SQLPingHistory extends SQLElement<SQLPingHistory> {
	
	public SQLPingHistory() {
		super();
	}
	
	public SQLPingHistory(int id) {
		super(id);
	}
	
	@Override
	protected String tableName() {
		return "pandacube_ping_history";
	}
	


	public static final SQLField<SQLPingHistory, Long> time = new SQLField<>("time", SQLType.BIGINT, false);
	public static final SQLField<SQLPingHistory, String> ip = new SQLField<>("ip", SQLType.VARCHAR(128), true);
	public static final SQLField<SQLPingHistory, Integer> minecraftVersion = new SQLField<>("minecraftVersion", SQLType.INT, false, 0);
	public static final SQLField<SQLPingHistory, String> hostName = new SQLField<>("hostName", SQLType.VARCHAR(128), true);


}
