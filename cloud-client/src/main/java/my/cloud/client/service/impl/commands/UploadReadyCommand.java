package my.cloud.client.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import command.service.CommandService;
import files.handler.ChannelWriteHandlerWithCallback;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import my.cloud.client.factory.Factory;
import utils.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Called when server ready to receive ChunkedWriteHandler data
 */
public class UploadReadyCommand implements CommandService {

    private ChunkedFile getChunkedFile(File file) {
        try {
            return new ChunkedFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {

        if (command.getArgs() == null
                || command.getArgs().length != 1) {
            Logger.warning("wrong arguments");
            return;
        }

        ChunkedFile cf;
        Path path = Factory.getFileTransferAuthService().getPathIfValid(command.getArgs()[0]);
        if (path == null || (cf = getChunkedFile(path.toFile())) == null) {
            Logger.warning("cant read file");
            ctx.close();
            return;
        }

        Factory.getUploadProgressService().add(path, path.toFile().length());
        ChannelWriteHandlerWithCallback transferListener = new ChannelWriteHandlerWithCallback(path);
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
    public CommandCode getCommandCode() {
        return CommandCode.UPLOAD_READY;
    }

}
