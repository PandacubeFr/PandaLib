package fr.pandacube.java.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class StaffTicketTable extends SQLTable<StaffTicketElement> {

	
	public StaffTicketTable() throws SQLException {
		super("pandacube_staff_ticket");
	}

	@Override
	protected String createTableParameters() {
		return "id INT AUTO_INCREMENT PRIMARY KEY,"
				+ "playerId CHAR(36) NOT NULL,"
				+ "message VARCHAR(1024) NOT NULL,"
				+ "creationTime BIGINT NOT NULL,"
				+ "staffPlayerId CHAR(36) NULL";
	}

	@Override
	protected StaffTicketElement getElementInstance(ResultSet sqlResult)
			throws SQLException {
		StaffTicketElement el = new StaffTicketElement(
				sqlResult.getInt("id"),
				sqlResult.getString("playerId"),
				sqlResult.getString("message"),
				sqlResult.getLong("creationTime"));
		String staffId = sqlResult.getString("staffPlayerId");
		el.setStaffPlayer((staffId == null) ? null : UUID.fromString(staffId));
		
		return el;
	}

}
