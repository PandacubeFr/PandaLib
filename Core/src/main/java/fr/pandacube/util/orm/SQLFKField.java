package fr.pandacube.util.orm;

import fr.pandacube.util.Log;

/**
 * 
 * @author Marc
 *
 * @param <F> the table class of this current foreign key field
 * @param <T> the Java type of this field
 * @param <P> the table class of the targeted primary key
 */
public class SQLFKField<F extends SQLElement<F>, T, P extends SQLElement<P>> extends SQLField<F, T> {

	private SQLField<P, T> sqlPrimaryKeyField;
	private Class<P> sqlForeignKeyElemClass;

	protected SQLFKField(SQLType<T> t, boolean nul, T deflt, Class<P> fkEl, SQLField<P, T> fkF) {
		super(t, nul, deflt);
		construct(fkEl, fkF);
	}

	/* package */ static <E extends SQLElement<E>, F extends SQLElement<F>> SQLFKField<E, Integer, F> idFK(boolean nul, Class<F> fkEl) {
		return idFK(nul, null, fkEl);
	}

	/* package */ static <E extends SQLElement<E>, F extends SQLElement<F>> SQLFKField<E, Integer, F> idFK(boolean nul, Integer deflt, Class<F> fkEl) {
		if (fkEl == null) throw new IllegalArgumentException("foreignKeyElement can't be null");
		try {
			SQLField<F, Integer> f = ORM.getSQLIdField(fkEl);
			return new SQLFKField<>(f.type, nul, deflt, fkEl, f);
		} catch (ORMInitTableException e) {
			Log.severe("Can't create Foreign key Field targetting id field of '"+fkEl+"'", e);
			return null;
		}
	}

	/* package */ static <E extends SQLElement<E>, T, F extends SQLElement<F>> SQLFKField<E, T, F> customFK(boolean nul, Class<F> fkEl, SQLField<F, T> fkF) {
		return customFK(nul, null, fkEl, fkF);
	}

	/* package */ static <E extends SQLElement<E>, T, F extends SQLElement<F>> SQLFKField<E, T, F> customFK(boolean nul, T deflt, Class<F> fkEl, SQLField<F, T> fkF) {
		if (fkEl == null) throw new IllegalArgumentException("foreignKeyElement can't be null");
		return new SQLFKField<>(fkF.type, nul, deflt, fkEl, fkF);
	}

	private void construct(Class<P> fkEl, SQLField<P, T> fkF) {
		if (fkF == null) throw new IllegalArgumentException("foreignKeyField can't be null");
		try {
			ORM.initTable(fkEl);
		} catch (ORMInitTableException e) {
			throw new RuntimeException(e);
		}
		
		if (fkF.getSQLElementType() == null)
			throw new RuntimeException("Can't initialize foreign key. The primary key in the table " + fkEl.getName() + " is not properly initialized and can't be targetted by a forein key");
		sqlPrimaryKeyField = fkF;
		sqlForeignKeyElemClass = fkEl;
	}

	public SQLField<P, T> getPrimaryField() {
		return sqlPrimaryKeyField;
	}

	public Class<P> getForeignElementClass() {
		return sqlForeignKeyElemClass;
	}

}
