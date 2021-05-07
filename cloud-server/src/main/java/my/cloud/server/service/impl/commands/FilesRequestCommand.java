package my.cloud.server.service.impl.commands;

import command.domain.CommandCode;
import my.cloud.server.factory.Factory;
import my.cloud.server.service.impl.commands.base.BaseServerCommand;
import utils.PathUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 * Called when client want to download file
 */
public class FilesRequestCommand extends BaseServerCommand {

    public FilesRequestCommand() {
        isAuthNeeded = true;
        expectedArgumentsCountCheck = i -> i == 2;
    }

    @Override
    protected void processArguments(String[] args) {
        String request = args[0];
        File requestFile = getFileFromClientRequest(request);

        if (requestFile == null) {
            sendFailMessage("access violation");
            return;
        }

        String clientDownloadFolder = args[1];

        if (requestFile.canRead()) {
            sendConfirm(requestFile, clientDownloadFolder);
        } else {
            sendFailMessage("cant read file " + requestFile.getName());
        }
    }

    private void sendConfirm(File requestFile, String clientDownloadFolder) {

        List<File> files = PathUtils.getFilesListRecursively(requestFile.toPath());
        long size = PathUtils.getSize(files);

        List<String> response = new LinkedList<>();

        response.add(String.valueOf(size)); // total size
        response.add(String.valueOf(files.size())); // number of files
        response.add(clientDownloadFolder);

        Path rootUserPath = getUserRootPath();
        response.add(rootUserPath.relativize(requestFile.toPath()).toString()); // origin folder

        for (File f : files) {
            response.add(Factory.getFileTransferAuthService().add(null, f.toPath(), ctx.channel()));
            response.add(requestFile.getParentFile().toPath().relativize(f.toPath()).toString());
        }

        sendResponse(CommandCode.DOWNLOAD_POSSIBLE, response.toArray(new String[0]));
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.FILES_REQUEST;
    }

}
