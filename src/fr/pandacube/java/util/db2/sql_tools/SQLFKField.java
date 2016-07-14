package fr.pandacube.java.util.db2.sql_tools;

import fr.pandacube.java.util.Log;

public class SQLFKField<T, E extends SQLElement> extends SQLField<T> {

	private SQLField<T> sqlForeignKeyField;
	private Class<E> sqlForeignKeyElement;

	public SQLFKField(String n, SQLType<T> t, boolean nul, Class<E> fkEl, SQLField<T> fkF) {
		super(n, t, nul);
		construct(fkEl, fkF);
	}

	public SQLFKField(String n, SQLType<T> t, boolean nul, T deflt, Class<E> fkEl, SQLField<T> fkF) {
		super(n, t, nul, deflt);
		construct(fkEl, fkF);
	}

	public static <E extends SQLElement> SQLFKField<Integer, E> idFK(String n, SQLType<Integer> t, boolean nul,
			Class<E> fkEl) {
		if (fkEl == null) throw new IllegalArgumentException("foreignKeyElement can't be null");
		try {
			return new SQLFKField<>(n, t, nul, fkEl, ORM.getSQLIdField(fkEl));
		} catch (ORMInitTableException e) {
			Log.severe("Can't create Foreign key Field called '" + n + "'", e);
			return null;
		}
	}

	public static <E extends SQLElement> SQLFKField<Integer, E> idFKField(String n, SQLType<Integer> t, boolean nul,
			Integer deflt, Class<E> fkEl) {
		if (fkEl == null) throw new IllegalArgumentException("foreignKeyElement can't be null");
		try {
			return new SQLFKField<>(n, t, nul, deflt, fkEl, ORM.getSQLIdField(fkEl));
		} catch (ORMInitTableException e) {
			Log.severe("Can't create Foreign key Field called '" + n + "'", e);
			return null;
		}
	}

	private void construct(Class<E> fkEl, SQLField<T> fkF) {
		if (fkEl == null) throw new IllegalArgumentException("foreignKeyElement can't be null");
		if (fkF == null) throw new IllegalArgumentException("foreignKeyField can't be null");
		try {
			ORM.initTable(fkEl);
		} catch (ORMInitTableException e) {
			Log.severe(e);
			return;
		}
		if (!fkEl.equals(fkF.getSQLElementType()))
			throw new IllegalArgumentException("foreignKeyField must be from supplied foreignKeyElement");
		if (!type.equals(fkF.type))
			throw new IllegalArgumentException("foreignKeyField and current Field must have the same SQLType");
		sqlForeignKeyField = fkF;
		sqlForeignKeyElement = fkEl;
	}

	public SQLField<T> getForeignField() {
		return sqlForeignKeyField;
	}

	public Class<E> getForeignElementClass() {
		return sqlForeignKeyElement;
	}

}
