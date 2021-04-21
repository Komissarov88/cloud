package my.cloud.server.service.command.impl;

import command.Command;
import command.CommandCode;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.server.factory.Factory;
import my.cloud.server.service.CommandDictionaryService;
import my.cloud.server.service.command.CommandService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CommandDictionaryServiceImpl implements CommandDictionaryService {

    private final Map<CommandCode, CommandService> commandDictionary;

    private static CommandDictionaryServiceImpl instance;

    private static Logger logger = Logger.getLogger(CommandDictionaryServiceImpl.class.getName());

    public static CommandDictionaryServiceImpl getInstance() {
        if (instance == null) {
            instance = new CommandDictionaryServiceImpl();
        }
        return instance;
    }
    private CommandDictionaryServiceImpl() {
        commandDictionary = Collections.unmodifiableMap(getCommonDictionary());
    }

    private Map<CommandCode, CommandService> getCommonDictionary() {
        List<CommandService> commandServices = Factory.getCommandServices();

        Map<CommandCode, CommandService> commandDictionary = new HashMap<>();
        for (CommandService commandService : commandServices) {
            commandDictionary.put(commandService.getCommand(), commandService);
        }

        return commandDictionary;
    }

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {
        CommandService c = commandDictionary.get(command.getCode());
        if (c == null) {
            logger.warning("command not found");
            return;
        }
       c.processCommand(command, ctx);
    }

}
