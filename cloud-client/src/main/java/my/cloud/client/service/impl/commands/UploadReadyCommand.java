package my.cloud.client.service.impl.commands;

import command.domain.CommandCode;
import command.service.CommandService;
import files.domain.TransferId;
import files.handler.ChannelWriteHandlerWithCallback;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import my.cloud.client.factory.Factory;
import utils.Logger;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import static my.cloud.client.service.impl.commands.util.ClientCommandUtil.*;

/**
 * Called when server ready to receive ChunkedWriteHandler data
 */
public class UploadReadyCommand implements CommandService {

    private Consumer<String[]> consumer;

    @Override
    public void processCommand(ChannelHandlerContext ctx, String[] args) {
        if (wrongArgumentsLength(args, i -> i != 1)) {
            ctx.close();
            return;
        }

        ChunkedFile cf;
        TransferId transferId = Factory.getFileTransferAuthService().getTransferIfValid(args[0]);
        if (transferId == null || (cf = getChunkedFile(transferId.destination.toFile())) == null) {
            Logger.warning("cant read file");
            ctx.close();
            return;
        }

        sendFile(ctx, transferId, cf);

        if (consumer != null) {
            consumer.accept(args);
        }
    }

    private ChunkedFile getChunkedFile(File file) {
        try {
            return new ChunkedFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendFile(ChannelHandlerContext ctx, TransferId transferId, ChunkedFile cf) {
        ChannelWriteHandlerWithCallback transferListener = new ChannelWriteHandlerWithCallback(transferId);
        transferListener.setTransferListener(Factory.getUploadProgressService()::increment);

        ctx.pipeline().replace("ObjectEncoder", "transferListener", transferListener);
        ctx.pipeline().addAfter("transferListener", "chunkedWriter", new ChunkedWriteHandler());
        ctx.pipeline().remove("MainInboundHandler");
        ctx.writeAndFlush(cf).addListener((ChannelFutureListener) future -> {
            Logger.info("upload complete");
            ctx.close();
        });
    }

    @Override
    public void setListener(Consumer<String[]> consumer) {
        this.consumer = consumer;
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.UPLOAD_READY;
    }

}
