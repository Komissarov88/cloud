package my.cloud.server.factory;

import command.service.CommandDictionaryService;
import command.service.CommandService;
import files.service.FileJobService;
import files.service.impl.FileJobServiceImpl;
import my.cloud.server.service.*;
import my.cloud.server.service.impl.database.PostgresDBServiceImpl;
import command.service.impl.CommandDictionaryServiceImpl;
import my.cloud.server.service.impl.NettyServerServiceImpl;
import utils.ClassInstanceSetBuilder;

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
                    "my.cloud.server.service.impl.commands", CommandService.class));
        }
        return commandDictionaryService;
    }

    public static FileJobService getFileJobService() {
        return FileJobServiceImpl.getInstance();
    }
}
