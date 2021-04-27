package my.cloud.client.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import command.service.CommandService;
import files.handler.FileReadHandler;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.client.factory.Factory;
import utils.Logger;

import java.nio.file.Path;

/**
 * Called right before ChunkedWriteHandler on server side starts working
 */
public class DownloadReadyCommand implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {
        Logger.info(command.toString());

        if (command.getArgs() == null
                || command.getArgs().length != 2) {
            Logger.warning("wrong arguments");
            return;
        }



        Path path = Factory.getFileTransferAuthService().getPathIfValid(command.getArgs()[0]);
        ctx.pipeline().replace("ObjectDecoder", "Reader", new FileReadHandler(path));
        ctx.pipeline().removeLast();
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.DOWNLOAD_READY;
    }
}
