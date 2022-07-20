package fr.pandacube.lib.db;

import java.util.List;

public record ParameterizedSQLString(String sqlString, List<Object> parameters) {
}
