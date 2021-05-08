package my.cloud.server.service.impl.database;

import my.cloud.server.service.DBService;
import utils.HashOperator;
import utils.Logger;
import utils.PropertiesReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgresDBServiceImpl implements DBService {

    private static PostgresDBServiceImpl dbService;
    private final DBConnector connector;
    private PreparedStatement addUserStatement;
    private PreparedStatement authStatement;
    private PreparedStatement spaceLimitStatement;

    public static DBService getInstance() {
        if (dbService == null) {
            dbService = new PostgresDBServiceImpl();
        }
        return dbService;
    }

    private String getStatementSql(String res) {
        String statementLocation = PropertiesReader.getProperty("db.statement.path") + res;
        try {
            InputStream stream =
                    (InputStream) getClass().getResource(statementLocation).getContent();
            StringBuilder sb = new StringBuilder();
            int x;
            while ((x = stream.read()) > -1) {
                sb.append((char) x);
            }
            stream.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
            Logger.error(statementLocation + " not found");
        }
        return "";
    }

    private PostgresDBServiceImpl() {
        connector = new DBConnector();
        try {
            addUserStatement = connector.getConnection().prepareStatement(
                    getStatementSql("add-user.sql"));
            authStatement = connector.getConnection().prepareStatement(
                    getStatementSql("authenticate-user.sql"));
            spaceLimitStatement = connector.getConnection().prepareStatement(
                    getStatementSql("get-space-limit.sql"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean login(String login, String password) {
        try {
            String hash = HashOperator.apply(password);
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
    public boolean addUser(String login, String password) {
        if (getSpaceLimit(login) > 0) {
            return false;
        }

        final long GIGABYTE = 1_073_741_824;
        long spaceLimit = GIGABYTE * Long.parseLong(PropertiesReader.getProperty("user.default.space.gb"));
        try {
            String hash = HashOperator.apply(password);
            addUserStatement.setString(1, login);
            addUserStatement.setString(2, hash);
            addUserStatement.setLong(3, spaceLimit);
            addUserStatement.executeQuery();
        } catch (SQLException e) {
            Logger.info(new String(e.getMessage().getBytes(StandardCharsets.UTF_8)));
        }
        return true;
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
            Logger.info(new String(e.getMessage().getBytes(StandardCharsets.UTF_8)));
        }
        return -1;
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
}
