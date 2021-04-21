package my.cloud.server.service.commands;

import command.Command;
import command.CommandCode;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.server.factory.Factory;
import command.CommandService;
import utils.Logger;

import java.io.File;

/**
 * Called when client want to download file
 */
public class DownloadRequest implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {
        Logger.info(command.toString());

        if (command.getArgs() == null
                || command.getArgs().length != 1
                || !Factory.getServerService().isUserLoggedIn(ctx.channel())) {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "wrong arguments"));
            return;
        }

        File file = new File(command.getArgs()[0]);
        if (file.canRead()) {
            String[] response = {
                    Factory.getFileJobService().add(file, ctx.channel()),
                    file.getName(),
                    String.valueOf(file.length())
            };
            ctx.writeAndFlush(new Command(CommandCode.DOWNLOAD_REQUEST, response));
        } else {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "cant read file"));
        }
    }

    @Override
    public CommandCode getCommand() {
        return CommandCode.DOWNLOAD_REQUEST;
    }

}
