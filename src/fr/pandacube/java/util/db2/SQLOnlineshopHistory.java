package fr.pandacube.java.util.db2;

import java.util.UUID;

import fr.pandacube.java.util.db2.sql_tools.SQLElement;
import fr.pandacube.java.util.db2.sql_tools.SQLField;
import fr.pandacube.java.util.db2.sql_tools.SQLType;

public class SQLOnlineshopHistory extends SQLElement {

	
	public SQLOnlineshopHistory() { super(); }
	public SQLOnlineshopHistory(int id) { super(id); }

	@Override
	protected String tableName() { return "pandacube_onlineshop_history"; }
	
	
	public static final SQLField<Long>   time           = new SQLField<>("time",           SQLType.BIGINT,             false);
	public static final SQLField<String> transactionId  = new SQLField<>("transactionId",  SQLType.VARCHAR(255),       true);
	public static final SQLField<SourceType> sourceType = new SQLField<>("sourceType", SQLType.ENUM(SourceType.class), false);
	public static final SQLField<String> sourcePlayerId = new SQLField<>("sourcePlayerId", SQLType.CHAR(36),           true);
	public static final SQLField<Double> sourceQuantity = new SQLField<>("sourceQuantity", SQLType.DOUBLE,             false);
	public static final SQLField<String> sourceName     = new SQLField<>("sourceName",     SQLType.VARCHAR(64),        false);
	public static final SQLField<DestType> destType     = new SQLField<>("destType",     SQLType.ENUM(DestType.class), false);
	public static final SQLField<String> destPlayerId   = new SQLField<>("destPlayerId",   SQLType.CHAR(36),           false);
	public static final SQLField<Double> destQuantity   = new SQLField<>("destQuantity",   SQLType.DOUBLE,             false);
	public static final SQLField<String> destName       = new SQLField<>("destName",       SQLType.VARCHAR(64),        false);
	
	
	

	
	public UUID getSourcePlayerId() {
		String id = (String)get(sourcePlayerId);
		return (id == null) ? null : UUID.fromString(id);
	}
	
	
	public void setSourcePlayerId(UUID pName) {
		set(sourcePlayerId, (pName == null) ? (String)null : pName.toString());
	}
	

	
	public UUID getDestPlayerId() {
		String id = (String)get(destPlayerId);
		return (id == null) ? null : UUID.fromString(id);
	}
	
	
	public void setDestPlayerId(UUID pName) {
		set(destPlayerId, (pName == null) ? (String)null : pName.toString());
	}
	
	
	
	
	
	
	
	public static enum SourceType {
		REAL_MONEY, BAMBOU
	}
	
	public static enum DestType {
		BAMBOU, GRADE
	}
	
	
}
