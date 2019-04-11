package fr.pandacube.java.util.orm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.javatuples.Pair;

import fr.pandacube.java.util.Log;
import fr.pandacube.java.util.orm.SQLWhereChain.SQLBoolOp;
import fr.pandacube.java.util.orm.SQLWhereComp.SQLComparator;

/**
 * <b>ORM = Object-Relational Mapping</b>
 *
 * @author Marc Baloup
 *
 */
public final class ORM {

	private static List<Class<? extends SQLElement<?>>> tables = new ArrayList<>();

	private static DBConnection connection;

	public static DBConnection getConnection() {
		return connection;
	}

	public synchronized static <E extends SQLElement<E>> void init(DBConnection conn) {

		connection = conn;


	}

	public static synchronized <E extends SQLElement<E>> void initTable(Class<E> elemClass) throws ORMInitTableException {
		if (tables.contains(elemClass)) return;
		try {
			tables.add(elemClass);
			Log.info("[ORM] Start Init SQL table "+elemClass.getSimpleName());
			E instance = elemClass.newInstance();
			String tableName = instance.tableName();
			if (!tableExist(tableName)) createTable(instance);
			Log.info("[ORM] End init SQL table "+elemClass.getSimpleName());
		} catch (Exception|ExceptionInInitializerError e) {
			throw new ORMInitTableException(elemClass, e);
		}
	}

	private static <E extends SQLElement<E>> void createTable(E elem) throws SQLException {

		String sql = "CREATE TABLE IF NOT EXISTS " + elem.tableName() + " (";
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

	private static boolean tableExist(String tableName) throws SQLException {
		boolean exist = false;
		try (ResultSet set = connection.getNativeConnection().getMetaData().getTables(null, null, tableName, null)) {
			exist = set.next();
		}
		return exist;
	}

	@SuppressWarnings("unchecked")
	public static <E extends SQLElement<E>> SQLField<E, Integer> getSQLIdField(Class<E> elemClass)
			throws ORMInitTableException {
		initTable(elemClass);
		return (SQLField<E, Integer>) SQLElement.fieldsCache.get(elemClass).get("id");
	}

	public static <E extends SQLElement<E>> SQLElementList<E> getByIds(Class<E> elemClass, Collection<Integer> ids)
			throws ORMException {
		return getByIds(elemClass, ids.toArray(new Integer[ids.size()]));
	}

	public static <E extends SQLElement<E>> SQLElementList<E> getByIds(Class<E> elemClass, Integer... ids) throws ORMException {
		SQLField<E, Integer> idField = getSQLIdField(elemClass);
		SQLWhereChain where = new SQLWhereChain(SQLBoolOp.OR);
		for (Integer id : ids)
			if (id != null) where.add(new SQLWhereComp(idField, SQLComparator.EQ, id));
		return getAll(elemClass, where, new SQLOrderBy().add(idField), 1, null);
	}

	public static <E extends SQLElement<E>> E getById(Class<E> elemClass, int id) throws ORMException {
		return getFirst(elemClass, new SQLWhereComp(getSQLIdField(elemClass), SQLComparator.EQ, id), null);
	}

	public static <E extends SQLElement<E>> E getFirst(Class<E> elemClass, SQLWhere where, SQLOrderBy orderBy)
			throws ORMException {
		SQLElementList<E> elts = getAll(elemClass, where, orderBy, 1, null);
		return (elts.size() == 0) ? null : elts.get(0);
	}

	public static <E extends SQLElement<E>> SQLElementList<E> getAll(Class<E> elemClass) throws ORMException {
		return getAll(elemClass, null, null, null, null);
	}

	public static <E extends SQLElement<E>> SQLElementList<E> getAll(Class<E> elemClass, SQLWhere where,
			SQLOrderBy orderBy, Integer limit, Integer offset) throws ORMException {
		initTable(elemClass);

		try {
			String sql = "SELECT * FROM " + elemClass.newInstance().tableName();

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

			SQLElementList<E> elmts = new SQLElementList<>();

			try (PreparedStatement ps = connection.getNativeConnection().prepareStatement(sql)) {

				int i = 1;
				for (Object val : params) {
					if (val instanceof Enum<?>) val = ((Enum<?>) val).name();
					ps.setObject(i++, val);
				}
				Log.debug(ps.toString());
				
				try (ResultSet set = ps.executeQuery()) {
					while (set.next())
						elmts.add(getElementInstance(set, elemClass));
				}
			}

			return elmts;
		} catch (ReflectiveOperationException | SQLException e) {
			throw new ORMException(e);
		}

	}
	
	
	
	public static <E extends SQLElement<E>> boolean truncateTable(Class<E> elemClass) throws ORMException {
		boolean success;
        try (Statement stmt = connection.getNativeConnection().createStatement()) {
            success = stmt.execute("TRUNCATE `" + elemClass.newInstance().tableName() + "`");
        } catch(SQLException | ReflectiveOperationException e) {
        	throw new ORMException(e);
        }
        return success;
	}
	
	
	public static ResultSet getCustomResult(String sql, List<Object> params) throws ORMException {
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
			throw new ORMException(e);
		}

	}

	@SuppressWarnings("unchecked")
	private static <E extends SQLElement<E>> E getElementInstance(ResultSet set, Class<E> elemClass) throws ORMException {
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
							throw new ORMException("Error while converting value of field '"+sqlField.getName()+"' with SQLCustomType from "+((SQLCustomType<Object, Object>)sqlField.type).intermediateJavaType
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

			if (!instance.isValidForSave()) throw new ORMException(
					"This SQLElement representing a database entry is not valid for save : " + instance.toString());

			return instance;
		} catch (ReflectiveOperationException | IllegalArgumentException | SecurityException | SQLException e) {
			throw new ORMException("Can't instanciate " + elemClass.getName(), e);
		}
	}

	private ORM() {} // rend la classe non instanciable

	/*
	 * public static void main(String[] args) throws Throwable {
	 * ORM.init(new DBConnection("localhost", 3306, "pandacube", "pandacube",
	 * "pandacube"));
	 * List<SQLPlayer> players = ORM.getAll(SQLPlayer.class,
	 * new SQLWhereChain(SQLBoolOp.AND)
	 * .add(new SQLWhereNull(SQLPlayer.banTimeout, true))
	 * .add(new SQLWhereChain(SQLBoolOp.OR)
	 * .add(new SQLWhereComp(SQLPlayer.bambou, SQLComparator.EQ, 0L))
	 * .add(new SQLWhereComp(SQLPlayer.grade, SQLComparator.EQ, "default"))
	 * ),
	 * new SQLOrderBy().addField(SQLPlayer.playerDisplayName), null, null);
	 * for(SQLPlayer p : players) {
	 * System.out.println(p.get(SQLPlayer.playerDisplayName));
	 * }
	 * // TODO mise à jour relative d'un champ (incrément / décrément)
	 * }
	 */

}
