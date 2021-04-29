package my.cloud.client.service.impl;

import command.domain.Command;
import command.domain.CommandCode;
import my.cloud.client.factory.Factory;
import my.cloud.client.service.NetworkService;
import utils.PathUtils;
import utils.PropertiesReader;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class NettyNetworkServiceImpl implements NetworkService {

    private static NettyNetworkServiceImpl instance;
    private CloudConnection mainConnection;
    private String login;
    private ExecutorService executorService;
    private Path currentPath = Paths.get("./");

    private final int maximumConnections = Integer.parseInt(
            PropertiesReader.getProperty("client.connections.maximum"));

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
        if (executorService != null) {
            executorService.shutdownNow();
        }
        executorService = Executors.newFixedThreadPool(maximumConnections);
        submitConnection(mainConnection);
    }

    @Override
    public void downloadFile(Path file) {
        mainConnection.sendCommand(new Command(CommandCode.FILES_REQUEST, file.toString()));
    }

    @Override
    public void uploadFile(File file) {
        if (!file.canRead()) {
            return;
        }
        List<File> files = PathUtils.getFilesListRecursively(file.toPath());
        long size = PathUtils.getSize(files);

        String[] args = new String[files.size() * 2 + 1];
        args[0] = String.valueOf(size);
        Path folderToTransfer = file.toPath().getFileName();
        int i = 1;
        for (File f : files) {
            args[i++] = Factory.getFileTransferAuthService().add(f.toPath(), mainConnection.getChannel());
            Path serverPath = folderToTransfer.resolve(folderToTransfer.relativize(f.toPath())).normalize();
            args[i++] = serverPath.toString();
        }
        mainConnection.sendCommand(new Command(CommandCode.FILES_OFFER, args));
    }

    private void sendCommand(Command command) {
        if (isConnected()) {
            mainConnection.sendCommand(command);
        }
    }

    public String getLogin() {
        if (!isConnected()) {
            login = "";
        }
        return login;
    }

    public void setCurrentPath(Path path) {
        if (path.toFile().exists()) {
            currentPath = path;
        } else {
            throw new IllegalArgumentException("path does not exist");
        }
    }

    @Override
    public void closeConnection() {
        if (mainConnection != null) {
            mainConnection.disconnect();
            executorService.shutdown();
        }
    }

    @Override
    public boolean isConnected() {
        if (mainConnection == null) {
            return false;
        }
        return mainConnection.isConnected();
    }

    @Override
    public void submitConnection(Runnable connection) {
        executorService.submit(connection);
    }

    @Override
    public Path getCurrentPath() {
        return currentPath;
    }

    @Override
    public void setCommandCodeListener(CommandCode code, Consumer<String[]> listener) {
        Factory.getCommandDictionaryService().getCommandService(code).setListener(listener);
    }

    @Override
    public void requestFileList(String path) {
        sendCommand(new Command(CommandCode.LS, path));
    }
}
