package my.cloud.client.service.impl;

import command.domain.Command;
import command.domain.CommandCode;
import my.cloud.client.service.NetworkService;
import utils.PropertiesReader;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettyNetworkServiceImpl implements NetworkService {

    private static NettyNetworkServiceImpl instance;
    private CloudConnection mainConnection;
    private String login;
    private ExecutorService executorService;

    private final Path clientDataRoot = Paths.get(PropertiesReader.getProperty("client.data.root.path"));
    private final int maximumConnections = Integer.parseInt(
            PropertiesReader.getProperty("client.connections.count"));

    public static NetworkService getInstance() {
        if (instance == null) {
            instance = new NettyNetworkServiceImpl();
        }
        return instance;
    }

    private NettyNetworkServiceImpl() {
    }

    @Override
    public void connect(String login, String password) {
        if (isConnected()) {
            throw new RuntimeException("Channel already open");
        }
        this.login = login;
        mainConnection = new CloudConnection(new Command(CommandCode.AUTH, login, password));
        executorService = Executors.newFixedThreadPool(maximumConnections);
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
        executorService.shutdown();
    }

    public boolean isConnected() {
        if (mainConnection == null) {
            return false;
        }
        return mainConnection.isConnected();
    }

    @Override
    public String getLogin() {
        if (!isConnected()) {
            login = "";
        }
        return login;
    }

    @Override
    public void submitConnection(Runnable connection) {
        executorService.submit(connection);
    }

    @Override
    public Path getUserCurrentPath() {
        return Paths.get("./");
    }


}
