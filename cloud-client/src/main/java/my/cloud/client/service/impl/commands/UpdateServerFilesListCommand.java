package my.cloud.client.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import command.service.CommandService;
import io.netty.channel.ChannelHandlerContext;

import java.util.function.Consumer;

/**
 * Hold list of files in directory, sent with LS command
 */
public class UpdateServerFilesListCommand implements CommandService {

    private Consumer<String[]> consumer;

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {
        if (consumer != null) {
            consumer.accept(command.getArgs());
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.LS;
    }

    @Override
    public void setListener(Consumer<String[]> consumer) {
        this.consumer = consumer;
    }
}
