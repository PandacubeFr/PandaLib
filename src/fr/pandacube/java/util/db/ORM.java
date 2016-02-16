package fr.pandacube.java.util.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <b>ORM = Object-Relational Mapping</b><br/>
 * Liste des tables avec leur classes :
 * <ul>
 * 	<li><code>LoginHistoryTable</code></li>
 * 	<li><code>ModoHistoryTable</code></li>
 * 	<li><code>StaffTicketTable</code></li>
 * 	<li><code>MPMessageTable</code></li>
 * 	<li><code>MPGroupTable</code></li>
 * 	<li><code>MPGroupUserTable</code></li>
 * 	<li><code>MPWebSessionTable</code></li>
 * 	<li><code>PlayerIgnoreTable</code></li>
 * </ul>
 * @author Marc Baloup
 *
 */
public final class ORM {
	
	@SuppressWarnings("rawtypes")
	private static List<SQLTable> tables = new ArrayList<SQLTable>();
	
	/* package */ static DBConnection connection;

	
	public synchronized static void init(DBConnection conn) {
		try {
			
			connection = conn;
			/*
			 * Les tables SQL sont à instancier ici !
			 */

			tables.add(new LoginHistoryTable());
			
			tables.add(new ModoHistoryTable());
			
			tables.add(new StaffTicketTable());

			tables.add(new MPMessageTable());
			tables.add(new MPGroupTable());
			tables.add(new MPGroupUserTable());
			tables.add(new PlayerTable());
			
			tables.add(new PlayerIgnoreTable());
			
			tables.add(new ShopStockTable());
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	@SuppressWarnings("rawtypes")
	public synchronized static <T extends SQLTable> T getTable(Class<T> c) {
		if (c == null) return null;
		for (SQLTable table : tables) {
			
			if (c.isAssignableFrom(table.getClass())) {
				return c.cast(table);
			}
		}
		return null;
	}
	
	
	
	
	
	

	
	private ORM() { } // rend la classe non instanciable
	
	
}
