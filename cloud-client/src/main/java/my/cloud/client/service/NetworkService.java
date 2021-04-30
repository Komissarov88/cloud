package my.cloud.client.service;

import command.domain.CommandCode;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

public interface NetworkService {

    void uploadFile(File file, Path serverUploadDirectory);
    void downloadFile(Path file, Path clientDownloadDirectory);
    void closeConnection();
    boolean isConnected();
    void connect(String login, String password);
    void submitConnection(Runnable connection);
    void setCommandCodeListener(CommandCode code, Consumer<String[]> listener);
    void requestFileList(String path);
}
