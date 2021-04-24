package my.cloud.client.service;

import command.domain.Command;
import io.netty.channel.Channel;

import java.io.File;
import java.nio.file.Path;

public interface NetworkService {

    void sendCommand(Command command);
    void uploadFile(File file);
    void downloadFile(Path file);
    void closeConnection();
    void connect(String login, String password);
    String getLogin();
    Path getCurrentPath();
    void setCurrentPath(Path path);
    void submitConnection(Runnable connection);
}
