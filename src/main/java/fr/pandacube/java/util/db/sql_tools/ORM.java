package fr.pandacube.java.util.db.sql_tools;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import fr.pandacube.java.util.EnumUtil;
import fr.pandacube.java.util.Log;
import fr.pandacube.java.util.db.SQLContact;
import fr.pandacube.java.util.db.SQLForumCategorie;
import fr.pandacube.java.util.db.SQLForumForum;
import fr.pandacube.java.util.db.SQLForumPost;
import fr.pandacube.java.util.db.SQLForumThread;
import fr.pandacube.java.util.db.SQLLoginHistory;
import fr.pandacube.java.util.db.SQLLoginKickHistory;
import fr.pandacube.java.util.db.SQLMPGroup;
import fr.pandacube.java.util.db.SQLMPGroupUser;
import fr.pandacube.java.util.db.SQLMPMessage;
import fr.pandacube.java.util.db.SQLModoHistory;
import fr.pandacube.java.util.db.SQLOnlineshopHistory;
import fr.pandacube.java.util.db.SQLPingHistory;
import fr.pandacube.java.util.db.SQLPlayer;
import fr.pandacube.java.util.db.SQLPlayerIgnore;
import fr.pandacube.java.util.db.SQLShopStock;
import fr.pandacube.java.util.db.SQLStaffTicket;
import fr.pandacube.java.util.db.SQLStaticPages;
import fr.pandacube.java.util.db.SQLUUIDPlayer;
import fr.pandacube.java.util.db.sql_tools.SQLWhereChain.SQLBoolOp;
import fr.pandacube.java.util.db.sql_tools.SQLWhereComp.SQLComparator;
import org.javatuples.Pair;

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

	public synchronized static void init(DBConnection conn) {

		connection = conn;

		/*
		 * Les tables à initialiser
		 * utile des les initialiser ici, car on peut tout de suite déceler les
		 * bugs ou erreurs dans la déclaration des SQLFields
		 */

		try {
			initTable(SQLContact.class);
			initTable(SQLForumCategorie.class);
			initTable(SQLForumForum.class);
			initTable(SQLForumPost.class);
			initTable(SQLForumThread.class);
			initTable(SQLLoginHistory.class);
			initTable(SQLLoginKickHistory.class);
			initTable(SQLModoHistory.class);
			initTable(SQLMPGroup.class);
			initTable(SQLMPGroupUser.class);
			initTable(SQLMPMessage.class);
			initTable(SQLOnlineshopHistory.class);
			initTable(SQLPingHistory.class);
			initTable(SQLPlayer.class);
			initTable(SQLPlayerIgnore.class);
			initTable(SQLShopStock.class);
			initTable(SQLStaffTicket.class);
			initTable(SQLStaticPages.class);
			initTable(SQLUUIDPlayer.class);
		} catch (ORMInitTableException e) {
			Log.getLogger().log(Level.SEVERE, "Erreur d'initialisation d'une table dans l'ORM", e);
		}

	}

	/* package */ static synchronized <E extends SQLElement<E>> void initTable(Class<E> elemClass) throws ORMInitTableException {
		if (tables.contains(elemClass)) return;
		try {
			tables.add(elemClass);
			//Log.info("Start Init SQL table "+elemClass.getSimpleName());
			E instance = elemClass.newInstance();
			String tableName = instance.tableName();
			if (!tableExist(tableName)) createTable(instance);
			//Log.info("End init SQL table "+elemClass.getSimpleName());
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
		PreparedStatement ps = connection.getNativeConnection().prepareStatement(sql);
		int i = 1;
		for (Object val : params)
			ps.setObject(i++, val);
		try {
			Log.info("Creating table " + elem.tableName() + ":\n" + ps.toString());
			ps.executeUpdate();
		} finally {
			ps.close();
		}
	}

	private static boolean tableExist(String tableName) throws SQLException {
		ResultSet set = null;
		boolean exist = false;
		try {
			set = connection.getNativeConnection().getMetaData().getTables(null, null, tableName, null);
			exist = set.next();
		} finally {
			if (set != null) set.close();
		}
		return exist;
	}

	@SuppressWarnings("unchecked")
	public static <E extends SQLElement<E>> SQLField<E, Integer> getSQLIdField(Class<E> elemClass)
			throws ORMInitTableException {
		initTable(elemClass);
		return (SQLField<E, Integer>) SQLElement.fieldsCache.get(elemClass).get("id");
	}

	public static <E extends SQLElement<E>> List<E> getByIds(Class<E> elemClass, Collection<Integer> ids)
			throws ORMException {
		return getByIds(elemClass, ids.toArray(new Integer[ids.size()]));
	}

	public static <E extends SQLElement<E>> List<E> getByIds(Class<E> elemClass, Integer... ids) throws ORMException {
		SQLField<E, Integer> idField = getSQLIdField(elemClass);
		SQLWhereChain where = new SQLWhereChain(SQLBoolOp.OR);
		for (Integer id : ids)
			if (id != null) where.add(new SQLWhereComp(idField, SQLComparator.EQ, id));
		return getAll(elemClass, where, new SQLOrderBy().addField(idField), 1, null);
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

			PreparedStatement ps = connection.getNativeConnection().prepareStatement(sql);

			try {

				int i = 1;
				for (Object val : params) {
					if (val instanceof Enum<?>) val = ((Enum<?>) val).name();
					ps.setObject(i++, val);
				}
				Log.debug(ps.toString());
				ResultSet set = ps.executeQuery();

				try {
					while (set.next())
						elmts.add(getElementInstance(set, elemClass));
				} finally {
					set.close();
				}
			} finally {
				ps.close();
			}

			return elmts;
		} catch (ReflectiveOperationException | SQLException e) {
			throw new ORMException(e);
		}

	}

	private static <E extends SQLElement<E>> E getElementInstance(ResultSet set, Class<E> elemClass) throws ORMException {
		try {
			E instance = elemClass.getConstructor(int.class).newInstance(set.getInt("id"));

			int fieldCount = set.getMetaData().getColumnCount();

			for (int c = 1; c <= fieldCount; c++) {
				String fieldName = set.getMetaData().getColumnLabel(c);
				if (!instance.getFields().containsKey(fieldName)) continue;
				// ignore when field is present in database but not handled by SQLElement instance
				@SuppressWarnings("unchecked")
				SQLField<E, Object> sqlField = (SQLField<E, Object>) instance.getFields().get(fieldName);
				if (sqlField.type.getJavaType().isEnum()) {
					// JDBC ne supporte pas les enums
					String enumStrValue = set.getString(c);
					if (enumStrValue == null || set.wasNull()) instance.set(sqlField, null, false);
					else {
						Enum<?> enumValue = EnumUtil.searchUncheckedEnum(sqlField.type.getJavaType(), enumStrValue);
						if (enumValue == null) throw new ORMException("The enum constant '" + enumStrValue
								+ "' is not found in enum class " + sqlField.type.getJavaType().getName());
						instance.set(sqlField, enumValue, false);
					}
				}
				else {
					Object val = set.getObject(c, sqlField.type.getJavaType());
					if (val == null || set.wasNull()) instance.set(sqlField, null, false);
					else
						instance.set(sqlField, val, false);
				}

				// la valeur venant de la BDD est marqué comme "non modifié"
				// dans l'instance
				// car le constructeur de l'instance met tout les champs comme
				// modifiés
				instance.modifiedSinceLastSave.remove(sqlField.name);
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
