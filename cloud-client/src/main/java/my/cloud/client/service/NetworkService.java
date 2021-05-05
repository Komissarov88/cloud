package my.cloud.client.service;

import command.domain.Command;
import command.domain.CommandCode;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public interface NetworkService {

    void uploadFile(Path serverUploadDirectory, List<Path> files);
    void removeFile(List<Path> path);
    void downloadFile(Path clientDownloadDirectory, List<Path> files);
    void closeConnection();
    boolean isConnected();
    void login(String login, String password);
    void submitConnection(Runnable connection);
    void setCommandCodeListener(CommandCode code, Consumer<String[]> listener);
    void setOnChannelInactive(Runnable onChannelInactive);
    void requestFileList(String path);
    void requestRegistration(String login, String password);
    void sendCommand(Command command);
}