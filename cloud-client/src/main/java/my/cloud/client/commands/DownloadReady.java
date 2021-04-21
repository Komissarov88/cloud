package my.cloud.client.commands;

import command.Command;
import command.CommandCode;
import command.CommandService;
import handler.FileReadHandler;
import io.netty.channel.ChannelHandlerContext;
import utils.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Called right before ChunkedWriteHandler on server side starts working
 */
public class DownloadReady implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {
        Logger.info(command.toString());

        if (command.getArgs() == null
                || command.getArgs().length != 1) {
            Logger.warning("wrong arguments");
            return;
        }

        Path path = Paths.get(command.getArgs()[0]);
        ctx.pipeline().replace("ObjectDecoder", "Reader", new FileReadHandler(path));
        ctx.pipeline().removeLast();
    }

    @Override
    public CommandCode getCommand() {
        return CommandCode.DOWNLOAD_READY;
    }

}