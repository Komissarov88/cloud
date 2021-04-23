package my.cloud.server.factory;

import command.service.CommandDictionaryService;
import command.service.CommandService;
import my.cloud.server.service.*;
import my.cloud.server.service.impl.database.PostgresService;
import command.service.impl.CommandDictionaryServiceImpl;
import my.cloud.server.service.impl.files.FileJobService;
import my.cloud.server.service.impl.NettyServerService;
import utils.ClassInstanceSetBuilder;

public class Factory {

    private static CommandDictionaryService commandDictionaryService;

    public static ServerService getServerService() {
        return NettyServerService.getInstance();
    }

    public static DBService getDbService() {
        return PostgresService.getInstance();
    }

    public static CommandDictionaryService getCommandDictionaryService() {
        if (commandDictionaryService == null) {
            return new CommandDictionaryServiceImpl(ClassInstanceSetBuilder.build(
                    "my.cloud.server.service.impl.commands", CommandService.class));
        }
        return commandDictionaryService;
    }

    public static FileJobService getFileJobService() {
        return FileJobService.getInstance();
    }
}
