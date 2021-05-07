package my.cloud.client.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import command.service.CommandService;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.client.factory.Factory;
import my.cloud.client.service.impl.CloudConnection;

import java.util.function.Consumer;

import static my.cloud.client.service.impl.commands.util.ClientCommandUtil.*;

/**
 * Called when server sends client authentication key for upload channel
 */
public class UploadPossibleCommand implements CommandService {

    private Consumer<String[]> consumer;

    @Override
    public void processCommand(ChannelHandlerContext ctx, String[] args) {
        if (wrongArgumentsLength(args, i -> i != 2)) {
            return;
        }

        CloudConnection uploadConnection = new CloudConnection(new Command(CommandCode.UPLOAD, args), null);
        Factory.getNetworkService().submitConnection(uploadConnection);

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
        return CommandCode.UPLOAD_POSSIBLE;
    }
}
