package fr.pandacube.java.util.orm;

import java.util.List;

import org.javatuples.Pair;

public abstract class SQLWhere {

	public abstract Pair<String, List<Object>> toSQL();

	@Override
	public String toString() {
		return toSQL().getValue0();
	}

}