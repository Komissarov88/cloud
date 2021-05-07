package my.cloud.client.service.impl.commands;

import command.domain.CommandCode;
import command.service.CommandService;
import io.netty.channel.ChannelHandlerContext;

import java.util.function.Consumer;

/**
 * Called when something goes wrong
 */
public class FailResponseCommand implements CommandService {

    private Consumer<String[]> consumer;

    @Override
    public void processCommand(ChannelHandlerContext ctx, String[] args) {
        if (consumer != null) {
            consumer.accept(args);
        }
    }

    @Override
    public void setListener(Consumer<String[]> consumer) {
        this.consumer = consumer;
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.FAIL;
    }
}
