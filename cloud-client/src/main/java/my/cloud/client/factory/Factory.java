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

public class Factory {

    private static CommandDictionaryService commandDictionaryService;
    private static FileTransferProgressService clientProgressService;
    private static FileTransferProgressService serverProgressService;

    public static NetworkService getNetworkService() {
        return NettyNetworkServiceImpl.getInstance();
    }

    public static CommandDictionaryService getCommandDictionaryService() {
        if (commandDictionaryService == null) {
            return new CommandDictionaryServiceImpl(ClassInstanceSetBuilder.build(
                            "my.cloud.client.service.impl.commands", CommandService.class));
        }
        return commandDictionaryService;
    }

    public static FileTransferAuthService getFileTransferAuthService() {
        return FileTransferAuthServiceImpl.getInstance();
    }

    public static FileTransferProgressService getClientProgressService() {
        if (clientProgressService == null) {
            clientProgressService = new FileTransferProgressServiceImpl();
        }
        return clientProgressService;
    }

    public static FileTransferProgressService getServerProgressService() {
        if (serverProgressService == null) {
            serverProgressService = new FileTransferProgressServiceImpl();
        }
        return serverProgressService;
    }
}
