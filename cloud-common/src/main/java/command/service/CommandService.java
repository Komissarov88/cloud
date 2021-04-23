package command.service;

import command.domain.Command;
import command.domain.CommandCode;
import io.netty.channel.ChannelHandlerContext;

/**
 * Action executed on Command message
 */
public interface CommandService {

    void processCommand(Command command, ChannelHandlerContext ctx);
    CommandCode getCommand();
}
