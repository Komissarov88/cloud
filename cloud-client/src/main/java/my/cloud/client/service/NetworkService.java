package my.cloud.client.service;

import command.domain.Command;

import java.io.File;
import java.nio.file.Path;

public interface NetworkService {

    void sendCommand(Command command);
    void uploadFile(File file);
    void downloadFile(Path file);
    void closeConnection();
    void connect(String login, String password);
    String getLogin();
    Path getUserCurrentPath();
    void submitConnection(Runnable connection);
}
