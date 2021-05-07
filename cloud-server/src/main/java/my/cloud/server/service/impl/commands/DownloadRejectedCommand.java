package my.cloud.server.service.impl.commands;

import command.domain.CommandCode;
import my.cloud.server.factory.Factory;
import my.cloud.server.service.impl.commands.base.BaseServerCommand;

/**
 * Called when client cant accept files
 */
public class DownloadRejectedCommand extends BaseServerCommand {

    public DownloadRejectedCommand() {
        isAuthNeeded = true;
        expectedArgumentsCountCheck = i -> i > 0;
    }

    @Override
    protected void processArguments(String[] args) {
        for (String arg : args) {
            Factory.getFileTransferAuthService().remove(arg);
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.OFFER_REFUSED;
    }

}
