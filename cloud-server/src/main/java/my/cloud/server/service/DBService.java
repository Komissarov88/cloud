package my.cloud.server.service;

public interface DBService {

    /**
     * @param login user
     * @return space available to user
     */
    long getSpaceLimit(String login);

    /**
     * Register new user if absent
     * @param login user
     * @param password password
     * @return true on success
     */
    boolean addUser(String login, String password);

    /**
     * Look for user in db
     * @param login user
     * @param password password
     * @return true if login / password correct
     */
    boolean login(String login, String password);

    /**
     * Close database connection
     */
    void closeConnection();
}