package command;

import io.netty.channel.ChannelHandlerContext;

/**
 * Connects CommandCode and Class in commands package
 */
public interface CommandDictionaryService {

    void processCommand(Command command, ChannelHandlerContext ctx);

}
