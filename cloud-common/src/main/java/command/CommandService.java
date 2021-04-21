package command;

import io.netty.channel.ChannelHandlerContext;

public interface CommandService {

    void processCommand(Command command, ChannelHandlerContext ctx);
    CommandCode getCommand();
}
