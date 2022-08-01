package fr.pandacube.lib.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

/**
 * A class holding the connection to the database.
 */
public class DBConnection {

    private final BasicDataSource connSource;

    /**
     * Create a new connection with the provided settings.
     * @param host the MySQL DB host.
     * @param port the MySQL DB port.
     * @param dbname the MySQL DB name.
     * @param login the login/username.
     * @param password the password.
     */
    public DBConnection(String host, int port, String dbname, String login, String password) {
        this("jdbc:mysql://" + host + ":" + port + "/" + dbname
                        + "?useUnicode=true"
                        + "&useSSL=false"
                        + "&characterEncoding=utf8"
                        + "&characterSetResults=utf8"
                        + "&character_set_server=utf8mb4"
                        + "&character_set_connection=utf8mb4",
                login, password);
    }

    /**
     * Create a new connection with the provided settings.
     * @param url the JDBC URL.
     * @param login the login/username.
     * @param password the password.
     */
    public DBConnection(String url, String login, String password) {
        connSource = new BasicDataSource();
        connSource.setUrl(url);
        connSource.setUsername(login);
        connSource.setPassword(password);
    }

    /* package */ Connection getConnection() throws SQLException {
        return connSource.getConnection();
    }

    /**
     * Closes the connection.
     */
    public void close() {
        try {
            connSource.close();
        } catch (SQLException ignored) {}
    }

}
