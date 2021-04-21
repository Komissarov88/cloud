package my.cloud.client.factory;

import command.CommandDictionaryService;
import command.impl.CommandDictionaryServiceImpl;
import my.cloud.client.service.NetworkService;
import my.cloud.client.service.impl.NettyNetworkService;

import java.util.Arrays;

public class Factory {

    private static CommandDictionaryService commandDictionaryService;

    public static NetworkService getNetworkService() {
        return NettyNetworkService.getInstance();
    }

    public static CommandDictionaryService getCommandDictionaryService() {
        if (commandDictionaryService == null) {
            return new CommandDictionaryServiceImpl(Arrays.asList(

            ));
        }
        return commandDictionaryService;
    }
}
