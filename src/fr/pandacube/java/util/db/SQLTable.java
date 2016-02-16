package fr.pandacube.java.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public abstract class SQLTable<T extends SQLElement> {

	DBConnection db = ORM.connection;
	
	private final String tableName;
	
	
	public SQLTable(String name) throws SQLException {
		tableName = name;
		
		if (!tableExist())
			createTable();
		
		
	}


	private void createTable() throws SQLException {
		Statement stmt = db.getConnection().createStatement();
		String sql = "CREATE TABLE IF NOT EXISTS "+tableName+" " +
            "("+createTableParameters()+")"; 
		try {
			stmt.executeUpdate(sql);
		} finally {
			stmt.close();
		}
	}


	private boolean tableExist() throws SQLException {
		ResultSet set = null;
		boolean exist = false;
		try {
			set = db.getConnection().getMetaData().getTables(null, null, tableName, null);
			exist = set.next();
		} finally {
			if (set != null)
				set.close();
		}
		return exist;
	}
	
	
	/**
	 * Retourne une chaine de caractère qui sera inclu dans la requête SQL de création de la table.
	 * La requête est de la forme : <code>CRATE TABLE tableName ();</code>
	 * La chaine retournée sera ajoutée entre les parenthèses.
	 */
	protected abstract String createTableParameters();
	
	
	
	/**
	 * Crée une instance de l'élément courant se trouvant dans le resultSet passé en paramètre
	 * @param sqlResult le set de résultat, déjà positionné sur un élément. Ne surtout pas appeler la méthode next() !
	 * @return
	 * @throws SQLException 
	 */
	protected abstract T getElementInstance(ResultSet sqlResult) throws SQLException;
	
	
	
	
	public String getTableName() {
		return tableName;
	}
	
	
	
	public T get(int id) throws SQLException {
		T elementInstance = null;
		Statement stmt = db.getConnection().createStatement();
		try {
			String sql = "SELECT * FROM "+tableName+" WHERE id = "+id+";";
	
			ResultSet set = stmt.executeQuery(sql);
			try {
				if (set.next())
					elementInstance = getElementInstance(set);
			} finally {
				set.close();
			}
		} finally {
			stmt.close();
		}
		return elementInstance;
	}
	
	
	
	public List<T> getAll() throws SQLException {
		return getAll(null, null, null, null);
	}
	
	
	
	public T getFirst(String where, String orderBy) throws SQLException {
		List<T> elts = getAll(where, orderBy, 1, null);
		return (elts.size() == 0)? null : elts.get(0);
	}
	
	
	public List<T> getAll(String where, String orderBy, Integer limit, Integer offset) throws SQLException {
		Statement stmt = db.getConnection().createStatement();
		String sql = "SELECT * FROM "+tableName;

		if (where != null)
			sql += " WHERE "+where;
		if (orderBy != null)
			sql += " ORDER BY "+orderBy;
		if (limit != null)
			sql += " LIMIT "+limit;
		if (offset != null)
			sql += " OFFSET "+offset;
		sql += ";";
		
		List<T> elmts = new ArrayList<T>();
		try {
			ResultSet set = stmt.executeQuery(sql);
			try {
				while (set.next())
					elmts.add(getElementInstance(set));
			} finally {
				set.close();
			}
		} finally {
			stmt.close();
		}
		return elmts;
	}
	
}
