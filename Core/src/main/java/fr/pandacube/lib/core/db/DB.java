package fr.pandacube.lib.core.db;

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

import org.javatuples.Pair;

import fr.pandacube.lib.core.util.Log;

/**
 * Static class to handle most of the database operations.
 * 
 * To use this database library, first call {@link #init(DBConnection)} with an appropriate {@link DBConnection},
 * they you can initialize every table you need for your application, using {@link #initTable(Class)}.
 *
 * @author Marc Baloup
 */
public final class DB {

	private static List<Class<? extends SQLElement<?>>> tables = new ArrayList<>();
	private static Map<Class<? extends SQLElement<?>>, String> tableNames = new HashMap<>();

	private static DBConnection connection;
	/* package */ static String tablePrefix = "";

	public static DBConnection getConnection() {
		return connection;
	}

	public synchronized static <E extends SQLElement<E>> void init(DBConnection conn, String tablePrefix) {
		connection = conn;
		DB.tablePrefix = Objects.requireNonNull(tablePrefix);
	}

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

		String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (";
		List<Object> params = new ArrayList<>();

		Collection<SQLField<E, ?>> tableFields = elem.getFields().values();
		boolean first = true;
		for (SQLField<E, ?> f : tableFields) {
			Pair<String, List<Object>> statementPart = f.forSQLPreparedStatement();
			params.addAll(statementPart.getValue1());

			if (!first) sql += ", ";
			first = false;
			sql += statementPart.getValue0();
		}

		sql += ", PRIMARY KEY id(id))";
		
		try (PreparedStatement ps = connection.getNativeConnection().prepareStatement(sql)) {
			int i = 1;
			for (Object val : params)
				ps.setObject(i++, val);
			Log.info("Creating table " + elem.tableName() + ":\n" + ps.toString());
			ps.executeUpdate();
		}
	}
	
	public static <E extends SQLElement<E>> String getTableName(Class<E> elemClass) throws DBException {
		initTable(elemClass);
		return tableNames.get(elemClass);
	}

	private static boolean tableExistInDB(String tableName) throws SQLException {
		boolean exist = false;
		try (ResultSet set = connection.getNativeConnection().getMetaData().getTables(null, null, tableName, null)) {
			exist = set.next();
		}
		return exist;
	}

	@SuppressWarnings("unchecked")
	public static <E extends SQLElement<E>> SQLField<E, Integer> getSQLIdField(Class<E> elemClass)
			throws DBInitTableException {
		initTable(elemClass);
		return (SQLField<E, Integer>) SQLElement.fieldsCache.get(elemClass).get("id");
	}

	public static <E extends SQLElement<E>> SQLElementList<E> getByIds(Class<E> elemClass, Integer... ids) throws DBException {
		return getByIds(elemClass, Arrays.asList(ids));
	}

	public static <E extends SQLElement<E>> SQLElementList<E> getByIds(Class<E> elemClass, Collection<Integer> ids)
			throws DBException {
		return getAll(elemClass, getSQLIdField(elemClass).in(ids), SQLOrderBy.asc(getSQLIdField(elemClass)), 1, null);
	}

	public static <E extends SQLElement<E>> E getById(Class<E> elemClass, int id) throws DBException {
		return getFirst(elemClass, getSQLIdField(elemClass).eq(id));
	}

	public static <E extends SQLElement<E>> E getFirst(Class<E> elemClass, SQLWhere<E> where)
			throws DBException {
		return getFirst(elemClass, where, null, null);
	}

	public static <E extends SQLElement<E>> E getFirst(Class<E> elemClass, SQLOrderBy<E> orderBy)
			throws DBException {
		return getFirst(elemClass, null, orderBy, null);
	}

	public static <E extends SQLElement<E>> E getFirst(Class<E> elemClass, SQLWhere<E> where, SQLOrderBy<E> orderBy)
			throws DBException {
		return getFirst(elemClass, where, orderBy, null);
	}

	public static <E extends SQLElement<E>> E getFirst(Class<E> elemClass, SQLWhere<E> where, SQLOrderBy<E> orderBy, Integer offset)
			throws DBException {
		SQLElementList<E> elts = getAll(elemClass, where, orderBy, 1, offset);
		return (elts.size() == 0) ? null : elts.get(0);
	}

	public static <E extends SQLElement<E>> SQLElementList<E> getAll(Class<E> elemClass) throws DBException {
		return getAll(elemClass, null, null, null, null);
	}

	public static <E extends SQLElement<E>> SQLElementList<E> getAll(Class<E> elemClass, SQLWhere<E> where) throws DBException {
		return getAll(elemClass, where, null, null, null);
	}

	public static <E extends SQLElement<E>> SQLElementList<E> getAll(Class<E> elemClass, SQLWhere<E> where,
			SQLOrderBy<E> orderBy) throws DBException {
		return getAll(elemClass, where, orderBy, null, null);
	}

	public static <E extends SQLElement<E>> SQLElementList<E> getAll(Class<E> elemClass, SQLWhere<E> where,
			SQLOrderBy<E> orderBy, Integer limit) throws DBException {
		return getAll(elemClass, where, orderBy, limit, null);
	}

	public static <E extends SQLElement<E>> SQLElementList<E> getAll(Class<E> elemClass, SQLWhere<E> where,
			SQLOrderBy<E> orderBy, Integer limit, Integer offset) throws DBException {
			SQLElementList<E> elmts = new SQLElementList<>();
			forEach(elemClass, where, orderBy, limit, offset, elmts::add);
			return elmts;
	}
	
	public static <E extends SQLElement<E>> void forEach(Class<E> elemClass, Consumer<E> action) throws DBException {
		forEach(elemClass, null, null, null, null, action);
	}
	
	public static <E extends SQLElement<E>> void forEach(Class<E> elemClass, SQLWhere<E> where,
			Consumer<E> action) throws DBException {
		forEach(elemClass, where, null, null, null, action);
	}
	
	public static <E extends SQLElement<E>> void forEach(Class<E> elemClass, SQLWhere<E> where,
			SQLOrderBy<E> orderBy, Consumer<E> action) throws DBException {
		forEach(elemClass, where, orderBy, null, null, action);
	}
	
	public static <E extends SQLElement<E>> void forEach(Class<E> elemClass, SQLWhere<E> where,
			SQLOrderBy<E> orderBy, Integer limit, Consumer<E> action) throws DBException {
		forEach(elemClass, where, orderBy, limit, null, action);
	}
	
	public static <E extends SQLElement<E>> void forEach(Class<E> elemClass, SQLWhere<E> where,
			SQLOrderBy<E> orderBy, Integer limit, Integer offset, Consumer<E> action) throws DBException {
		initTable(elemClass);

		try {
			String sql = "SELECT * FROM " + getTableName(elemClass);

			List<Object> params = new ArrayList<>();

			if (where != null) {
				Pair<String, List<Object>> ret = where.toSQL();
				sql += " WHERE " + ret.getValue0();
				params.addAll(ret.getValue1());
			}
			if (orderBy != null) sql += " ORDER BY " + orderBy.toSQL();
			if (limit != null) sql += " LIMIT " + limit;
			if (offset != null) sql += " OFFSET " + offset;
			sql += ";";
			
			try (ResultSet set = customQueryStatement(sql, params)) {
				while (set.next()) {
					E elm = getElementInstance(set, elemClass);
					action.accept(elm);
				}
			}
		} catch (SQLException e) {
			throw new DBException(e);
		}

	}
	
	
	
	public static <E extends SQLElement<E>> long count(Class<E> elemClass) throws DBException {
		return count(elemClass, null);
	}
	
	public static <E extends SQLElement<E>> long count(Class<E> elemClass, SQLWhere<E> where) throws DBException {
		initTable(elemClass);

		try {
			String sql = "SELECT COUNT(*) as count FROM " + getTableName(elemClass);

			List<Object> params = new ArrayList<>();

			if (where != null) {
				Pair<String, List<Object>> ret = where.toSQL();
				sql += " WHERE " + ret.getValue0();
				params.addAll(ret.getValue1());
			}
			sql += ";";
			
			try (ResultSet set = customQueryStatement(sql, params)) {
				if (set.next()) {
					return set.getLong(1);
				}
			}
		} catch (SQLException e) {
			throw new DBException(e);
		}
		
		throw new DBException("Can’t retrieve element count from database (The ResultSet may be empty)");

	}
	
	
	
	
	public static ResultSet customQueryStatement(String sql, List<Object> params) throws DBException {
		try {
			PreparedStatement ps = connection.getNativeConnection().prepareStatement(sql);
			int i = 1;
			for (Object val : params) {
				if (val instanceof Enum<?>) val = ((Enum<?>) val).name();
				ps.setObject(i++, val);
			}
			Log.debug(ps.toString());
			
			ResultSet rs = ps.executeQuery();
			
			ps.closeOnCompletion();
			
			return rs;
		} catch (SQLException e) {
			throw new DBException(e);
		}

	}

	

	
	public static <E extends SQLElement<E>> SQLUpdate<E> update(Class<E> elemClass, SQLWhere<E> where) throws DBException {
		return new SQLUpdate<>(elemClass, where);
	}
	
	/* package */ static <E extends SQLElement<E>> int update(Class<E> elemClass, SQLWhere<E> where, Map<SQLField<E, ?>, Object> values) throws DBException {
		return new SQLUpdate<>(elemClass, where, values).execute();
	}

	
	/**
	 * Delete the elements of the table represented by {@code elemClass} which meet the condition {@code where}.
	 * @param elemClass the SQLElement representing the table.
	 * @param where the condition to meet for an element to be deleted from the table. If null, the table is truncated using {@link #truncateTable(Class)}.
	 * @return The return value of {@link PreparedStatement#executeUpdate()}, for an SQL query {@code DELETE}.
	 * @throws DBException
	 */
	public static <E extends SQLElement<E>> int delete(Class<E> elemClass, SQLWhere<E> where) throws DBException {
		initTable(elemClass);
		
		if (where == null) {
			return truncateTable(elemClass);
		}
		
		Pair<String, List<Object>> whereData = where.toSQL();
		
		String sql = "DELETE FROM " + getTableName(elemClass)
				+ " WHERE " + whereData.getValue0()
				+ ";";
		List<Object> params = new ArrayList<>(whereData.getValue1());
		
		return customUpdateStatement(sql, params);

	}
	
	
	
	public static int customUpdateStatement(String sql, List<Object> params) throws DBException {
		try (PreparedStatement ps = connection.getNativeConnection().prepareStatement(sql)) {

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
	
	
	
	public static <E extends SQLElement<E>> int truncateTable(Class<E> elemClass) throws DBException {
        try (Statement stmt = connection.getNativeConnection().createStatement()) {
            return stmt.executeUpdate("TRUNCATE `" + getTableName(elemClass) + "`");
        } catch(SQLException e) {
        	throw new DBException(e);
        }
	}

	@SuppressWarnings("unchecked")
	private static <E extends SQLElement<E>> E getElementInstance(ResultSet set, Class<E> elemClass) throws DBException {
		try {
			E instance = elemClass.getConstructor(int.class).newInstance(set.getInt("id"));

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
									+"(jdbc source) to "+sqlField.type.getJavaType()+"(java destination). The original value is '"+val.toString()+"'", e);
						}
					}
					
					instance.set(sqlField, val, false);
					// la valeur venant de la BDD est marqué comme "non modifié"
					// dans l'instance car le constructeur de l'instance met
					// tout les champs comme modifiés
					instance.modifiedSinceLastSave.remove(sqlField.getName());
					
				}
			}

			if (!instance.isValidForSave()) throw new DBException(
					"This SQLElement representing a database entry is not valid for save : " + instance.toString());

			return instance;
		} catch (ReflectiveOperationException | IllegalArgumentException | SecurityException | SQLException e) {
			throw new DBException("Can't instanciate " + elemClass.getName(), e);
		}
	}

	private DB() {}

}
