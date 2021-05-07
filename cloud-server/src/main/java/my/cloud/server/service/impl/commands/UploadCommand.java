package my.cloud.server.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import files.handler.FileReadHandlerWithCallback;
import files.domain.TransferId;
import my.cloud.server.factory.Factory;
import my.cloud.server.service.impl.commands.base.BaseServerCommand;

/**
 * Called from upload channel with authenticate key
 */
public class UploadCommand extends BaseServerCommand {

    public UploadCommand() {
        expectedArgumentsCountCheck = i -> i == 2;
    }

    @Override
    protected void processArguments(String[] args) {
        String authKey = args[0];
        String clientJobKey = args[1];
        TransferId transferId = Factory.getFileTransferAuthService().getTransferIfValid(authKey);

        if (transferId == null) {
            sendFailMessage("transfer channel authentication fails");
            return;
        }

        uploadReady(transferId, clientJobKey);
    }

    private void uploadReady(TransferId id, String clientJobKey) {
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
