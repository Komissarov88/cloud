package my.cloud.server.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.server.factory.Factory;
import command.service.CommandService;
import utils.PathUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * return files in server directory
 */
public class ViewFilesInDirCommand implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {

        final int requirementCountCommandParts = 1;

        if (command.getArgs().length != requirementCountCommandParts
                || !Factory.getServerService().isUserLoggedIn(ctx.channel())) {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "wrong arguments"));
            return;
        }

        Path rootUserPath = Factory.getServerService().getUserRootPath(ctx.channel());
        Path requestPath = Paths.get(rootUserPath.toString(), command.getArgs()[0]);

        if (!PathUtils.isPathsParentAndChild(rootUserPath, requestPath)) {
            return;
        }

        String[] args = PathUtils.lsDirectory(requestPath, rootUserPath);
        if (args.length > 0) {
            ctx.writeAndFlush(new Command(CommandCode.LS, args));
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.LS;
    }

}
