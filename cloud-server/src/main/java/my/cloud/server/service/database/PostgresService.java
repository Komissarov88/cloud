package my.cloud.server.service.database;

import my.cloud.server.service.DbService;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgresService implements DbService {

    private static PostgresService dbService;
    private DbConnector connector;
    private PreparedStatement addUserStatement;
    private PreparedStatement authStatement;

    public static DbService getInstance() {
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
        connector = new DbConnector();
        try {
            addUserStatement = connector.getConnection().prepareStatement(
                    getResourceContent("/db/add-user.sql"));
            authStatement = connector.getConnection().prepareStatement(
                    getResourceContent("/db/authenticate-user.sql"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String login(String login, String password) {
        try {
            MessageDigest msg = MessageDigest.getInstance("MD5");
            msg.update(password.getBytes());
            String hash = DatatypeConverter.printHexBinary(msg.digest());
            authStatement.setString(1, login);
            authStatement.setString(2, hash);
            ResultSet resultSet = authStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("nickname");
            }
        } catch (NoSuchAlgorithmException | SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public long getSpaceAvailable(String login) {
        return 0;
    }

    @Override
    public void closeConnection() {
        try {
            addUserStatement.close();
            authStatement.close();
            connector.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean addUser(String login, String nickname, String password) {
        try {
            MessageDigest msg = MessageDigest.getInstance("MD5");
            msg.update(password.getBytes());
            String hash = DatatypeConverter.printHexBinary(msg.digest());
            addUserStatement.setString(1, login);
            addUserStatement.setString(2, nickname);
            addUserStatement.setString(3, hash);
            addUserStatement.setInt(4, 1073741824);
            addUserStatement.executeQuery();
        } catch (NoSuchAlgorithmException | SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
