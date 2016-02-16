package fr.pandacube.java.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerIgnoreTable extends SQLTable<PlayerIgnoreElement> {

	public PlayerIgnoreTable() throws SQLException {
		super("pandacube_player_ignore");
	}

	@Override
	protected String createTableParameters() {
		return "id INT AUTO_INCREMENT PRIMARY KEY,"
				+ "ignorer CHAR(36) NOT NULL,"
				+ "ignored CHAR(36) NOT NULL";
	}

	@Override
	protected PlayerIgnoreElement getElementInstance(ResultSet sqlResult) throws SQLException {
		return new PlayerIgnoreElement(sqlResult.getInt("id"),
				sqlResult.getString("ignorer"),
				sqlResult.getString("ignored"));
	}
	
	
	
	public List<UUID> getListIgnoredPlayer(UUID ignore) throws SQLException {
		if (ignore == null)
			throw new IllegalArgumentException("ignore can't be null");
		
		List<PlayerIgnoreElement> dbIgnored = getAll("ignorer = '"+ignore+"'", "id", null, null);
		
		List<UUID> ret = new ArrayList<UUID>();
		
		for (PlayerIgnoreElement el : dbIgnored) {
			ret.add(el.getIgnored());
		}
		
		return ret;
		
	}
	

	public boolean isPlayerIgnoringPlayer(UUID ignore, UUID ignored) throws SQLException {
		if (ignore == null)
			throw new IllegalArgumentException("ignore can't be null");
		if (ignored == null)
			throw new IllegalArgumentException("ignored can't be null");

		return getFirst("ignorer = '"+ignore+"' AND ignored = '"+ignored+"'", "id") != null;
		
	}
	
	
	public void setPlayerIgnorePlayer(UUID ignore, UUID ignored, boolean set) throws SQLException {
		if (ignore == null)
			throw new IllegalArgumentException("ignore can't be null");
		if (ignored == null)
			throw new IllegalArgumentException("ignored can't be null");
		if (ignore.equals(ignored)) // on ne peut pas s'auto ignorer
			return;

		PlayerIgnoreElement el = getFirst("ignorer = '"+ignore+"' AND ignored = '"+ignored+"'", "id");
		
		if (set && el == null) {
			el = new PlayerIgnoreElement(ignore, ignored);
			el.save();
		}
		else if (!set && el != null) {
			el.delete();
		}
		
	}

}
