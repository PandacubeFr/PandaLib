package fr.pandacube.java.util.orm;

import fr.pandacube.java.util.Log;

public class SQLFKField<E extends SQLElement<E>, T, F extends SQLElement<F>> extends SQLField<E, T> {

	private SQLField<F, T> sqlForeignKeyField;
	private Class<F> sqlForeignKeyElemClass;

	protected SQLFKField(SQLType<T> t, boolean nul, T deflt, Class<F> fkEl, SQLField<F, T> fkF) {
		super(t, nul, deflt);
		construct(fkEl, fkF);
	}

	public static <E extends SQLElement<E>, F extends SQLElement<F>> SQLFKField<E, Integer, F> idFK(boolean nul, Class<F> fkEl) {
		return idFK(nul, null, fkEl);
	}

	public static <E extends SQLElement<E>, F extends SQLElement<F>> SQLFKField<E, Integer, F> idFK(boolean nul, Integer deflt, Class<F> fkEl) {
		if (fkEl == null) throw new IllegalArgumentException("foreignKeyElement can't be null");
		try {
			SQLField<F, Integer> f = ORM.getSQLIdField(fkEl);
			return new SQLFKField<>(f.type, nul, deflt, fkEl, f);
		} catch (ORMInitTableException e) {
			Log.severe("Can't create Foreign key Field targetting id field of '"+fkEl+"'", e);
			return null;
		}
	}

	public static <E extends SQLElement<E>, T, F extends SQLElement<F>> SQLFKField<E, T, F> customFK(boolean nul, Class<F> fkEl, SQLField<F, T> fkF) {
		return customFK(nul, null, fkEl, fkF);
	}

	public static <E extends SQLElement<E>, T, F extends SQLElement<F>> SQLFKField<E, T, F> customFK(boolean nul, T deflt, Class<F> fkEl, SQLField<F, T> fkF) {
		if (fkEl == null) throw new IllegalArgumentException("foreignKeyElement can't be null");
		return new SQLFKField<>(fkF.type, nul, deflt, fkEl, fkF);
	}

	private void construct(Class<F> fkEl, SQLField<F, T> fkF) {
		if (fkF == null) throw new IllegalArgumentException("foreignKeyField can't be null");
		try {
			ORM.initTable(fkEl);
		} catch (ORMInitTableException e) {
			throw new RuntimeException(e);
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
