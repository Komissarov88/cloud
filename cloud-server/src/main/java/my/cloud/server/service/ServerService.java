package my.cloud.server.service;

import io.netty.channel.Channel;

import java.nio.file.Path;

public interface ServerService {

    void startServer();
    void stopServer();
    boolean isUserLoggedIn(Channel channel);
    Path getUserRootPath(Channel channel);
    void subscribeUser(String login, Channel channel);
    void unsubscribeUser(Channel channel);
    long getUserFreeSpace(Channel channel);
}
