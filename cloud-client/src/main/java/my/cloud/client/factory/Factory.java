package my.cloud.client.factory;

import command.service.CommandDictionaryService;
import command.service.CommandService;
import command.service.impl.CommandDictionaryServiceImpl;
import my.cloud.client.service.NetworkService;
import my.cloud.client.service.impl.NettyNetworkService;
import utils.ClassInstanceSetBuilder;

public class Factory {

    private static CommandDictionaryService commandDictionaryService;

    public static NetworkService getNetworkService() {
        return NettyNetworkService.getInstance();
    }

    public static CommandDictionaryService getCommandDictionaryService() {
        if (commandDictionaryService == null) {
            return new CommandDictionaryServiceImpl(ClassInstanceSetBuilder.build(
                            "my.cloud.client.service.impl.commands", CommandService.class));
        }
        return commandDictionaryService;
    }
}
