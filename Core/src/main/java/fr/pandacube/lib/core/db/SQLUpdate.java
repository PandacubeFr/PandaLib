package fr.pandacube.lib.core.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.pandacube.lib.core.util.Log;

public class SQLUpdate<E extends SQLElement<E>> {
	
	private final Class<E> elemClass;
	private final SQLWhere<E> where;
	private final Map<SQLField<E, ?>, Object> values;
	
	/* package */ SQLUpdate(Class<E> el, SQLWhere<E> w) {
		elemClass = el;
		where = w;
		values = new HashMap<>();
	}
	
	/* package */ SQLUpdate(Class<E> el, SQLWhere<E> w, Map<SQLField<E, ?>, Object> v) {
		elemClass = el;
		where = w;
		values = v;
	}
	
	public <T> SQLUpdate<E> set(SQLField<E, T> field, T value) {
		values.put(field, value);
		return this;
	}
	
	public SQLUpdate<E> setUnsafe(SQLField<E, ?> field, Object value) {
		values.put(field, value);
		return this;
	}
	
	public int execute() throws DBException {

		if (values.isEmpty()) {
			Log.warning(new DBException("Trying to do an UPDATE with no values to SET. Query aborted."));
			return 0;
		}

		StringBuilder sql = new StringBuilder("UPDATE " + DB.getTableName(elemClass) + " SET ");
		List<Object> params = new ArrayList<>();

		boolean first = true;
		for (Map.Entry<SQLField<E, ?>, Object> entry : values.entrySet()) {
			if (!first)
				sql.append(", ");
			sql.append("`").append(entry.getKey().getName()).append("` = ? ");
			SQLElement.addValueToSQLObjectList(params, entry.getKey(), entry.getValue());
			first = false;
		}

		if (where != null) {
			ParameterizedSQLString ret = where.toSQL();
			sql.append(" WHERE ").append(ret.sqlString());
			params.addAll(ret.parameters());
		}
		
		sql.append(";");
		
		return DB.customUpdateStatement(sql.toString(), params);
	}

}
