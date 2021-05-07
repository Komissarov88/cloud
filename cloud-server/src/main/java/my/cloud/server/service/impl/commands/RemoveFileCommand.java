package my.cloud.server.service.impl.commands;

import command.domain.CommandCode;
import my.cloud.server.service.impl.commands.base.BaseServerCommand;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Called on delete request
 */
public class RemoveFileCommand extends BaseServerCommand {

    public RemoveFileCommand() {
        expectedArgumentsCountCheck = i -> i == 1;
    }

    @Override
    protected void processArguments(String[] args) {

        String request = args[0];
        File requestFile = getFileFromClientRequest(request);

        if (requestFile == null) {
            sendFailMessage("access violation");
            return;
        }

        if (FileUtils.deleteQuietly(requestFile)) {
            sendResponse(CommandCode.REFRESH_VIEW);
        } else {
            sendFailMessage("cant delete fail");
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.REMOVE_FILE;
    }

}
