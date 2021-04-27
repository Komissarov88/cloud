package my.cloud.server.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.server.factory.Factory;
import command.service.CommandService;
import utils.Logger;
import utils.PathUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Called when client want to upload file
 */
public class FilesOfferCommand implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {
        Logger.info(command.toString());

        if (command.getArgs() == null
                || command.getArgs().length < 3
                || !Factory.getServerService().isUserLoggedIn(ctx.channel())) {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "wrong arguments"));
            return;
        }

        Path rootUserPath = Factory.getServerService().getUserRootPath(ctx.channel());

        long size = Long.parseLong(command.getArgs()[0]);
        if (size > Factory.getServerService().getUserFreeSpace(ctx.channel())) {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "no free space"));
            return;
        }

        for (int i = 1; i <= command.getArgs().length - 2; i+=2) {
            File file = Paths.get(rootUserPath.toString(), command.getArgs()[i+1]).toFile();

            if (!PathUtils.isPathsParentAndChild(rootUserPath, file.toPath())) {
                ctx.writeAndFlush(new Command(CommandCode.FAIL, "access violation"));
                continue;
            }

            String clientKey = command.getArgs()[i];
            String uploadChannelAuthKey = Factory.getFileTransferAuthService().add(file.toPath(), ctx.channel());

            ctx.writeAndFlush(
                    new Command(CommandCode.UPLOAD_POSSIBLE,
                    uploadChannelAuthKey,
                    clientKey));
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.FILES_OFFER;
    }

}
