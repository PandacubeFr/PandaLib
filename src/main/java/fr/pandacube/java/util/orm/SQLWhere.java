package fr.pandacube.java.util.orm;

import java.util.List;

import org.javatuples.Pair;

import fr.pandacube.java.util.Log;

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

}
