package my.cloud.server.factory;

import command.service.CommandDictionaryService;
import command.service.CommandService;
import files.service.FileTransferAuthService;
import files.service.impl.FileTransferAuthServiceImpl;
import my.cloud.server.service.*;
import my.cloud.server.service.impl.database.PostgresDBServiceImpl;
import command.service.impl.CommandDictionaryServiceImpl;
import my.cloud.server.service.impl.NettyServerServiceImpl;
import utils.ClassInstanceSetBuilder;
import utils.PropertiesReader;

public class Factory {

    private static CommandDictionaryService commandDictionaryService;

    public static ServerService getServerService() {
        return NettyServerServiceImpl.getInstance();
    }

    public static DBService getDbService() {
        return PostgresDBServiceImpl.getInstance();
    }

    public static CommandDictionaryService getCommandDictionaryService() {
        if (commandDictionaryService == null) {
            return new CommandDictionaryServiceImpl(ClassInstanceSetBuilder.build(
                    PropertiesReader.getProperty("command.package"), CommandService.class));
        }
        return commandDictionaryService;
    }

    public static FileTransferAuthService getFileTransferAuthService() {
        return FileTransferAuthServiceImpl.getInstance();
    }
}
