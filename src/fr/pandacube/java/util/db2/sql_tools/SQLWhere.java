package fr.pandacube.java.util.db2.sql_tools;

import java.util.List;

import javafx.util.Pair;

public abstract class SQLWhere {

	public abstract Pair<String, List<Object>> toSQL();

	@Override
	public String toString() {
		return toSQL().getKey();
	}

}
