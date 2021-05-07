package my.cloud.server.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import command.service.CommandService;
import files.handler.FileReadHandlerWithCallback;
import files.domain.TransferId;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.server.factory.Factory;

import static my.cloud.server.service.impl.commands.util.ServerCommandUtil.*;

/**
 * Called from upload channel with authenticate key
 */
public class UploadCommand implements CommandService {

    private boolean notCorrectCommand(ChannelHandlerContext ctx, String[] args) {
        return wrongArgumentsLength(ctx, args, i -> i != 2);
    }

    @Override
    public void processCommand(ChannelHandlerContext ctx, String[] args) {

        if (notCorrectCommand(ctx, args)) {
            return;
        }

        String authKey = args[0];
        String clientJobKey = args[1];
        TransferId transferId = Factory.getFileTransferAuthService().getTransferIfValid(authKey);

        if (transferId == null) {
            sendFailMessage(ctx,"transfer channel authentication fails");
            return;
        }

        uploadReady(ctx, transferId, clientJobKey);
    }

    private void uploadReady(ChannelHandlerContext ctx, TransferId id, String clientJobKey) {
        try {
            ctx.pipeline().replace(
                    "ObjectDecoder", "Reader", new FileReadHandlerWithCallback(id));
            ctx.writeAndFlush(new Command(CommandCode.UPLOAD_READY, clientJobKey)).sync();
            ctx.pipeline().removeLast();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.UPLOAD;
    }


}
