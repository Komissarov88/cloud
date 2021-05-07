package my.cloud.client.service.impl.commands;

import command.domain.CommandCode;
import files.domain.TransferId;
import files.handler.ChannelWriteHandlerWithCallback;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import my.cloud.client.factory.Factory;
import my.cloud.client.service.impl.commands.base.BaseClientCommand;
import utils.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Called when server ready to receive ChunkedWriteHandler data
 */
public class UploadReadyCommand extends BaseClientCommand {

    public UploadReadyCommand() {
        expectedArgumentsCountCheck = i -> i == 1;
        disconnectOnFail = true;
    }

    @Override
    protected void processArguments(String[] args) {
        ChunkedFile cf;
        TransferId transferId = Factory.getFileTransferAuthService().getTransferIfValid(args[0]);
        if (transferId == null || (cf = getChunkedFile(transferId.destination.toFile())) == null) {
            Logger.warning("cant read file");
            ctx.close();
            return;
        }

        sendFile(transferId, cf);
    }

    private ChunkedFile getChunkedFile(File file) {
        try {
            return new ChunkedFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendFile(TransferId transferId, ChunkedFile cf) {
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
    public CommandCode getCommandCode() {
        return CommandCode.UPLOAD_READY;
    }

}
