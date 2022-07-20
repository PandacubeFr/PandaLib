package fr.pandacube.lib.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.pandacube.lib.db.SQLWhere.SQLWhereComp;
import fr.pandacube.lib.db.SQLWhere.SQLWhereComp.SQLComparator;
import fr.pandacube.lib.db.SQLWhere.SQLWhereIn;
import fr.pandacube.lib.db.SQLWhere.SQLWhereLike;
import fr.pandacube.lib.db.SQLWhere.SQLWhereNull;

public class SQLField<E extends SQLElement<E>, T> {

	private Class<E> sqlElemClass;
	private String name = null;
	public final SQLType<T> type;
	public final boolean canBeNull;
	public final boolean autoIncrement;
	/* package */ final T defaultValue;

	/* package */ SQLField(SQLType<T> t, boolean nul, boolean autoIncr, T deflt) {
		type = t;
		canBeNull = nul;
		autoIncrement = autoIncr;
		defaultValue = deflt;
	}

	/* package */ SQLField(SQLType<T> t, boolean nul) {
		this(t, nul, false, null);
	}

	/* package */ SQLField(SQLType<T> t, boolean nul, boolean autoIncr) {
		this(t, nul, autoIncr, null);
	}

	/* package */ SQLField(SQLType<T> t, boolean nul, T deflt) {
		this(t, nul, false, deflt);
	}

	/* package */ ParameterizedSQLString forSQLPreparedStatement() {
		List<Object> params = new ArrayList<>(1);
		if (defaultValue != null && !autoIncrement) params.add(defaultValue);
		return new ParameterizedSQLString("`" + getName() + "` " + type.toString() + (canBeNull ? " NULL" : " NOT NULL")
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
	 * <b>Don't use this {@code toString()} method in a SQL query, because
	 * the default value is not escaped correctly</b>
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return forSQLPreparedStatement().sqlString().replaceFirst("\\?",
				(defaultValue != null && !autoIncrement) ? defaultValue.toString() : "");
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof SQLField<?, ?> f
				&& f.getName().equals(getName())
				&& f.sqlElemClass.equals(sqlElemClass);
	}

	@Override
	public int hashCode() {
		return getName().hashCode() + sqlElemClass.hashCode();
	}
	
	


	
	
	public fr.pandacube.lib.db.SQLWhere<E> eq(T r) {
		return comp(SQLComparator.EQ, r);
	}
	public fr.pandacube.lib.db.SQLWhere<E> geq(T r) {
		return comp(SQLComparator.GEQ, r);
	}
	public fr.pandacube.lib.db.SQLWhere<E> gt(T r) {
		return comp(SQLComparator.GT, r);
	}
	public fr.pandacube.lib.db.SQLWhere<E> leq(T r) {
		return comp(SQLComparator.LEQ, r);
	}
	public fr.pandacube.lib.db.SQLWhere<E> lt(T r) {
		return comp(SQLComparator.LT, r);
	}
	public fr.pandacube.lib.db.SQLWhere<E> neq(T r) {
		return comp(SQLComparator.NEQ, r);
	}
	
	private fr.pandacube.lib.db.SQLWhere<E> comp(SQLComparator c, T r) {
		if (r == null)
			throw new IllegalArgumentException("The value cannot be null. Use SQLField#isNull(value) or SQLField#isNotNull(value) to check for null values");
		return new SQLWhereComp<>(this, c, r);
	}
	
	
	public fr.pandacube.lib.db.SQLWhere<E> like(String like) {
		return new SQLWhereLike<>(this, like);
	}
	
	
	
	public fr.pandacube.lib.db.SQLWhere<E> in(Collection<T> v) {
		return new SQLWhereIn<>(this, v);
	}
	
	

	public fr.pandacube.lib.db.SQLWhere<E> isNull() {
		return new SQLWhereNull<>(this, true);
	}
	
	public fr.pandacube.lib.db.SQLWhere<E> isNotNull() {
		return new SQLWhereNull<>(this, false);
	}

}
