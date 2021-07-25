package fr.pandacube.lib.core.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import fr.pandacube.lib.core.util.Log;

public class DBConnection {
	private static final long CONNECTION_CHECK_TIMEOUT = 30000; // in ms
	
	private Connection conn;
	private String url;
	private String login;
	private String pass;
	
	private long timeOfLastCheck = 0;

	public DBConnection(String host, int port, String dbname, String l, String p)
			throws ClassNotFoundException, SQLException {
		//Class.forName("com.mysql.jdbc.Driver"); // apparently this is deprecated now
		url = "jdbc:mysql://" + host + ":" + port + "/" + dbname
				+ "?autoReconnect=true"
				+ "&useUnicode=true"
				+ "&useSSL=false"
				+ "&characterEncoding=utf8"
				+ "&characterSetResults=utf8"
				+ "&character_set_server=utf8mb4"
				+ "&character_set_connection=utf8mb4";
		login = l;
		pass = p;
		connect();
	}

	private void checkConnection() throws SQLException {
		if (!isConnected()) {
			Log.info("Connection to the database lost. Trying to reconnect...");
			close();
			connect();
		}
	}
	
	public boolean isConnected()
    {
        try {
    		if (conn.isClosed())
    			return false;

    		// avoid checking the connection everytime we want to do a db request
    		long now = System.currentTimeMillis();
    		if (timeOfLastCheck + CONNECTION_CHECK_TIMEOUT > now)
    			return true;
    		
			timeOfLastCheck = now;
    		
    		if (conn.isValid(1))
    			return true;
    		
        	try (ResultSet rs = conn.createStatement().executeQuery("SELECT 1;")) {
	            return rs == null ? false : rs.next();
            }
        } catch (Exception e) {
            return false;
        }
    }

	public Connection getNativeConnection() throws SQLException {
		checkConnection();
		return conn;
	}

	private void connect() throws SQLException {
		conn = DriverManager.getConnection(url, login, pass);
		timeOfLastCheck = System.currentTimeMillis();
	}

	public void close() {
		try {
			conn.close();
		} catch (Exception e) {}
	}

}
