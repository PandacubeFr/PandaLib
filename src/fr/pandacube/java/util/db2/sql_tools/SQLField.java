package fr.pandacube.java.util.db2.sql_tools;

import java.util.ArrayList;
import java.util.List;

import javafx.util.Pair;

public class SQLField<T> {
	
	private Class<? extends SQLElement> sqlElemClass;
	public final String name;
	public final SQLType<T> type;
	public final boolean canBeNull;
	public final boolean autoIncrement;
	/* package */ final T defaultValue;
	
	public SQLField(String n, SQLType<T> t, boolean nul, boolean autoIncr, T deflt) {
		name = n;
		type = t;
		canBeNull = nul;
		autoIncrement = autoIncr;
		defaultValue = deflt;
	}
	
	public SQLField(String n, SQLType<T> t, boolean nul) {
		this(n, t, nul, false, null);
	}
	
	public SQLField(String n, SQLType<T> t, boolean nul, boolean autoIncr) {
		this(n, t, nul, autoIncr, null);
	}
	
	public SQLField(String n, SQLType<T> t, boolean nul, T deflt) {
		this(n, t, nul, false, deflt);
	}
	
	/* package */ Pair<String, List<Object>> forSQLPreparedStatement() {
		List<Object> params = new ArrayList<>(1);
		if (defaultValue != null && !autoIncrement)
			params.add(defaultValue);
		return new Pair<>(name
					 + " "+ type.toString()
					 + (canBeNull ? " NULL" : " NOT NULL")
					 + (autoIncrement ? " AUTO_INCREMENT" : "")
					 + ((defaultValue == null || autoIncrement) ? "" : " DEFAULT ?"),
				params);
	}
	
	/* package */ void setSQLElementType(Class<? extends SQLElement> elemClass) {
		sqlElemClass = elemClass;
	}
	
	public Class<? extends SQLElement> getSQLElementType() {
		return sqlElemClass;
	}
	
	
	/**
	 * <b>Don't use this {@link #toString()} method in a SQL query, because
	 * the default value is not escaped correctly</b>
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return forSQLPreparedStatement().getKey().replaceFirst("\\?", (defaultValue != null && !autoIncrement) ? defaultValue.toString() : "");
	}
	
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof SQLField)) return false;
		SQLField<?> f = (SQLField<?>) obj;
		if (!f.name.equals(name)) return false;
		if (!f.sqlElemClass.equals(sqlElemClass)) return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode() + sqlElemClass.hashCode();
	}
	
	
}
