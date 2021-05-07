package my.cloud.server.service.impl.commands;

import command.domain.CommandCode;
import command.service.CommandService;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.server.factory.Factory;
import utils.PathUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import static my.cloud.server.service.impl.commands.util.ServerCommandUtil.*;

/**
 * Called when client want to download file
 */
public class FilesRequestCommand implements CommandService {

    private boolean notCorrectCommand(ChannelHandlerContext ctx, String[] args) {
        return disconnectIfUnknown(ctx) || wrongArgumentsLength(ctx, args, i -> i != 2);
    }

    @Override
    public void processCommand(ChannelHandlerContext ctx, String[] args) {

        if (notCorrectCommand(ctx, args)) {
            return;
        }

        String request = args[0];
        File requestFile = getFileFromClientRequest(ctx, request);

        if (requestFile == null) {
            sendFailMessage(ctx,"access violation");
            return;
        }

        String clientDownloadFolder = args[1];

        if (requestFile.canRead()) {
            sendConfirm(ctx, requestFile, clientDownloadFolder);
        } else {
            sendFailMessage(ctx,"cant read file " + requestFile.getName());
        }
    }

    private void sendConfirm(ChannelHandlerContext ctx, File requestFile, String clientDownloadFolder) {

        List<File> files = PathUtils.getFilesListRecursively(requestFile.toPath());
        long size = PathUtils.getSize(files);

        List<String> response = new LinkedList<>();

        response.add(String.valueOf(size)); // total size
        response.add(String.valueOf(files.size())); // number of files
        response.add(clientDownloadFolder);

        Path rootUserPath = getUserRootPath(ctx);
        response.add(rootUserPath.relativize(requestFile.toPath()).toString()); // origin folder

        for (File f : files) {
            response.add(Factory.getFileTransferAuthService().add(null, f.toPath(), ctx.channel()));
            response.add(requestFile.getParentFile().toPath().relativize(f.toPath()).toString());
        }

        sendResponse(ctx, CommandCode.DOWNLOAD_POSSIBLE, response.toArray(new String[0]));
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.FILES_REQUEST;
    }

}
