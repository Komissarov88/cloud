package my.cloud.client.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import my.cloud.client.factory.Factory;
import my.cloud.client.service.impl.CloudConnection;
import my.cloud.client.service.impl.commands.base.BaseClientCommand;

/**
 * Called when server sends client authentication key for upload channel
 */
public class UploadPossibleCommand extends BaseClientCommand {

    public UploadPossibleCommand() {
        expectedArgumentsCountCheck = i -> i == 2;
    }

    @Override
    protected void processArguments(String[] args) {
        CloudConnection uploadConnection = new CloudConnection(new Command(CommandCode.UPLOAD, args), null);
        Factory.getNetworkService().submitConnection(uploadConnection);
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.UPLOAD_POSSIBLE;
    }
}
