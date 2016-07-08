package fr.pandacube.java.util.db2;

import fr.pandacube.java.util.db2.sql_tools.SQLElement;
import fr.pandacube.java.util.db2.sql_tools.SQLField;
import fr.pandacube.java.util.db2.sql_tools.SQLType;

public class SQLShopStock extends SQLElement {
	
	
	public SQLShopStock() { super(); }
	public SQLShopStock(int id) { super(id); }

	@Override
	protected String tableName() { return "pandacube_shop_stock"; }
	
	
	public static final SQLField<String> material = new SQLField<>("material", SQLType.VARCHAR(50), false);
	public static final SQLField<Integer> damage = new SQLField<>("damage", SQLType.INT, false, 0);
	public static final SQLField<Double> quantity = new SQLField<>("quantity", SQLType.DOUBLE, false);
	public static final SQLField<String> server = new SQLField<>("server", SQLType.VARCHAR(50), false);
	
	
}
