package my.cloud.server.service;

public interface DbService {

    long getSpaceAvailable(String login);
    boolean addUser(String login, String nickname, String password);
    String login(String login, String password);
    void closeConnection();
}