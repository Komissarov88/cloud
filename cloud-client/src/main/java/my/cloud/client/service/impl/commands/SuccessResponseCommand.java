package my.cloud.client.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import command.service.CommandService;
import io.netty.channel.ChannelHandlerContext;
import utils.Logger;

/**
 * Called on successful authentication
 */
public class SuccessResponseCommand implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {
        Logger.info(command.toString());
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.SUCCESS;
    }

}
