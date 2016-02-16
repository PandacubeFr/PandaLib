package fr.pandacube.java.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import fr.pandacube.java.util.db.ModoHistoryElement.ActionType;

public class ModoHistoryTable extends SQLTable<ModoHistoryElement> {
	
	

	public ModoHistoryTable() throws SQLException {
		super("pandacube_modo_history");
	}

	@Override
	protected String createTableParameters() {
		return "id INT AUTO_INCREMENT PRIMARY KEY,"
				+ "modoId CHAR(36) NULL," // null si c'est la console
				+ "actionType ENUM('BAN', 'UNBAN', 'MUTE', 'UNMUTE', 'REPORT', 'KICK') NOT NULL,"
				+ "time BIGINT NOT NULL,"
				+ "playerId CHAR(36) NOT NULL,"
				+ "value BIGINT NULL,"
				+ "message VARCHAR(512) NOT NULL";
	}

	@Override
	protected ModoHistoryElement getElementInstance(ResultSet sqlResult) throws SQLException {
		ModoHistoryElement el = new ModoHistoryElement(
				sqlResult.getInt("id"),
				sqlResult.getString("modoId"),
				ActionType.valueOf(sqlResult.getString("actionType")),
				sqlResult.getString("playerId"),
				sqlResult.getString("message"));
		el.setValue(sqlResult.getLong("value"));
		el.setTime(sqlResult.getLong("time"));
		return el;
	}

}
