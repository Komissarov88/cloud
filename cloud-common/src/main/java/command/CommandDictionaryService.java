package command;

import command.Command;
import io.netty.channel.ChannelHandlerContext;

public interface CommandDictionaryService {

    void processCommand(Command command, ChannelHandlerContext ctx);

}
