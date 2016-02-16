package fr.pandacube.java.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ShopStockTable extends SQLTable<ShopStockElement> {

	public ShopStockTable() throws SQLException {
		super("pandacube_shop_stock");
	}

	@Override
	protected String createTableParameters() {
		return "id INT AUTO_INCREMENT PRIMARY KEY,"
				+ "material varchar(50) NOT NULL,"
				+ "damage int(11) NOT NULL DEFAULT '0',"
				+ "quantity double NOT NULL,"
				+ "server varchar(50) NOT NULL";
	}

	@Override
	protected ShopStockElement getElementInstance(ResultSet sqlResult) throws SQLException {
		return new ShopStockElement(sqlResult.getInt("id"),
				sqlResult.getString("material"),
				sqlResult.getShort("damage"),
				sqlResult.getDouble("quantity"),
				sqlResult.getString("server"));
	}

}
