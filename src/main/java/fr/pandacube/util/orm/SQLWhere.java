package fr.pandacube.util.orm;

import java.util.List;

import org.javatuples.Pair;

import fr.pandacube.util.Log;

public abstract class SQLWhere {

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
	
	public SQLWhereAnd and(SQLWhere other) {
		return and().and(this).and(other);
	}
	
	public SQLWhereOr or(SQLWhere other) {
		return or().or(this).or(other);
	}
	
	public static SQLWhereAnd and() {
		return new SQLWhereAnd();
	}
	
	public static SQLWhereOr or() {
		return new SQLWhereOr();
	}
	
	
	
	public static SQLWhere like(SQLField<?, String> f, String like) {
		return new SQLWhereLike(f, like);
	}

}
