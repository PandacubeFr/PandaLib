package fr.pandacube.java.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class OnlineShopHistoryTable extends SQLTable<OnlineShopHistoryElement> {

	public OnlineShopHistoryTable() throws SQLException {
		super("pandacube_onlineshop_history");
	}

	@Override
	protected String createTableParameters() {
		return "id INT AUTO_INCREMENT PRIMARY KEY,"
				+ "time BIGINT NOT NULL,"
				+ "transactionId VARCHAR(255) NULL,"
				+ "sourceType ENUM('REAL_MONEY', 'BAMBOU') NOT NULL,"
				+ "sourcePlayerId CHAR(36) NULL,"
				+ "sourceQuantity DOUBLE NOT NULL,"
				+ "sourceName VARCHAR(64) NOT NULL,"
				+ "destType ENUM('BAMBOU', 'GRADE') NOT NULL,"
				+ "destPlayerId CHAR(36) NOT NULL,"
				+ "destQuantity DOUBLE NOT NULL,"
				+ "destName VARCHAR(64) NOT NULL";
	}

	@Override
	protected OnlineShopHistoryElement getElementInstance(ResultSet sqlResult) throws SQLException {
		OnlineShopHistoryElement el = new OnlineShopHistoryElement(
				sqlResult.getInt("id"),
				sqlResult.getLong("time"),
				sqlResult.getString("sourceType"),
				sqlResult.getDouble("sourceQuantity"),
				sqlResult.getString("sourceName"),
				sqlResult.getString("destType"),
				sqlResult.getString("destPlayerId"),
				sqlResult.getDouble("destQuantity"),
				sqlResult.getString("destName"));
		el.setTransactionId(sqlResult.getString("transactionId"));
		String sourcePlayerId = sqlResult.getString("sourcePlayerId");
		if (sourcePlayerId != null)
			el.setSourcePlayerId(UUID.fromString(sourcePlayerId));
		return el;
	}

}
