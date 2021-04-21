package command;

import io.netty.channel.ChannelHandlerContext;

/**
 * Action executed on Command message
 */
public interface CommandService {

    void processCommand(Command command, ChannelHandlerContext ctx);
    CommandCode getCommand();
}
