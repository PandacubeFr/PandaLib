package fr.pandacube.util.orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {
	private Connection conn;
	private String url;
	private String login;
	private String pass;
	
	private long timeOfLastCheck = 0;

	public DBConnection(String host, int port, String dbname, String l, String p)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		url = "jdbc:mysql://" + host + ":" + port + "/" + dbname
				+ "?autoReconnect=true"
				+ "&useUnicode=true"
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
			close();
			connect();
		}
	}
	
	public boolean isConnected()
    {
        boolean connected = false;

        try (ResultSet rs = conn.createStatement().executeQuery("SELECT 1;"))
        {
            if (rs == null)
                connected = false;
            else if (rs.next())
                connected = true;
        } catch (Exception e) {
            connected = false;
        }
        return connected;
    }

	public Connection getNativeConnection() throws SQLException {
		if (conn.isClosed())
			connect();
		long now = System.currentTimeMillis();
		if (timeOfLastCheck + 5000 > now) {
			timeOfLastCheck = now;
			if (!conn.isValid(1))
				checkConnection();
		}
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
