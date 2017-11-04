package fr.pandacube.java.util.orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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

	public void reconnectIfNecessary() throws SQLException {
		try(Statement stmt = conn.createStatement()) {
		} catch (SQLException e) {
			close();
			connect();
		}
	}

	public Connection getNativeConnection() throws SQLException {
		if (conn.isClosed())
			connect();
		long now = System.currentTimeMillis();
		if (timeOfLastCheck + 5000 > now) {
			timeOfLastCheck = now;
			if (!conn.isValid(1))
				reconnectIfNecessary();
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
