package fr.pandacube.java.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MPGroupTable extends SQLTable<MPGroupElement> {

	public MPGroupTable() throws SQLException {
		super("pandacube_mp_group");
	}

	@Override
	protected String createTableParameters() {
		return "id INT AUTO_INCREMENT PRIMARY KEY,"
				+ "groupName VARCHAR(16) NOT NULL";
	}

	@Override
	protected MPGroupElement getElementInstance(ResultSet sqlResult)
			throws SQLException {
		return new MPGroupElement(
				sqlResult.getInt("id"),
				sqlResult.getString("groupName"));
	}

}
