package my.cloud.client.factory;

import command.service.CommandDictionaryService;
import command.service.CommandService;
import command.service.impl.CommandDictionaryServiceImpl;
import files.service.FileTransferAuthService;
import files.service.FileTransferProgressService;
import files.service.impl.FileTransferAuthServiceImpl;
import files.service.impl.FileTransferProgressServiceImpl;
import my.cloud.client.service.NetworkService;
import my.cloud.client.service.impl.NettyNetworkServiceImpl;
import utils.ClassInstanceSetBuilder;
import utils.PropertiesReader;

public class Factory {

    private static CommandDictionaryService commandDictionaryService;
    private static FileTransferProgressService uploadProgressService;
    private static FileTransferProgressService downloadProgressService;

    public static NetworkService getNetworkService() {
        return NettyNetworkServiceImpl.getInstance();
    }

    public static CommandDictionaryService getCommandDictionaryService() {
        if (commandDictionaryService == null) {
            commandDictionaryService = new CommandDictionaryServiceImpl(ClassInstanceSetBuilder.build(
                    PropertiesReader.getProperty("command.package"), CommandService.class));
        }
        return commandDictionaryService;
    }

    public static FileTransferAuthService getFileTransferAuthService() {
        return FileTransferAuthServiceImpl.getInstance();
    }

    public static FileTransferProgressService getUploadProgressService() {
        if (uploadProgressService == null) {
            uploadProgressService = new FileTransferProgressServiceImpl();
        }
        return uploadProgressService;
    }

    public static FileTransferProgressService getDownloadProgressService() {
        if (downloadProgressService == null) {
            downloadProgressService = new FileTransferProgressServiceImpl();
        }
        return downloadProgressService;
    }
}
