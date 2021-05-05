package my.cloud.server.service;

import io.netty.channel.Channel;

import java.nio.file.Path;

public interface ServerService {

    /**
     * Start server channel
     */
    void startServer();

    /**
     * Stop server
     */
    void stopServer();

    /**
     * @param channel client channel
     * @return true if channel is authenticated
     */
    boolean isUserLoggedIn(Channel channel);

    /**
     * @param channel user channel
     * @return path to user folder
     */
    Path getUserRootPath(Channel channel);

    /**
     * Add user to map of authenticated channels
     * @param login user
     * @param channel main client channel
     */
    void subscribeUser(String login, Channel channel);

    /**
     * User logged out
     * @param channel main client channel
     */
    void unsubscribeUser(Channel channel);

    /**
     * @param channel user main channel
     * @return min of server free space and user free space
     */
    long getUserFreeSpace(Channel channel);
}
