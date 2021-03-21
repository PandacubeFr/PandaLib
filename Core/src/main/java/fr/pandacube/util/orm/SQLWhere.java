package fr.pandacube.util.orm;

import java.util.List;

import org.javatuples.Pair;

import fr.pandacube.util.Log;

public abstract class SQLWhere<E extends SQLElement<E>> {

	public abstract Pair<String, List<Object>> toSQL() throws ORMException;

	@Override
	public String toString() {
		try {
			return toSQL().getValue0();
		} catch (ORMException e) {
			Log.warning(e);
			return "[SQLWhere.toString() error (see logs)]";
		}
	}
	
	public SQLWhereAnd<E> and(SQLWhere<E> other) {
		return new SQLWhereAnd<E>().and(this).and(other);
	}
	
	public SQLWhereOr<E> or(SQLWhere<E> other) {
		return new SQLWhereOr<E>().or(this).or(other);
	}
	
	public static <E extends SQLElement<E>> SQLWhereAnd<E> and() {
		return new SQLWhereAnd<>();
	}
	
	public static <E extends SQLElement<E>> SQLWhereOr<E> or() {
		return new SQLWhereOr<>();
	}
	
	public static String escapeLike(String str) {
		return str.replace("\\", "\\\\").replace("_", "\\_").replace("%", "\\%");
	}

}
