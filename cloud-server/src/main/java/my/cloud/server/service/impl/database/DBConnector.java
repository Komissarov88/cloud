package my.cloud.server.service.impl.database;

import utils.PropertiesReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {

    private static int PORT = Integer.parseInt(PropertiesReader.getProperty("db.port"));
    private static String HOST = PropertiesReader.getProperty("db.address");
    private static String DB = PropertiesReader.getProperty("db.name");
    private static String USER = PropertiesReader.getProperty("db.user");
    private static String PWD = PropertiesReader.getProperty("db.password");

    private Connection connection;

    public DBConnector() {
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
