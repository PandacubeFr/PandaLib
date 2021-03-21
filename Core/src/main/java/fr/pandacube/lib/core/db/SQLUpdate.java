package fr.pandacube.lib.core.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

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

		String sql = "UPDATE " + DB.getTableName(elemClass) + " SET ";
		List<Object> params = new ArrayList<>();

		boolean first = true;
		for (Map.Entry<SQLField<E, ?>, Object> entry : values.entrySet()) {
			if (!first)
				sql += ", ";
			sql += "`" + entry.getKey().getName() + "` = ? ";
			SQLElement.addValueToSQLObjectList(params, entry.getKey(), entry.getValue());
			first = false;
		}

		if (where != null) {
			Pair<String, List<Object>> ret = where.toSQL();
			sql += " WHERE " + ret.getValue0();
			params.addAll(ret.getValue1());
		}
		
		sql += ";";
		
		return DB.customUpdateStatement(sql, params);
	}

}
