package fr.pandacube.java.util.orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
	Connection conn;
	String url;
	String login;
	String pass;

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
		try {
			Statement stmt = conn.createStatement();
			stmt.close();
		} catch (SQLException e) {
			try {
				close();
			} catch (Exception ex) {}
			connect();
		}
	}

	public Connection getNativeConnection() throws SQLException {
		if (!conn.isValid(1)) reconnectIfNecessary();
		return conn;
	}

	private void connect() throws SQLException {
		conn = DriverManager.getConnection(url, login, pass);
	}

	public void close() {
		try {
			conn.close();
		} catch (Exception e) {}

	}

}