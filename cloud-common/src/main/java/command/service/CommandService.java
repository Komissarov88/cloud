package command.service;

import command.domain.CommandCode;
import io.netty.channel.ChannelHandlerContext;

import java.util.function.Consumer;

/**
 * Action executed on Command message
 */
public interface CommandService {

    void processCommand(ChannelHandlerContext ctx, String[] args);
    CommandCode getCommandCode();
    default void setListener(Consumer<String[]> consumer){};
}
