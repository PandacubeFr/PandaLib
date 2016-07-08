package fr.pandacube.java.util.db2.sql_tools;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import fr.pandacube.java.PandacubeUtil;
import fr.pandacube.java.util.db2.SQLPlayer;
import fr.pandacube.java.util.db2.sql_tools.SQLWhereChain.SQLBoolOp;
import fr.pandacube.java.util.db2.sql_tools.SQLWhereComp.SQLComparator;
import javafx.util.Pair;

/**
 * <b>ORM = Object-Relational Mapping</b>
 * @author Marc Baloup
 *
 */
public final class ORM {
	
	private static List<Class<? extends SQLElement>> tables = new ArrayList<>();
	
	private static DBConnection connection;
	
	public static DBConnection getConnection() {
		return connection;
	}
	
	
	public synchronized static void init(DBConnection conn) {
		
		connection = conn;
		
		/*
		 * Les tables à initialiser
		 * 
		 * utile des les initialiser ici, car on peut tout de suite déceler les bugs ou erreurs dans la déclaration des SQLFields
		 */
		
		initTable(SQLPlayer.class);
		
		
		
	}
	
	
	/* package */ static <T extends SQLElement> void initTable(Class<T> elemClass) {
		if (tables.contains(elemClass))
			return;
		try {
			T instance = elemClass.newInstance();
			String tableName = instance.tableName();
			if (!tableExist(tableName))
				createTable(instance);
			tables.add(elemClass);
		} catch (Exception e) {
			PandacubeUtil.getMasterLogger().log(Level.SEVERE, "Can't init table " + elemClass.getName(), e);
		}
	}
	
	

	
	private static <T extends SQLElement> void createTable(T elem) throws SQLException {
		
		String sql = "CREATE TABLE IF NOT EXISTS "+elem.tableName()+" (";
		List<Object> params = new ArrayList<>();
		
		Collection<SQLField<?>> tableFields = elem.getFields().values();
		boolean first = true;
		for (SQLField<?> f : tableFields) {
			Pair<String, List<Object>> statementPart = f.forSQLPreparedStatement();
			params.addAll(statementPart.getValue());
			
			if (!first) sql += ", ";
			first = false;
			sql += statementPart.getKey();
		}
		
		
		
		
		sql += ", PRIMARY KEY id(id))";
		PreparedStatement ps = connection.getNativeConnection().prepareStatement(sql);
		int i = 1;
		for (Object val : params) {
			ps.setObject(i++, val);
		}
		try {
			System.out.println(ps.toString());
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
			if (set != null)
				set.close();
		}
		return exist;
	}
	
	
	
	
	
	
	@SuppressWarnings("unchecked")
	public static <T extends SQLElement> SQLField<Integer> getSQLIdField(Class<T> elemClass) {
		return (SQLField<Integer>) SQLElement.fieldsCache.get(elemClass).get("id");
	}
	

	public static <T extends SQLElement> List<T> getByIds(Class<T> elemClass, Collection<Integer> ids) throws Exception {
		return getByIds(elemClass, ids.toArray(new Integer[ids.size()]));
	}
	
	public static <T extends SQLElement> List<T> getByIds(Class<T> elemClass, Integer... ids) throws Exception {
		SQLField<Integer> idField = getSQLIdField(elemClass);
		SQLWhereChain where = new SQLWhereChain(SQLBoolOp.OR);
		for (Integer id : ids)
			if (id != null)
				where.add(new SQLWhereComp(idField, SQLComparator.EQ, id));
		return getAll(elemClass, where, new SQLOrderBy().addField(idField), 1, null);
	}
	
	public static <T extends SQLElement> T getById(Class<T> elemClass, int id) throws Exception {
		return getFirst(elemClass, new SQLWhereComp(getSQLIdField(elemClass), SQLComparator.EQ, id), null);
	}
	
	public static <T extends SQLElement> T getFirst(Class<T> elemClass, SQLWhere where, SQLOrderBy orderBy) throws Exception {
		SQLElementList<T> elts = getAll(elemClass, where, orderBy, 1, null);
		return (elts.size() == 0)? null : elts.get(0);
	}
	
	
	
	public static <T extends SQLElement> SQLElementList<T> getAll(Class<T> elemClass) throws Exception {
		return getAll(elemClass, null, null, null, null);
	}
	
	public static <T extends SQLElement> SQLElementList<T> getAll(Class<T> elemClass, SQLWhere where, SQLOrderBy orderBy, Integer limit, Integer offset) throws Exception {
		initTable(elemClass);
		String sql = "SELECT * FROM "+elemClass.newInstance().tableName();
		List<Object> params = new ArrayList<>();

		if (where != null) {
			Pair<String, List<Object>> ret = where.toSQL();
			sql += " WHERE "+ret.getKey();
			params.addAll(ret.getValue());
		}
		if (orderBy != null)
			sql += " ORDER BY "+orderBy.toSQL();
		if (limit != null)
			sql += " LIMIT "+limit;
		if (offset != null)
			sql += " OFFSET "+offset;
		sql += ";";
		
		SQLElementList<T> elmts = new SQLElementList<T>();
		
		PreparedStatement ps = connection.getNativeConnection().prepareStatement(sql);
		
		try {
			
			int i = 1;
			for (Object val : params) {
				ps.setObject(i++, val);
			}
			
			System.out.println(ps.toString());
			
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
	}
	
	
	
	
	
	
	
	
	
	
	

	
	
	private static <T extends SQLElement> T getElementInstance(ResultSet set, Class<T> elemClass) throws Exception {
		try {
			T instance = elemClass.getConstructor(int.class).newInstance(set.getInt("id"));
			
			int fieldCount = set.getMetaData().getColumnCount();
			
			for (int c = 1; c<= fieldCount; c++) {
				String fieldName = set.getMetaData().getColumnLabel(c);
				if (!instance.getFields().containsKey(fieldName))
					continue; // ignore when field is present in database but not handled by SQLElement instance
				@SuppressWarnings("unchecked")
				SQLField<Object> sqlField = (SQLField<Object>) instance.getFields().get(fieldName);
				instance.set(sqlField, set.getObject(c), false);
			}
			
			return instance;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new Exception("Can't instanciate " + elemClass.getName(), e);
		} catch (SQLException e) {
			throw new Exception("Error reading ResultSet for creating instance of " + elemClass.getName(), e);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private ORM() { } // rend la classe non instanciable
	
	/*
	public static void main(String[] args) throws Throwable {
		ORM.init(new DBConnection("localhost", 3306, "pandacube", "pandacube", "pandacube"));
		
		List<SQLPlayer> players = ORM.getAll(SQLPlayer.class,
				new SQLWhereChain(SQLBoolOp.AND)
					.add(new SQLWhereNull(SQLPlayer.banTimeout, true))
					.add(new SQLWhereChain(SQLBoolOp.OR)
							.add(new SQLWhereComp(SQLPlayer.bambou, SQLComparator.EQ, 0L))
							.add(new SQLWhereComp(SQLPlayer.grade, SQLComparator.EQ, "default"))
					),
				new SQLOrderBy().addField(SQLPlayer.playerDisplayName), null, null);
		
		for(SQLPlayer p : players) {
			System.out.println(p.get(SQLPlayer.playerDisplayName));
		}
		
		
		// TODO LIST
		
		 * - Gérer mise à jour relative d'un champ (incrément / décrément)
		 
		
		
		
	}
	*/
	
	
	
	
	
	
	
	
	
	
}
