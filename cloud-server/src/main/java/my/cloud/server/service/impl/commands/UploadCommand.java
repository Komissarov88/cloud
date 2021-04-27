package my.cloud.server.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import files.handler.FileReadHandler;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.server.factory.Factory;
import command.service.CommandService;
import utils.Logger;

import java.nio.file.Path;

/**
 * Called from upload channel with authenticate key
 */
public class UploadCommand implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {
        Logger.info(command.toString());

        if (command.getArgs() == null
                || command.getArgs().length != 2) {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "wrong arguments"));
            return;
        }

        String authKey = command.getArgs()[0];
        String clientJobKey = command.getArgs()[1];

        Path path = Factory.getFileTransferAuthService().getPathIfValid(authKey);
        if (path != null) {

            try {
                ctx.pipeline().replace(
                        "ObjectDecoder", "Reader", new FileReadHandler(path));
                ctx.writeAndFlush(new Command(CommandCode.UPLOAD_READY, clientJobKey)).sync();
                ctx.pipeline().removeLast();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }
        ctx.writeAndFlush(new Command(CommandCode.FAIL, "authentication fails"));
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.UPLOAD;
    }

}
