package my.cloud.server.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import command.service.CommandService;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.server.factory.Factory;
import org.apache.commons.io.FileUtils;
import utils.PathUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Called on delete request
 */
public class RemoveFileCommand implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {

        if (command.getArgs() == null || command.getArgs().length != 1) {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "wrong arguments, expected file"));
            ctx.close();
            return;
        }

        Path rootUserPath = Factory.getServerService().getUserRootPath(ctx.channel());
        File requestFile = Paths.get(rootUserPath.toString(), command.getArgs()[0]).toFile();

        if (!PathUtils.isPathsParentAndChild(rootUserPath, requestFile.toPath())) {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "access violation"));
            return;
        }

        System.out.println(requestFile);
        if (FileUtils.deleteQuietly(requestFile)) {
            ctx.writeAndFlush(new Command(CommandCode.REFRESH_VIEW));
        } else {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "cant delete fail"));
        }

    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.REMOVE_FILE;
    }

}
