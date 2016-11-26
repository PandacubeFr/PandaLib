package fr.pandacube.java.util.db;

import fr.pandacube.java.util.db.sql_tools.SQLElement;
import fr.pandacube.java.util.db.sql_tools.SQLField;
import fr.pandacube.java.util.db.sql_tools.SQLType;

public class SQLShopStock extends SQLElement<SQLShopStock> {

	public SQLShopStock() {
		super();
	}

	public SQLShopStock(int id) {
		super(id);
	}

	@Override
	protected String tableName() {
		return "pandacube_shop_stock";
	}

	public static final SQLField<SQLShopStock, String> material = new SQLField<>("material", SQLType.VARCHAR(50), false);
	public static final SQLField<SQLShopStock, Integer> damage = new SQLField<>("damage", SQLType.INT, false, 0);
	public static final SQLField<SQLShopStock, Double> quantity = new SQLField<>("quantity", SQLType.DOUBLE, false);
	public static final SQLField<SQLShopStock, String> server = new SQLField<>("server", SQLType.VARCHAR(50), false);

}
