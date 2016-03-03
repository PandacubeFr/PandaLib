package fr.pandacube.java.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerTable extends SQLTable<PlayerElement> {

	public PlayerTable() throws SQLException {
		super("pandacube_player");
	}

	@Override
	protected String createTableParameters() {
		return "id INT AUTO_INCREMENT PRIMARY KEY,"
				+ "playerId CHAR(36) NOT NULL,"
				+ "token CHAR(36) NULL,"
				+ "mailCheck VARCHAR(255) NULL,"
				+ "password VARCHAR(255) NULL,"
				+ "mail VARCHAR(255) NULL,"
				+ "playerDisplayName VARCHAR(255) NOT NULL,"
				+ "firstTimeInGame BIGINT NOT NULL,"
				+ "timeWebRegister BIGINT NULL,"
				+ "lastTimeInGame BIGINT NULL,"
				+ "lastWebActivity BIGINT NOT NULL,"
				+ "onlineInServer VARCHAR(32) NULL,"
				+ "skinURL VARCHAR(255) NULL,"
				+ "isVanish TINYINT NULL,"
				+ "birthday DATE NULL,"
				+ "lastYearCelebratedBirthday INT NOT NULL DEFAULT 0,"
				+ "banTimeout BIGINT NULL,"
				+ "muteTimeout BIGINT NULL,"
				+ "isWhitelisted TINYINT NOT NULL DEFAULT 0,"
				+ "bambou BIGINT NOT NULL DEFAULT 0,"
				+ "grade VARCHAR(36) NOT NULL DEFAULT 'default'";
	}

	@Override
	protected PlayerElement getElementInstance(ResultSet sqlResult)
			throws SQLException {
		PlayerElement el = new PlayerElement(
				sqlResult.getInt("id"),
				sqlResult.getString("playerId"),
				sqlResult.getString("playerDisplayName"),
				sqlResult.getLong("firstTimeInGame"),
				sqlResult.getLong("lastWebActivity"),
				sqlResult.getString("onlineInServer"));
		String token = sqlResult.getString("token");
		el.setToken((token == null) ? null : UUID.fromString(token));
		el.setMailCheck(sqlResult.getString("mailCheck"));
		el.setPasswordHash(sqlResult.getString("password"));
		el.setMail(sqlResult.getString("mail"));
		el.setFirstTimeInGame(sqlResult.getLong("firstTimeInGame"));
		el.setTimeWebRegister(sqlResult.getLong("timeWebRegister"));
		el.setLastTimeInGame(sqlResult.getLong("lastTimeInGame"));
		el.setSkinURL(sqlResult.getString("skinURL"));
		el.setVanish(sqlResult.getBoolean("isVanish"));
		el.setBirthday(sqlResult.getDate("birthday"));
		el.setLastYearCelebratedBirthday(sqlResult.getInt("lastYearCelebratedBirthday"));
		el.setBambou(sqlResult.getLong("bambou"));
		el.setGrade(sqlResult.getString("grade"));
		
		long longVal;

		longVal = sqlResult.getLong("banTimeout");
		el.setBanTimeout(sqlResult.wasNull()?null:longVal);
		longVal = sqlResult.getLong("muteTimeout");
		el.setMuteTimeout(sqlResult.wasNull()?null:longVal);
		
		el.setWhitelisted(sqlResult.getBoolean("isWhitelisted"));
		return el;
	}

}
