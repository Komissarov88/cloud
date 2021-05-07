package my.cloud.client.service.impl.commands;

import command.domain.CommandCode;
import command.service.CommandService;
import files.domain.TransferId;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.client.factory.Factory;

import java.util.function.Consumer;

import static my.cloud.client.service.impl.commands.util.ClientCommandUtil.*;

/**
 * Called when server cant accept upload
 */
public class FilesOfferRefusedCommand implements CommandService {

    private Consumer<String[]> consumer;

    @Override
    public void processCommand(ChannelHandlerContext ctx, String[] args) {
        if (wrongArgumentsLength(args, i -> i == 0)) {
            return;
        }

        TransferId t = Factory.getFileTransferAuthService().getTransferIfValid(args[0]);
        Factory.getUploadProgressService().remove(t.origin);

        for (int i = 1; i < args.length; i++) {
            Factory.getFileTransferAuthService().getTransferIfValid(args[i]);
        }

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
        return CommandCode.OFFER_REFUSED;
    }
}
