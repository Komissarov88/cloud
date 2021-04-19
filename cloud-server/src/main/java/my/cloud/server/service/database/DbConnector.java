package my.cloud.server.service.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnector {

    private static int PORT = 5432;
    private static String HOST = "localhost";
    private static String DB = "cloud";
    private static String USER = "postgres";
    private static String PWD = "postgrespass";

    private Connection connection;

    public DbConnector() {
        connect();
    }

    public Connection getConnection() {
        return connection;
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB, USER, PWD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("db is down");
        }
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
