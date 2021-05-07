package my.cloud.client.service.impl.commands;

import command.domain.CommandCode;
import files.domain.TransferId;
import my.cloud.client.factory.Factory;
import my.cloud.client.service.impl.commands.base.BaseClientCommand;

/**
 * Called when server cant accept upload
 */
public class FilesOfferRefusedCommand extends BaseClientCommand {

    public FilesOfferRefusedCommand() {
        expectedArgumentsCountCheck = i -> i > 0;
    }

    @Override
    protected void processArguments(String[] args) {
        TransferId t = Factory.getFileTransferAuthService().getTransferIfValid(args[0]);
        Factory.getUploadProgressService().remove(t.origin);

        for (int i = 1; i < args.length; i++) {
            Factory.getFileTransferAuthService().getTransferIfValid(args[i]);
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.OFFER_REFUSED;
    }
}
