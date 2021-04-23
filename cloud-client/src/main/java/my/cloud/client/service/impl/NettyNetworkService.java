package my.cloud.client.service.impl;

import command.domain.Command;
import command.domain.CommandCode;
import my.cloud.client.service.NetworkService;

import java.io.File;
import java.nio.file.Path;

public class NettyNetworkService implements NetworkService {

    private static NettyNetworkService instance;
    private CloudConnection mainConnection;

    public static NetworkService getInstance() {
        if (instance == null) {
            instance = new NettyNetworkService();
        }
        return instance;
    }

    private NettyNetworkService() {
    }

    @Override
    public void connect(String login, String password) {
        if (isConnected()) {
            throw new RuntimeException("Channel already open");
        }
        mainConnection = new CloudConnection(new Command(CommandCode.AUTH, login, password));
        new Thread(mainConnection).start();
    }

    @Override
    public void downloadFile(Path file) {
        mainConnection.sendCommand(new Command(CommandCode.DOWNLOAD_REQUEST, file.toString()));
    }

    @Override
    public void uploadFile(File file) {
        if (!file.canRead()) {
            return;
        }
        String[] args = {file.toString(),
                String.valueOf(file.length())
        };
        mainConnection.sendCommand(new Command(CommandCode.UPLOAD_REQUEST, args));
    }

    @Override
    public void sendCommand(Command command) {
        if (isConnected()) {
            mainConnection.sendCommand(command);
        }
    }

    @Override
    public void closeConnection() {
        if (mainConnection != null) {
            mainConnection.disconnect();
        }
    }

    public boolean isConnected() {
        if (mainConnection == null) {
            return false;
        }
        return mainConnection.isConnected();
    }
}
