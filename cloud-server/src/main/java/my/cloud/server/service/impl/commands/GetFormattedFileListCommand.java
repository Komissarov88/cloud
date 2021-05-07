package my.cloud.server.service.impl.commands;

import command.domain.CommandCode;
import my.cloud.server.service.impl.commands.base.BaseServerCommand;
import utils.PathUtils;

import java.io.File;

/**
 * Return files list in server directory
 */
public class GetFormattedFileListCommand extends BaseServerCommand {

    public GetFormattedFileListCommand() {
        isAuthNeeded = true;
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

        String[] response = PathUtils.lsDirectory(requestFile.toPath(), getUserRootPath());
        if (response.length > 0) {
            sendResponse(CommandCode.LS, response);
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.LS;
    }
}
