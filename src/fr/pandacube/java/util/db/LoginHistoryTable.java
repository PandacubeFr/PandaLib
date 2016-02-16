package fr.pandacube.java.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import fr.pandacube.java.util.db.LoginHistoryElement.ActionType;

public class LoginHistoryTable extends SQLTable<LoginHistoryElement> {

	public LoginHistoryTable() throws SQLException {
		super("pandacube_login_history");
	}

	@Override
	protected String createTableParameters() {
		return "id INT AUTO_INCREMENT PRIMARY KEY,"
				+ "time BIGINT NOT NULL,"
				+ "playerId CHAR(36) NOT NULL,"
				+ "ip VARCHAR(128) NOT NULL,"
				+ "actionType ENUM('LOGIN', 'LOGOUT') NOT NULL,"
				+ "nbOnline INT NOT NULL";
	}

	@Override
	protected LoginHistoryElement getElementInstance(ResultSet sqlResult) throws SQLException {
		LoginHistoryElement el = new LoginHistoryElement(
				sqlResult.getInt("id"),
				sqlResult.getLong("time"),
				sqlResult.getString("playerId"),
				sqlResult.getString("ip"),
				ActionType.valueOf(sqlResult.getString("actionType")),
				sqlResult.getInt("nbOnline"));
		return el;
	}

}