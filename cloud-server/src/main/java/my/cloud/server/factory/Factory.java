package my.cloud.server.factory;

import command.CommandDictionaryService;
import my.cloud.server.service.*;
import my.cloud.server.service.commands.*;
import my.cloud.server.service.database.PostgresService;
import command.impl.CommandDictionaryServiceImpl;
import my.cloud.server.service.files.FileJobService;
import my.cloud.server.service.impl.NettyServerService;

import java.util.Arrays;

public class Factory {

    private static CommandDictionaryService commandDictionaryService;

    public static ServerService getServerService() {
        return NettyServerService.getInstance();
    }

    public static DbService getDbService() {
        return PostgresService.getInstance();
    }

    public static CommandDictionaryService getCommandDictionaryService() {
        if (commandDictionaryService == null) {
            return new CommandDictionaryServiceImpl(Arrays.asList(
                    new ViewFilesInDirCommand(),
                    new AuthenticateUser(),
                    new Download(),
                    new DownloadRequest(),
                    new Upload(),
                    new UploadRequest()
            ));
        }
        return commandDictionaryService;
    }

    public static FileJobService getFileJobService() {
        return FileJobService.getInstance();
    }
}
