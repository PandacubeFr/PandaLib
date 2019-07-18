package fr.pandacube.java.util.orm;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;

public class SQLField<E extends SQLElement<E>, T> {

	private Class<E> sqlElemClass;
	private String name = null;
	public final SQLType<T> type;
	public final boolean canBeNull;
	public final boolean autoIncrement;
	/* package */ final T defaultValue;

	public SQLField(SQLType<T> t, boolean nul, boolean autoIncr, T deflt) {
		type = t;
		canBeNull = nul;
		autoIncrement = autoIncr;
		defaultValue = deflt;
	}

	public SQLField(SQLType<T> t, boolean nul) {
		this(t, nul, false, null);
	}

	public SQLField(SQLType<T> t, boolean nul, boolean autoIncr) {
		this(t, nul, autoIncr, null);
	}

	public SQLField(SQLType<T> t, boolean nul, T deflt) {
		this(t, nul, false, deflt);
	}

	/* package */ Pair<String, List<Object>> forSQLPreparedStatement() {
		List<Object> params = new ArrayList<>(1);
		if (defaultValue != null && !autoIncrement) params.add(defaultValue);
		return new Pair<>("`" + getName() + "` " + type.toString() + (canBeNull ? " NULL" : " NOT NULL")
				+ (autoIncrement ? " AUTO_INCREMENT" : "")
				+ ((defaultValue == null || autoIncrement) ? "" : " DEFAULT ?"), params);
	}

	/* package */ void setSQLElementType(Class<E> elemClass) {
		sqlElemClass = elemClass;
	}

	public Class<E> getSQLElementType() {
		return sqlElemClass;
	}
	
	/* package */ void setName(String n) {
		name = n;
	}
	
	public String getName() {
		return name;
	}

	/**
	 * <b>Don't use this {@link #toString()} method in a SQL query, because
	 * the default value is not escaped correctly</b>
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return forSQLPreparedStatement().getValue0().replaceFirst("\\?",
				(defaultValue != null && !autoIncrement) ? defaultValue.toString() : "");
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof SQLField)) return false;
		SQLField<?, ?> f = (SQLField<?, ?>) obj;
		if (!f.getName().equals(getName())) return false;
		if (!f.sqlElemClass.equals(sqlElemClass)) return false;
		return true;
	}

	@Override
	public int hashCode() {
		return getName().hashCode() + sqlElemClass.hashCode();
	}

}
