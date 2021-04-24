package my.cloud.server.service;

public interface DBService {

    long getSpaceLimit(String login);
    boolean addUser(String login, String nickname, String password);
    boolean login(String login, String password);
    void closeConnection();
}