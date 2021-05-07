package my.cloud.client.service.impl;

import command.domain.Command;
import command.domain.CommandCode;
import my.cloud.client.factory.Factory;
import my.cloud.client.service.NetworkService;
import utils.Logger;
import utils.PathUtils;
import utils.PropertiesReader;

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedList;
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

    /**
     * Common part of login and register methods
     */
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
    public void removeFile(List<Path> path) {
        for (Path toDelete : path) {
            sendCommand(new Command(CommandCode.REMOVE_FILE, toDelete.toString()));
        }
    }

    @Override
    public void login(String login, String password) {
        if (!isConnected()) {
            connect(login, password, CommandCode.AUTH);
        } else {
            Logger.warning("server already connected");
        }
    }

    @Override
    public void requestRegistration(String login, String password) {
        if (!isConnected()) {
            connect(login, password, CommandCode.REGISTER_REQUEST);
        } else {
            Logger.warning("server already connected");
        }
    }

    @Override
    public void downloadFiles(Path clientDownloadDirectory, List<Path> files) {
        if (!isConnected()) {
            Logger.warning("Cant download, server not connected");
            return;
        }
        for (Path file : files) {
            mainConnection.sendCommand(new Command(CommandCode.FILES_REQUEST, file.toString(), clientDownloadDirectory.toString()));
        }
    }

    @Override
    public void uploadFiles(Path serverUploadDirectory, List<Path> files) {
        if (!isConnected()) {
            Logger.warning("Cant upload, server not connected");
            return;
        }
        files.stream().map(Path::toFile).forEach(file -> {
            if (!file.canRead()) {
                return;
            }
            List<File> dirContent = PathUtils.getFilesListRecursively(file.toPath());
            if (dirContent.isEmpty()) {
                return;
            }
            long size = PathUtils.getSize(dirContent);

            Factory.getUploadProgressService().add(file.toPath(), size);

            sendUploadOffer(serverUploadDirectory, file, dirContent, size);
        });
    }

    private void sendUploadOffer(Path serverUploadDirectory, File file, List<File> dirContent, long size) {
        List<String> args = new LinkedList<>();
        args.add(String.valueOf(size));
        Path folderToTransfer = file.toPath();
        for (File f : dirContent) {
            args.add(Factory.getFileTransferAuthService().add(file.toPath(), f.toPath(), mainConnection.getChannel()));
            Path serverPath = folderToTransfer.getParent().relativize(f.toPath());
            args.add(serverUploadDirectory.resolve(serverPath).toString());
        }
        mainConnection.sendCommand(new Command(CommandCode.FILES_OFFER, args.toArray(new String[0])));
    }

    @Override
    public void sendCommand(Command command) {
        if (isConnected()) {
            mainConnection.sendCommand(command);
        } else {
            Logger.warning("server not connected");
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
