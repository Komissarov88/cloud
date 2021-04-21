package my.cloud.server.service.command;

import command.Command;
import command.CommandCode;
import io.netty.channel.ChannelHandlerContext;

public interface CommandService {

    void processCommand(Command command, ChannelHandlerContext ctx);
    CommandCode getCommand();
}
