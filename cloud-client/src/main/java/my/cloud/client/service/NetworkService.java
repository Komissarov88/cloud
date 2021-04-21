package my.cloud.client.service;

import command.Command;

import java.io.File;
import java.nio.file.Path;

public interface NetworkService {

    void sendCommand(Command command);
    void uploadFile(File file);
    void downloadFile(Path file);
    void closeConnection();
    void connect(String login, String password);
}
