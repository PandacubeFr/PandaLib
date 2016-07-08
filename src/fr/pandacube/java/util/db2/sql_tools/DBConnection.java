package fr.pandacube.java.util.db2.sql_tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
	Connection conn;
	String url;
	String login;
	String pass;
	
	public DBConnection(String host, int port, String dbname, String l, String p) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		url = "jdbc:mysql://"+host+":"+port+"/"+dbname;
		login = l;
		pass = p;
		conn = DriverManager.getConnection(url, login, pass);
		
		
	}
	
	
	public void reconnectIfNecessary() throws SQLException
	{
		try
		{
			Statement stmt = conn.createStatement();
			stmt.close();
		}
		catch(SQLException e)
		{
			close();
			conn = DriverManager.getConnection(url, login, pass);
		}
	}
	
	public Connection getNativeConnection() throws SQLException
	{
		if (!conn.isValid(1))
			reconnectIfNecessary();
		return conn;
	}
	
	
	public void close() {
		try {
			conn.close();
		} catch (Exception e) { }
		
	}
	
}
