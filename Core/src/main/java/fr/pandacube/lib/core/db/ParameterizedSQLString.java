package fr.pandacube.lib.core.db;

import java.util.List;

public record ParameterizedSQLString(String sqlString, List<Object> parameters) {

}
