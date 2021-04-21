package my.cloud.server.service;

import io.netty.channel.Channel;

public interface ServerService {

    void startServer();
    void stopServer();
    boolean isUserLoggedIn(Channel channel);
    void subscribeUser(String login, Channel channel);
    void unsubscribeUser(Channel channel);

}
