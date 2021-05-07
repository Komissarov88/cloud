package my.cloud.client.service.impl.commands;

import command.domain.CommandCode;
import command.service.CommandService;
import files.domain.TransferId;
import files.handler.FileReadHandlerWithCallback;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.client.factory.Factory;

import java.util.function.Consumer;

import static my.cloud.client.service.impl.commands.util.ClientCommandUtil.*;

/**
 * Called right before ChunkedWriteHandler on server side starts working
 */
public class DownloadReadyCommand implements CommandService {

    private Consumer<String[]> consumer;

    @Override
    public void processCommand(ChannelHandlerContext ctx, String[] args) {
        if (wrongArgumentsLength(args, i -> i != 1)) {
            ctx.close();
            return;
        }

        TransferId transferId = Factory.getFileTransferAuthService().getTransferIfValid(args[0]);
        FileReadHandlerWithCallback fileReadHandler = new FileReadHandlerWithCallback(transferId);
        fileReadHandler.setTransferListener(Factory.getDownloadProgressService()::increment);

        ctx.pipeline().replace("ObjectDecoder", "Reader", fileReadHandler);
        ctx.pipeline().remove("MainInboundHandler");

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
        return CommandCode.DOWNLOAD_READY;
    }
}
