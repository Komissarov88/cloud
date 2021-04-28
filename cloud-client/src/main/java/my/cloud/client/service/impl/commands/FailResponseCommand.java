package my.cloud.client.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import command.service.CommandService;
import io.netty.channel.ChannelHandlerContext;

/**
 * Called when something goes wrong
 */
public class FailResponseCommand implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.FAIL;
    }

}
