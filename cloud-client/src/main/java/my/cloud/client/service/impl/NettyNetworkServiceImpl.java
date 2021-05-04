package my.cloud.client.service.impl;

import command.domain.Command;
import command.domain.CommandCode;
import my.cloud.client.factory.Factory;
import my.cloud.client.service.NetworkService;
import utils.PathUtils;
import utils.PropertiesReader;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class NettyNetworkServiceImpl implements NetworkService {

    private static NettyNetworkServiceImpl instance;
    private CloudConnection mainConnection;
    private ExecutorService executorService;
    private Runnable onChannelInactive;

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

    private void connect(String login, String password, CommandCode code) {
        if (isConnected()) {
            throw new RuntimeException("Channel already open");
        }
        mainConnection = new CloudConnection(new Command(code, login, password), onChannelInactive);
        if (executorService != null) {
            executorService.shutdownNow();
        }
        executorService = Executors.newFixedThreadPool(maximumConnections);
        submitConnection(mainConnection);
    }

    @Override
    public void connect(String login, String password) {
        connect(login, password, CommandCode.AUTH);
    }

    @Override
    public void requestRegistration(String login, String password) {
        connect(login, password, CommandCode.REGISTER_REQUEST);
    }

    @Override
    public void downloadFile(Path file, Path clientDownloadDirectory) {
        mainConnection.sendCommand(new Command(CommandCode.FILES_REQUEST, file.toString(), clientDownloadDirectory.toString()));
    }

    @Override
    public void uploadFile(File file, Path serverUploadDirectory) {
        if (!file.canRead()) {
            return;
        }
        List<File> files = PathUtils.getFilesListRecursively(file.toPath());
        long size = PathUtils.getSize(files);

        Factory.getUploadProgressService().add(file.toPath(), size);

        String[] args = new String[files.size() * 2 + 1];
        args[0] = String.valueOf(size);
        Path folderToTransfer = file.toPath();
        int i = 1;
        for (File f : files) {
            args[i++] = Factory.getFileTransferAuthService().add(null, f.toPath(), mainConnection.getChannel());
            Path serverPath = folderToTransfer.getParent().relativize(f.toPath());
            args[i++] = serverUploadDirectory.resolve(serverPath).toString();
        }
        mainConnection.sendCommand(new Command(CommandCode.FILES_OFFER, args));
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
    public void setCommandCodeListener(CommandCode code, Consumer<String[]> listener) {
        Factory.getCommandDictionaryService().getCommandService(code).setListener(listener);
    }

    @Override
    public void setOnChannelInactive(Runnable onChannelInactive) {
        this.onChannelInactive = onChannelInactive;
    }

    @Override
    public void requestFileList(String path) {
        sendCommand(new Command(CommandCode.LS, path));
    }
}
