package my.cloud.client.commands;

import command.Command;
import command.CommandCode;
import command.CommandService;
import io.netty.channel.ChannelHandlerContext;
import utils.Logger;

/**
 * Called when something goes wrong
 */
public class Fail implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {
        Logger.warning(command.toString());
    }

    @Override
    public CommandCode getCommand() {
        return CommandCode.FAIL;
    }

}
