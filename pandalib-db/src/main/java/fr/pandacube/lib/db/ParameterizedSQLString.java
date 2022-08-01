package fr.pandacube.lib.db;

import java.util.List;

/* package */ record ParameterizedSQLString(String sqlString, List<Object> parameters) {
}
