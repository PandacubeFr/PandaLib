package fr.pandacube.util.orm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.javatuples.Pair;

import fr.pandacube.util.orm.SQLWhereComp.SQLComparator;

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
	
	


	
	
	public SQLWhere<E> eq(T r) {
		return comp(SQLComparator.EQ, r);
	}
	public SQLWhere<E> geq(T r) {
		return comp(SQLComparator.GEQ, r);
	}
	public SQLWhere<E> gt(T r) {
		return comp(SQLComparator.GT, r);
	}
	public SQLWhere<E> leq(T r) {
		return comp(SQLComparator.LEQ, r);
	}
	public SQLWhere<E> lt(T r) {
		return comp(SQLComparator.LT, r);
	}
	public SQLWhere<E> neq(T r) {
		return comp(SQLComparator.NEQ, r);
	}
	
	private SQLWhere<E> comp(SQLComparator c, T r) {
		if (r == null)
			throw new IllegalArgumentException("The value cannot be null. Use SQLField#isNull(value) or SQLField#isNotNull(value) to check for null values");
		return new SQLWhereComp<>(this, c, r);
	}
	
	
	public SQLWhere<E> like(String like) {
		return new SQLWhereLike<>(this, like);
	}
	
	
	
	public SQLWhere<E> in(Collection<T> v) {
		return new SQLWhereIn<>(this, v);
	}
	
	

	public SQLWhere<E> isNull() {
		return new SQLWhereNull<>(this, true);
	}
	
	public SQLWhere<E> isNotNull() {
		return new SQLWhereNull<>(this, false);
	}

}
