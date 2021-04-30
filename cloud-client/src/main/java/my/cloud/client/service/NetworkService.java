package my.cloud.client.service;

import command.domain.CommandCode;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

public interface NetworkService {

    void uploadFile(File file, Path serverPrefix);
    void downloadFile(Path file);
    void closeConnection();
    boolean isConnected();
    void connect(String login, String password);
    void submitConnection(Runnable connection);
    void setCommandCodeListener(CommandCode code, Consumer<String[]> listener);
    String getLogin();
    void requestFileList(String path);
}
