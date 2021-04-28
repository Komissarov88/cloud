package my.cloud.client.service;

import command.domain.Command;
import command.domain.CommandCode;
import io.netty.channel.Channel;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

public interface NetworkService {

    void uploadFile(File file);
    void downloadFile(Path file);
    void closeConnection();
    boolean isConnected();
    void connect(String login, String password);
    Path getCurrentPath();
    void submitConnection(Runnable connection);
    void setCommandCodeListener(CommandCode code, Consumer<String[]> listener);

    void requestFileList();
}
