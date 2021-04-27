package command.service;

import command.domain.Command;
import command.domain.CommandCode;
import io.netty.channel.ChannelHandlerContext;

import java.util.function.Consumer;

/**
 * Action executed on Command message
 */
public interface CommandService {

    void processCommand(Command command, ChannelHandlerContext ctx);
    CommandCode getCommandCode();
    default void setCallback(Consumer<String[]> consumer){};
}
