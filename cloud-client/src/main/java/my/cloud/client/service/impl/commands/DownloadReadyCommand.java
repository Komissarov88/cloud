package my.cloud.client.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import command.service.CommandService;
import files.handler.FileReadHandlerWithCallback;
import files.domain.TransferId;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.client.factory.Factory;
import utils.Logger;

/**
 * Called right before ChunkedWriteHandler on server side starts working
 */
public class DownloadReadyCommand implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {

        if (command.getArgs() == null
                || command.getArgs().length != 2) {
            Logger.warning("wrong arguments");
            return;
        }

        TransferId transferId = Factory.getFileTransferAuthService().getTransferIfValid(command.getArgs()[0]);
        FileReadHandlerWithCallback fileReadHandler = new FileReadHandlerWithCallback(transferId);
        fileReadHandler.setTransferListener(Factory.getDownloadProgressService()::increment);

        ctx.pipeline().replace("ObjectDecoder", "Reader", fileReadHandler);
        ctx.pipeline().remove("MainInboundHandler");
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.DOWNLOAD_READY;
    }
}
