package my.cloud.server.factory;

import my.cloud.server.service.*;
import my.cloud.server.service.command.CommandService;
import my.cloud.server.service.database.PostgresService;
import my.cloud.server.service.impl.CommandDictionaryServiceImpl;
import my.cloud.server.service.impl.NettyServerService;
import my.cloud.server.service.command.commands.ViewFilesInDirCommand;

import java.util.Arrays;
import java.util.List;

public class Factory {

    public static ServerService getServerService() {
        return NettyServerService.getInstance();
    }

    public static DbService getDbService() {
        return PostgresService.getInstance();
    }

    public static CommandDictionaryService getCommandDirectoryService() {
        return new CommandDictionaryServiceImpl();
    }

    public static List<CommandService> getCommandServices() {
        return Arrays.asList(new ViewFilesInDirCommand());
    }

}
