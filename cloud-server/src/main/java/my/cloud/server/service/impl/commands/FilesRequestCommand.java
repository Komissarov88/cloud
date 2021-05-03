package my.cloud.server.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.server.factory.Factory;
import command.service.CommandService;
import utils.PathUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Called when client want to download file
 */
public class FilesRequestCommand implements CommandService {

    private void sendRequest(ChannelHandlerContext ctx, File file, String clientDownloadFolder) {
        List<File> files = PathUtils.getFilesListRecursively(file.toPath());
        long size = PathUtils.getSize(files);
        String[] response = new String[files.size() * 3 + 4];
        response[0] = String.valueOf(size); // total size
        response[1] = String.valueOf(files.size()); // number of files
        response[2] = clientDownloadFolder;

        Path rootUserPath = Factory.getServerService().getUserRootPath(ctx.channel());
        response[3] = rootUserPath.relativize(file.getParentFile().toPath()).toString(); // origin folder

        int i = 4;
        for (File f : files) {
            response[i++] = Factory.getFileTransferAuthService().add(null, f.toPath(), ctx.channel());
            response[i++] = file.getParentFile().toPath().relativize(f.toPath()).toString();
            response[i++] = String.valueOf(file.length());
        }
        ctx.writeAndFlush(new Command(CommandCode.DOWNLOAD_POSSIBLE, response));
    }

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {

        if (command.getArgs() == null
                || command.getArgs().length != 2
                || !Factory.getServerService().isUserLoggedIn(ctx.channel())) {
            ctx.writeAndFlush(new Command(CommandCode.FAIL,
                    "wrong arguments, expected wanted file and target client folder"));
            return;
        }

        Path rootUserPath = Factory.getServerService().getUserRootPath(ctx.channel());
        File requestFile = Paths.get(rootUserPath.toString(), command.getArgs()[0]).toFile();
        String clientDownloadFolder = command.getArgs()[1];

        if (!PathUtils.isPathsParentAndChild(rootUserPath, requestFile.toPath())) {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "access violation"));
            return;
        }

        if (requestFile.canRead()) {
            sendRequest(ctx, requestFile, clientDownloadFolder);
        } else {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "cant read file", requestFile.toString()));
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.FILES_REQUEST;
    }

}
