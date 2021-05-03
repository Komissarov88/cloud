package my.cloud.server.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import files.handler.FileReadHandlerWithCallback;
import files.domain.Transfer;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.server.factory.Factory;
import command.service.CommandService;

/**
 * Called from upload channel with authenticate key
 */
public class UploadCommand implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {

        if (command.getArgs() == null
                || command.getArgs().length != 2) {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "wrong arguments, expected keys pair"));
            return;
        }

        String authKey = command.getArgs()[0];
        String clientJobKey = command.getArgs()[1];

        Transfer transfer = Factory.getFileTransferAuthService().getTransferIfValid(authKey);
        if (transfer != null) {

            try {
                ctx.pipeline().replace(
                        "ObjectDecoder", "Reader", new FileReadHandlerWithCallback(transfer));
                ctx.writeAndFlush(new Command(CommandCode.UPLOAD_READY, clientJobKey)).sync();
                ctx.pipeline().removeLast();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }
        ctx.writeAndFlush(new Command(CommandCode.FAIL, "transfer channel authentication fails"));
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.UPLOAD;
    }

}
