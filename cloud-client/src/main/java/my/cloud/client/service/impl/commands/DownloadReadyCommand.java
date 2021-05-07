package my.cloud.client.service.impl.commands;

import command.domain.CommandCode;
import files.domain.TransferId;
import files.handler.FileReadHandlerWithCallback;
import my.cloud.client.factory.Factory;
import my.cloud.client.service.impl.commands.base.BaseClientCommand;

/**
 * Called right before ChunkedWriteHandler on server side starts working
 */
public class DownloadReadyCommand extends BaseClientCommand {

    public DownloadReadyCommand() {
        expectedArgumentsCountCheck = i -> i == 1;
    }

    @Override
    protected void processArguments(String[] args) {
        TransferId transferId = Factory.getFileTransferAuthService().getTransferIfValid(args[0]);
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
