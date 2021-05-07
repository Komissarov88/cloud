package my.cloud.server.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import my.cloud.server.factory.Factory;
import my.cloud.server.service.impl.commands.base.BaseServerCommand;
import utils.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Called from download channel with authenticate key
 */
public class DownloadCommand extends BaseServerCommand {

    public DownloadCommand() {
        expectedArgumentsCountCheck = i -> i == 2;
        disconnectOnFail = true;
    }

    @Override
    protected void processArguments(String[] args) {
        String key = args[0];
        String clientJobKey = args[1];

        Path path = Factory.getFileTransferAuthService().getTransferIfValid(key).destination;
        if (path != null) {
            ChunkedFile chunkedFile;
            if ((chunkedFile = getChunkedFile(path.toFile())) == null) {
                sendFailMessage("cant read file");
                ctx.close();
            }
            sendFile(chunkedFile, clientJobKey, path);
        } else {
            sendFailMessage("transfer channel authentication fails");
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

    private void sendFile(ChunkedFile cf, String clientJobKey, Path path) {
        try {
            Command readyCommand = new Command(
                    CommandCode.DOWNLOAD_READY,
                    clientJobKey);

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
