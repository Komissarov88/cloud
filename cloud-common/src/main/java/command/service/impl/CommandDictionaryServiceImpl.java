package command.service.impl;

import command.domain.Command;
import command.domain.CommandCode;
import io.netty.channel.ChannelHandlerContext;
import command.service.CommandDictionaryService;
import command.service.CommandService;
import utils.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CommandDictionaryServiceImpl implements CommandDictionaryService {

    private final Map<CommandCode, CommandService> commandDictionary;

    public CommandDictionaryServiceImpl(Set<CommandService> commandServices) {

        Map<CommandCode, CommandService> commandDictionary = new HashMap<>();
        for (CommandService commandService : commandServices) {
            commandDictionary.put(commandService.getCommandCode(), commandService);
        }

        this.commandDictionary = Collections.unmodifiableMap(commandDictionary);
    }

    @Override
    public void processCommand(ChannelHandlerContext ctx, Command command) {
        CommandService c = commandDictionary.get(command.getCode());
        if (c == null) {
            Logger.warning("command not found");
            return;
        }
       c.processCommand(ctx, command.getArgs());
    }

    @Override
    public CommandService getCommandService(CommandCode code) {
        return commandDictionary.get(code);
    }
}
