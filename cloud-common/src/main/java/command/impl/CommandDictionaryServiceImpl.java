package command.impl;

import command.Command;
import command.CommandCode;
import io.netty.channel.ChannelHandlerContext;
import command.CommandDictionaryService;
import command.CommandService;
import utils.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandDictionaryServiceImpl implements CommandDictionaryService {

    private final Map<CommandCode, CommandService> commandDictionary;

    public CommandDictionaryServiceImpl(List<CommandService> commandServices) {

        Map<CommandCode, CommandService> commandDictionary = new HashMap<>();
        for (CommandService commandService : commandServices) {
            commandDictionary.put(commandService.getCommand(), commandService);
        }

        this.commandDictionary = Collections.unmodifiableMap(commandDictionary);
    }

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {
        CommandService c = commandDictionary.get(command.getCode());
        if (c == null) {
            Logger.warning("command not found");
            return;
        }
       c.processCommand(command, ctx);
    }

}
