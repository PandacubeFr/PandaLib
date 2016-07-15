package fr.pandacube.java.util.db.sql_tools;

import fr.pandacube.java.util.Log;

public class SQLFKField<E extends SQLElement<E>, T, F extends SQLElement<F>> extends SQLField<E, T> {

	private SQLField<F, T> sqlForeignKeyField;
	private Class<F> sqlForeignKeyElemClass;

	public SQLFKField(String n, SQLType<T> t, boolean nul, SQLField<F, T> fkF) {
		super(n, t, nul);
		construct(fkF);
	}

	public SQLFKField(String n, SQLType<T> t, boolean nul, T deflt, SQLField<F, T> fkF) {
		super(n, t, nul, deflt);
		construct(fkF);
	}

	public static <E extends SQLElement<E>, F extends SQLElement<F>> SQLFKField<E, Integer, F> idFK(String n, SQLType<Integer> t, boolean nul,
			Class<F> fkEl) {
		if (fkEl == null) throw new IllegalArgumentException("foreignKeyElement can't be null");
		try {
			return new SQLFKField<>(n, t, nul, ORM.getSQLIdField(fkEl));
		} catch (ORMInitTableException e) {
			Log.severe("Can't create Foreign key Field called '" + n + "'", e);
			return null;
		}
	}

	public static <E extends SQLElement<E>, F extends SQLElement<F>> SQLFKField<E, Integer, F> idFKField(String n, SQLType<Integer> t, boolean nul,
			Integer deflt, Class<F> fkEl) {
		if (fkEl == null) throw new IllegalArgumentException("foreignKeyElement can't be null");
		try {
			return new SQLFKField<>(n, t, nul, deflt, ORM.getSQLIdField(fkEl));
		} catch (ORMInitTableException e) {
			Log.severe("Can't create Foreign key Field called '" + n + "'", e);
			return null;
		}
	}

	private void construct(SQLField<F, T> fkF) {
		if (fkF == null) throw new IllegalArgumentException("foreignKeyField can't be null");
		Class<F> fkEl = fkF.getSQLElementType();
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
		sqlForeignKeyElemClass = fkEl;
	}

	public SQLField<F, T> getForeignField() {
		return sqlForeignKeyField;
	}

	public Class<F> getForeignElementClass() {
		return sqlForeignKeyElemClass;
	}

}
