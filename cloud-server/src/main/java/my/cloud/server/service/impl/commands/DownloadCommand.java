package my.cloud.server.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import command.service.CommandService;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import my.cloud.server.factory.Factory;
import utils.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static my.cloud.server.service.impl.commands.util.ServerCommandUtil.*;

/**
 * Called from download channel with authenticate key
 */
public class DownloadCommand implements CommandService {

    private boolean notCorrectCommand(ChannelHandlerContext ctx, String[] args) {
        return disconnectIfArgsLengthNotEqual(ctx, 2, args);
    }

    @Override
    public void processCommand(ChannelHandlerContext ctx, String[] args) {
        if (notCorrectCommand(ctx, args)) {
            return;
        }

        String key = args[0];
        String clientJobKey = args[1];
        Path path = Factory.getFileTransferAuthService().getTransferIfValid(key).destination;

        if (path != null) {
            ChunkedFile chunkedFile;
            if ((chunkedFile = getChunkedFile(path.toFile())) == null) {
                sendFailMessage(ctx,"can't read file");
                ctx.close();
            }
            sendFile(ctx, chunkedFile, clientJobKey, path);
        } else {
            sendFailMessage(ctx,"transfer channel authentication fails");
            ctx.close();
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

    private void sendFile(ChannelHandlerContext ctx, ChunkedFile cf, String clientJobKey, Path path) {
        try {
            Command readyCommand = new Command(CommandCode.DOWNLOAD_READY, clientJobKey);
            ctx.writeAndFlush(readyCommand).sync();
            ctx.pipeline().replace("ObjectEncoder", "Writer", new ChunkedWriteHandler());
            ctx.pipeline().removeLast();
            ctx.writeAndFlush(cf).addListener((ChannelFutureListener) future -> {
                Logger.info("download complete");
                ctx.close();
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.DOWNLOAD;
    }

}
