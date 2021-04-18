package my.cloud.server.factory;

import my.cloud.server.service.ClientService;
import my.cloud.server.service.CommandDictionaryService;
import my.cloud.server.service.CommandService;
import my.cloud.server.service.ServerService;
import my.cloud.server.service.impl.CommandDictionaryServiceImpl;
import my.cloud.server.service.impl.NettyServerService;
import my.cloud.server.service.impl.command.ViewFilesInDirCommand;

import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class Factory {

    public static ServerService getServerService() {
        return NettyServerService.getInstance();
    }

    public static ClientService getClientService(Socket socket) {
        return null;
    }

    public static CommandDictionaryService getCommandDirectoryService() {
        return new CommandDictionaryServiceImpl();
    }

    public static List<CommandService> getCommandServices() {
        return Arrays.asList(new ViewFilesInDirCommand());
    }

}
