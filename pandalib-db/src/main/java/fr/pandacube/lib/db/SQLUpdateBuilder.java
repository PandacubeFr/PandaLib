package fr.pandacube.lib.db;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.pandacube.lib.util.log.Log;

/**
 * Builder for a SQL {@code UPDATE} query.
 * @param <E> the type of te table affected by this update.
 */
public class SQLUpdateBuilder<E extends SQLElement<E>> {

    private final Class<E> elemClass;
    private final SQLWhere<E> where;
    private final Map<SQLField<E, ?>, Object> values;

    /* package */ SQLUpdateBuilder(Class<E> el, SQLWhere<E> w) {
        elemClass = el;
        where = w;
        values = new HashMap<>();
    }

    /* package */ SQLUpdateBuilder(Class<E> el, SQLWhere<E> w, Map<SQLField<E, ?>, Object> v) {
        elemClass = el;
        where = w;
        values = v;
    }

    /**
     * Sets the value for the specified field.
     * @param field the field to set.
     * @param value the value to put in the field.
     * @return this.
     * @param <T> the type of the value
     */
    public <T> SQLUpdateBuilder<E> set(SQLField<E, T> field, T value) {
        values.put(field, value);
        return this;
    }

    /**
     * Sets the value for the specified field, without statically checking the value type.
     * This method is not safe to use. Use {@link #set(SQLField, Object)} instead when possible.
     * @param field the field to set.
     * @param value the value to put in the field.
     * @return this.
     */
    public SQLUpdateBuilder<E> setUnsafe(SQLField<E, ?> field, Object value) {
        values.put(field, value);
        return this;
    }

    /**
     * Executes the SQL {@code UPDATE} query.
     * @return the value returned by {@link PreparedStatement#executeUpdate()}.
     * @throws DBException if an error occurs when interacting with the database.
     */
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
            params.add(entry.getKey().fromJavaTypeToJDBCType(entry.getValue()));
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
