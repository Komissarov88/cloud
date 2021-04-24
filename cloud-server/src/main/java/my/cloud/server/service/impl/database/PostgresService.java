package my.cloud.server.service.impl.database;

import my.cloud.server.factory.Factory;
import my.cloud.server.service.DBService;
import utils.Hash;
import utils.PropertiesReader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgresService implements DBService {

    private static PostgresService dbService;
    private DBConnector connector;
    private PreparedStatement addUserStatement;
    private PreparedStatement authStatement;
    private PreparedStatement spaceLimitStatement;

    public static DBService getInstance() {
        if (dbService == null) {
            dbService = new PostgresService();
        }
        return dbService;
    }

    private String getResourceContent(String res) {
        String content = null;
        try {
            BufferedInputStream stream = (BufferedInputStream) getClass().getResource(res).getContent();
            StringBuilder sb = new StringBuilder();
            int x;
            while ((x = stream.read()) > -1) {
                sb.append((char) x);
            }
            stream.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private PostgresService() {
        connector = new DBConnector();
        try {
            addUserStatement = connector.getConnection().prepareStatement(
                    getResourceContent("/db/add-user.sql"));
            authStatement = connector.getConnection().prepareStatement(
                    getResourceContent("/db/authenticate-user.sql"));
            spaceLimitStatement = connector.getConnection().prepareStatement(
                    getResourceContent("/db/get-space-limit.sql"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean login(String login, String password) {
        try {
            String hash = Hash.get(password);
            authStatement.setString(1, login);
            authStatement.setString(2, hash);
            ResultSet resultSet = authStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public long getSpaceLimit(String login) {
        try {
            spaceLimitStatement.setString(1, login);
            ResultSet resultSet = spaceLimitStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void closeConnection() {
        try {
            addUserStatement.close();
            authStatement.close();
            spaceLimitStatement.close();
            connector.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean addUser(String login, String nickname, String password) {
        long spaceLimit = 1073741824 * Long.parseLong(PropertiesReader.getProperty("user.default.space.gb"));
        try {
            String hash = Hash.get(password);
            addUserStatement.setString(1, login);
            addUserStatement.setString(2, hash);
            addUserStatement.setLong(3, spaceLimit);
            addUserStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
