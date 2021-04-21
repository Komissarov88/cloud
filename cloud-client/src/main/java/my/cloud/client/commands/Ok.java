package my.cloud.client.commands;

import command.Command;
import command.CommandCode;
import command.CommandService;
import io.netty.channel.ChannelHandlerContext;
import utils.Logger;

/**
 * Called on successful authentication
 */
public class Ok implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {
        Logger.info(command.toString());
    }

    @Override
    public CommandCode getCommand() {
        return CommandCode.OK;
    }

}
