package fr.pandacube.lib.core.db;

import java.util.List;

//public record ParameterizedSQLString(String sqlString, List<Object> parameters) { } // Java 16
public class ParameterizedSQLString {
	private final String sqlString;
	private final List<Object> parameters;
	public ParameterizedSQLString(String sqlString, List<Object> parameters) {
		this.sqlString = sqlString; this.parameters = parameters;
	}
	public String sqlString() { return sqlString; }
	public List<Object> parameters() { return parameters; }
}
