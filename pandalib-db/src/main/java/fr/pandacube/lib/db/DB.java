package fr.pandacube.lib.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.util.Log;

/**
 * Static class to handle most of the database operations.
 * <p>
 * To use this database library, first call {@link #init(DBConnection, String)} with an appropriate {@link DBConnection},
 * then you can initialize every table you need for your application, using {@link #initTable(Class)}.
 *
 * @author Marc Baloup
 */
public final class DB {

    private static final List<Class<? extends SQLElement<?>>> tables = new ArrayList<>();
    private static final Map<Class<? extends SQLElement<?>>, String> tableNames = new HashMap<>();

    private static DBConnection connection;
    /* package */ static String tablePrefix = "";

    /**
     * Gets the {@link DBConnection}.
     * @return the {@link DBConnection}.
     */
    public static DBConnection getConnection() {
        return connection;
    }

    /**
     * Initialize with the provided connection.
     * @param conn the database connection.
     * @param tablePrefix determine a prefix for the table that will be initialized.
     */
    public synchronized static void init(DBConnection conn, String tablePrefix) {
        connection = conn;
        DB.tablePrefix = Objects.requireNonNull(tablePrefix);
    }

    /**
     * Initialialize the table represented by the provided class.
     * @param elemClass the class representing a table.
     * @param <E> the type representing the table.
     * @throws DBInitTableException if the table failed to initialized.
     */
    public static synchronized <E extends SQLElement<E>> void initTable(Class<E> elemClass) throws DBInitTableException {
        if (connection == null) {
            throw new DBInitTableException(elemClass, "Database connection is not yet initialized.");
        }
        if (tables.contains(elemClass)) return;
        try {
            tables.add(elemClass);
            Log.debug("[DB] Start Init SQL table "+elemClass.getSimpleName());
            E instance = elemClass.getConstructor().newInstance();
            String tableName = tablePrefix + instance.tableName();
            tableNames.put(elemClass, tableName);
            if (!tableExistInDB(tableName)) createTable(instance);
            Log.debug("[DB] End init SQL table "+elemClass.getSimpleName());
        } catch (Exception|ExceptionInInitializerError e) {
            throw new DBInitTableException(elemClass, e);
        }
    }

    private static <E extends SQLElement<E>> void createTable(E elem) throws SQLException {

        String tableName = tablePrefix + elem.tableName();

        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (");
        List<Object> params = new ArrayList<>();

        Collection<SQLField<E, ?>> tableFields = elem.getFields().values();
        boolean first = true;
        for (SQLField<E, ?> f : tableFields) {
            ParameterizedSQLString statementPart = f.forSQLPreparedStatement();
            params.addAll(statementPart.parameters());

            if (!first)
                sql.append(", ");
            first = false;
            sql.append(statementPart.sqlString());
        }

        sql.append(", PRIMARY KEY id(id))");

        try (Connection c = connection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            int i = 1;
            for (Object val : params)
                ps.setObject(i++, val);
            Log.info("Creating table " + elem.tableName() + ":\n" + ps.toString());
            ps.executeUpdate();
        }
    }

    /**
     * Gets the name of the table in the database.
     * @param elemClass the class representing a table.
     * @return a table name.
     * @param <E> the type representing the table.
     * @throws DBInitTableException if the provided table had to be initialized and it failed to do so.
     */
    public static <E extends SQLElement<E>> String getTableName(Class<E> elemClass) throws DBInitTableException {
        initTable(elemClass);
        return tableNames.get(elemClass);
    }

    private static boolean tableExistInDB(String tableName) throws SQLException {
        try (Connection c = connection.getConnection();
             ResultSet set = c.getMetaData().getTables(null, null, tableName, null)) {
            return set.next();
        }
    }

    /**
     * Gets the {@code id} field of the provided table.
     * @param elemClass the class representing a table.
     * @return the {@code id} field of the provided table.
     * @param <E> the type representing the table.
     * @throws DBInitTableException if the provided table had to be initialized and it failed to do so.
     */
    @SuppressWarnings("unchecked")
    public static <E extends SQLElement<E>> SQLField<E, Integer> getSQLIdField(Class<E> elemClass) throws DBInitTableException {
        initTable(elemClass);
        return (SQLField<E, Integer>) SQLElement.fieldsCache.get(elemClass).get("id");
    }

    /**
     * Fetch the entry from the provided table, that has the specified ids.
     * @param elemClass the class representing a table.
     * @param ids the ids of the element entries.
     * @return the entry from the provided table, that has the specified ids.
     * @param <E> the type representing the table.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public static <E extends SQLElement<E>> SQLElementList<E> getByIds(Class<E> elemClass, Integer... ids) throws DBException {
        return getByIds(elemClass, Arrays.asList(ids));
    }

    /**
     * Fetch the entry from the provided table, that has the specified ids.
     * @param elemClass the class representing a table.
     * @param ids the ids of the element entries.
     * @return the entry from the provided table, that has the specified ids.
     * @param <E> the type representing the table.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public static <E extends SQLElement<E>> SQLElementList<E> getByIds(Class<E> elemClass, Collection<Integer> ids) throws DBException {
        return getAll(elemClass, getSQLIdField(elemClass).in(ids), SQLOrderBy.asc(getSQLIdField(elemClass)), 1, null);
    }

    /**
     * Fetch the entry from the provided table, that has the specified id.
     * @param elemClass the class representing a table.
     * @param id the id of the element entry.
     * @return the entry from the provided table, that has the specified id, or null if none was found.
     * @param <E> the type representing the table.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public static <E extends SQLElement<E>> E getById(Class<E> elemClass, int id) throws DBException {
        return getFirst(elemClass, getSQLIdField(elemClass).eq(id));
    }

    /**
     * Fetch the entry from the provided table, using the provided {@code WHERE} clause,
     * and a {@code LIMIT} of 1.
     * @param elemClass the class representing a table.
     * @param where the {@code WHERE} clause of the query.
     * @return the entry from the provided table, or null if none was found.
     * @param <E> the type representing the table.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public static <E extends SQLElement<E>> E getFirst(Class<E> elemClass, SQLWhere<E> where) throws DBException {
        return getFirst(elemClass, where, null, null);
    }

    /**
     * Fetch the entry from the provided table, using the provided {@code ORDER BY} clause,
     * and a {@code LIMIT} of 1.
     * @param elemClass the class representing a table.
     * @param orderBy the {@code ORDER BY} clause of the query.
     * @return the entry from the provided table, or null if none was found.
     * @param <E> the type representing the table.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public static <E extends SQLElement<E>> E getFirst(Class<E> elemClass, SQLOrderBy<E> orderBy) throws DBException {
        return getFirst(elemClass, null, orderBy, null);
    }

    /**
     * Fetch the entry from the provided table, using the provided {@code WHERE} and {@code ORDER BY} clauses,
     * and a {@code LIMIT} of 1.
     * @param elemClass the class representing a table.
     * @param where the {@code WHERE} clause of the query.
     * @param orderBy the {@code ORDER BY} clause of the query.
     * @return the entry from the provided table, or null if none was found.
     * @param <E> the type representing the table.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public static <E extends SQLElement<E>> E getFirst(Class<E> elemClass, SQLWhere<E> where, SQLOrderBy<E> orderBy) throws DBException {
        return getFirst(elemClass, where, orderBy, null);
    }

    /**
     * Fetch the entry from the provided table, using the provided {@code WHERE}, {@code ORDER BY} and {@code OFFSET}
     * clauses, and a {@code LIMIT} of 1.
     * @param elemClass the class representing a table.
     * @param where the {@code WHERE} clause of the query.
     * @param orderBy the {@code ORDER BY} clause of the query.
     * @param offset the {@code OFFSET} clause of the query.
     * @return the entry from the provided table, or null if none was found.
     * @param <E> the type representing the table.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public static <E extends SQLElement<E>> E getFirst(Class<E> elemClass, SQLWhere<E> where, SQLOrderBy<E> orderBy, Integer offset) throws DBException {
        SQLElementList<E> elts = getAll(elemClass, where, orderBy, 1, offset);
        return (elts.size() == 0) ? null : elts.get(0);
    }

    /**
     * Fetch all the entries from the provided table.
     * @param elemClass the class representing a table.
     * @return the entries from the provided table, or empty if none was found.
     * @param <E> the type representing the table.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public static <E extends SQLElement<E>> SQLElementList<E> getAll(Class<E> elemClass) throws DBException {
        return getAll(elemClass, null, null, null, null);
    }

    /**
     * Fetch the entries from the provided table, using the provided {@code WHERE} clause.
     * @param elemClass the class representing a table.
     * @param where the {@code WHERE} clause of the query.
     * @return the entries from the provided table, or empty if none was found.
     * @param <E> the type representing the table.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public static <E extends SQLElement<E>> SQLElementList<E> getAll(Class<E> elemClass, SQLWhere<E> where) throws DBException {
        return getAll(elemClass, where, null, null, null);
    }

    /**
     * Fetch the entries from the provided table, using the provided {@code WHERE} and {@code ORDER BY} clauses.
     * @param elemClass the class representing a table.
     * @param where the {@code WHERE} clause of the query.
     * @param orderBy the {@code ORDER BY} clause of the query.
     * @return the entries from the provided table, or empty if none was found.
     * @param <E> the type representing the table.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public static <E extends SQLElement<E>> SQLElementList<E> getAll(Class<E> elemClass, SQLWhere<E> where, SQLOrderBy<E> orderBy) throws DBException {
        return getAll(elemClass, where, orderBy, null, null);
    }

    /**
     * Fetch the entries from the provided table, using the provided {@code WHERE}, {@code ORDER BY} and {@code LIMIT}
     * clauses.
     * @param elemClass the class representing a table.
     * @param where the {@code WHERE} clause of the query.
     * @param orderBy the {@code ORDER BY} clause of the query.
     * @param limit the {@code LIMIT} clause of the query.
     * @return the entries from the provided table, or empty if none was found.
     * @param <E> the type representing the table.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public static <E extends SQLElement<E>> SQLElementList<E> getAll(Class<E> elemClass, SQLWhere<E> where, SQLOrderBy<E> orderBy, Integer limit) throws DBException {
        return getAll(elemClass, where, orderBy, limit, null);
    }

    /**
     * Fetch the entries from the provided table, using the provided {@code WHERE}, {@code ORDER BY}, {@code LIMIT} and
     * {@code OFFSET} clauses.
     * @param elemClass the class representing a table.
     * @param where the {@code WHERE} clause of the query.
     * @param orderBy the {@code ORDER BY} clause of the query.
     * @param limit the {@code LIMIT} clause of the query.
     * @param offset the {@code OFFSET} clause of the query.
     * @return the entries from the provided table, or empty if none was found.
     * @param <E> the type representing the table.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public static <E extends SQLElement<E>> SQLElementList<E> getAll(Class<E> elemClass, SQLWhere<E> where, SQLOrderBy<E> orderBy, Integer limit, Integer offset) throws DBException {
        SQLElementList<E> elmts = new SQLElementList<>();
        forEach(elemClass, where, orderBy, limit, offset, elmts::add);
        return elmts;
    }

    /**
     * Iterate through all the entries from the provided table.
     * @param elemClass the class representing a table.
     * @param action the action to perform on each entries.
     * @param <E> the type representing the table.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public static <E extends SQLElement<E>> void forEach(Class<E> elemClass, Consumer<E> action) throws DBException {
        forEach(elemClass, null, null, null, null, action);
    }

    /**
     * Iterate through the entries from the provided table, using the provided {@code WHERE} clause.
     * @param elemClass the class representing a table.
     * @param where the {@code WHERE} clause of the query.
     * @param action the action to perform on each entries.
     * @param <E> the type representing the table.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public static <E extends SQLElement<E>> void forEach(Class<E> elemClass, SQLWhere<E> where, Consumer<E> action) throws DBException {
        forEach(elemClass, where, null, null, null, action);
    }

    /**
     * Iterate through the entries from the provided table, using the provided {@code WHERE} and {@code ORDER BY}
     * clauses.
     * @param elemClass the class representing a table.
     * @param where the {@code WHERE} clause of the query.
     * @param orderBy the {@code ORDER BY} clause of the query.
     * @param action the action to perform on each entries.
     * @param <E> the type representing the table.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public static <E extends SQLElement<E>> void forEach(Class<E> elemClass, SQLWhere<E> where, SQLOrderBy<E> orderBy, Consumer<E> action) throws DBException {
        forEach(elemClass, where, orderBy, null, null, action);
    }

    /**
     * Iterate through the entries from the provided table, using the provided {@code WHERE}, {@code ORDER BY} and
     * {@code LIMIT} clauses.
     * @param elemClass the class representing a table.
     * @param where the {@code WHERE} clause of the query.
     * @param orderBy the {@code ORDER BY} clause of the query.
     * @param limit the {@code LIMIT} clause of the query.
     * @param action the action to perform on each entries.
     * @param <E> the type representing the table.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public static <E extends SQLElement<E>> void forEach(Class<E> elemClass, SQLWhere<E> where, SQLOrderBy<E> orderBy, Integer limit, Consumer<E> action) throws DBException {
        forEach(elemClass, where, orderBy, limit, null, action);
    }

    /**
     * Iterate through the entries from the provided table, using the provided {@code WHERE}, {@code ORDER BY},
     * {@code LIMIT} and {@code OFFSET} clauses.
     * @param elemClass the class representing a table.
     * @param where the {@code WHERE} clause of the query.
     * @param orderBy the {@code ORDER BY} clause of the query.
     * @param limit the {@code LIMIT} clause of the query.
     * @param offset the {@code OFFSET} clause of the query.
     * @param action the action to perform on each entries.
     * @param <E> the type representing the table.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public static <E extends SQLElement<E>> void forEach(Class<E> elemClass, SQLWhere<E> where, SQLOrderBy<E> orderBy, Integer limit, Integer offset, Consumer<E> action) throws DBException {
        initTable(elemClass);

        String sql = "SELECT * FROM " + getTableName(elemClass);

        List<Object> params = new ArrayList<>();

        if (where != null) {
            ParameterizedSQLString ret = where.toSQL();
            sql += " WHERE " + ret.sqlString();
            params.addAll(ret.parameters());
        }
        if (orderBy != null)
            sql += " ORDER BY " + orderBy.toSQL();
        if (limit != null)
            sql += " LIMIT " + limit;
        if (offset != null)
            sql += " OFFSET " + offset;
        sql += ";";

        customQueryStatement(sql, params, set -> {
            while (set.next()) {
                E elm = getElementInstance(set, elemClass);
                action.accept(elm);
            }
            return null;
        });
    }



    /**
     * Counts the number of entries in the provided table.
     * @param elemClass the class representing a table.
     * @param <E> the type representing the table.
     * @return the number of entries in the provided table.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public static <E extends SQLElement<E>> long count(Class<E> elemClass) throws DBException {
        return count(elemClass, null);
    }

    /**
     * Counts the number of entries from the provided table, that meet the {@code WHERE} clause conditions.
     * @param elemClass the class representing a table.
     * @param where the {@code WHERE} clause of the query.
     * @param <E> the type representing the table.
     * @return the number of entries from the provided table, that meet the {@code WHERE} clause conditions.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public static <E extends SQLElement<E>> long count(Class<E> elemClass, SQLWhere<E> where) throws DBException {
        initTable(elemClass);

        String sql = "SELECT COUNT(*) AS count FROM " + getTableName(elemClass);

        List<Object> params = new ArrayList<>();

        if (where != null) {
            ParameterizedSQLString ret = where.toSQL();
            sql += " WHERE " + ret.sqlString();
            params.addAll(ret.parameters());
        }
        sql += ";";

        return customQueryStatement(sql, params, rs -> {
            if (rs.next()) {
                return rs.getLong(1);
            }
            throw new DBException("Can’t retrieve element count from database (the ResultSet is empty).");
        });
    }


    /**
     * Execute a custom SQL query statement with the provided parameters, and passes the produced {@link ResultSet}
     * to the {@code rsFunction}.
     * @param sql the query in SQL language, passed to {@link Connection#prepareStatement(String)}.
     * @param params the parameters to put in the query. Uses {@link PreparedStatement#setObject(int, Object)}.
     * @param rsFunction the function executed with the result set as the parameter. Its return value will then be
     *                   returned to the caller of this method.
     * @param <R> the return type of {@code rsFunction}.
     * @return the value returned by {@code rsFunction}.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public static <R> R customQueryStatement(String sql, List<Object> params, ResultSetFunction<R> rsFunction) throws DBException {
        try (Connection c = connection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            int i = 1;
            for (Object val : params) {
                if (val instanceof Enum<?>) val = ((Enum<?>) val).name();
                ps.setObject(i++, val);
            }
            Log.debug(ps.toString());

            try (ResultSet set = ps.executeQuery()) {
                return rsFunction.apply(set);
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }

    }


    /**
     * A function that takes a {@link ResultSet} as an input and output any value.
     * @param <R> the return type.
     */
    @FunctionalInterface
    public interface ResultSetFunction<R> {
        /**
         * Reads data into the result set.
         * @param resultSet the result set to read.
         * @throws SQLException if an error occurs while reading the result set.
         * @throws DBException if an error occurs while reading the result set.
         * @return data computed from the resultSet.
         */
        R apply(ResultSet resultSet) throws SQLException, DBException;
    }


    /**
     * Prepares an UPDATE query to the database.
     * Call the {@link SQLUpdateBuilder#set(SQLField, Object)} for any field you want to change the value, then call
     * {@link SQLUpdateBuilder#execute()} to send the query.
     * @param elemClass the class representing a table.
     * @param where the {@code WHERE} clause of the query.
     * @param <E> the type representing the table.
     * @return an {@link SQLUpdateBuilder} instance.
     */
    public static <E extends SQLElement<E>> SQLUpdateBuilder<E> update(Class<E> elemClass, SQLWhere<E> where) {
        return new SQLUpdateBuilder<>(elemClass, where);
    }

    /* package */ static <E extends SQLElement<E>> int update(Class<E> elemClass, SQLWhere<E> where, Map<SQLField<E, ?>, Object> values) throws DBException {
        return new SQLUpdateBuilder<>(elemClass, where, values).execute();
    }


    /**
     * Delete the entries from the provided table, using the provided {@code WHERE} clause.
     * @param elemClass the class representing a table.
     * @param where the condition to meet for an element to be deleted from the table. If null, the table is truncated
     *              using {@link #truncateTable(Class)}.
     * @param <E> the type representing the table.
     * @return The return value of {@link PreparedStatement#executeUpdate()}, for an SQL query {@code DELETE}.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public static <E extends SQLElement<E>> int delete(Class<E> elemClass, SQLWhere<E> where) throws DBException {
        initTable(elemClass);

        if (where == null) {
            return truncateTable(elemClass);
        }

        ParameterizedSQLString whereData = where.toSQL();

        String sql = "DELETE FROM " + getTableName(elemClass)
                + " WHERE " + whereData.sqlString()
                + ";";
        List<Object> params = new ArrayList<>(whereData.parameters());

        return customUpdateStatement(sql, params);

    }



    /**
     * Execute a custom SQL update statement with the provided parameters.
     * @param sql the query in SQL language, passed to {@link Connection#prepareStatement(String)}.
     * @param params the parameters to put in the query. Uses {@link PreparedStatement#setObject(int, Object)}.
     * @return the value returned by {@link PreparedStatement#executeUpdate()}.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public static int customUpdateStatement(String sql, List<Object> params) throws DBException {
        try (Connection c = connection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            int i = 1;
            for (Object val : params) {
                if (val instanceof Enum<?>) val = ((Enum<?>) val).name();
                ps.setObject(i++, val);
            }
            Log.debug(ps.toString());

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }



    /**
     * Truncate provided table.
     * @param elemClass the class representing a table.
     * @param <E> the type representing the table.
     * @return The return value of {@link PreparedStatement#executeUpdate()}, for an SQL query {@code DELETE}.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public static <E extends SQLElement<E>> int truncateTable(Class<E> elemClass) throws DBException {
        try (Connection c = connection.getConnection();
             Statement stmt = c.createStatement()) {
            return stmt.executeUpdate("TRUNCATE `" + getTableName(elemClass) + "`");
        } catch(SQLException e) {
            throw new DBException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <E extends SQLElement<E>> E getElementInstance(ResultSet set, Class<E> elemClass) throws DBException {
        try {
            E instance = Reflect.ofClass(elemClass).constructor(int.class).instanciate(set.getInt("id"));

            int fieldCount = set.getMetaData().getColumnCount();

            for (int c = 1; c <= fieldCount; c++) {
                String fieldName = set.getMetaData().getColumnLabel(c);

                // ignore when field is present in database but not handled by SQLElement instance
                if (!instance.getFields().containsKey(fieldName)) continue;

                SQLField<E, Object> sqlField = (SQLField<E, Object>) instance.getFields().get(fieldName);

                boolean customType = sqlField.type instanceof SQLCustomType;

                Object val = set.getObject(c,
                        (Class<?>)(customType ? ((SQLCustomType<?, ?>)sqlField.type).intermediateJavaType
                                : sqlField.type.getJavaType()));

                if (val == null || set.wasNull()) {
                    instance.set(sqlField, null, false);
                }
                else {
                    if (customType) {
                        try {
                            val = ((SQLCustomType<Object, Object>)sqlField.type).dbToJavaConv.apply(val);
                        } catch (Exception e) {
                            throw new DBException("Error while converting value of field '"+sqlField.getName()+"' with SQLCustomType from "+((SQLCustomType<Object, Object>)sqlField.type).intermediateJavaType
                                    +"(jdbc source) to "+sqlField.type.getJavaType()+"(java destination). The original value is '"+ val +"'", e);
                        }
                    }

                    /*
                     * The value from the DB is marked as not-modified in the entry instance since this boolean is set
                     * only when the value differs from the DB.
                     */
                    instance.set(sqlField, val, false);
                    instance.modifiedSinceLastSave.remove(sqlField.getName());

                }
            }

            if (!instance.isValidForSave()) throw new DBException(
                    "This SQLElement representing a database entry is not valid for save : " + instance);

            return instance;
        } catch (ReflectiveOperationException | IllegalArgumentException | SecurityException | SQLException e) {
            throw new DBException("Can't instanciate " + elemClass.getName(), e);
        }
    }

    private DB() {}

}
