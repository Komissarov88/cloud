package my.cloud.server.service.commands;

import command.Command;
import command.CommandCode;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.server.factory.Factory;
import command.CommandService;
import utils.Logger;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Called when client want to upload file
 */
public class UploadRequest implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {
        Logger.info(command.toString());

        if (command.getArgs() == null
                || command.getArgs().length != 2
                || !Factory.getServerService().isUserLoggedIn(ctx.channel())) {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "wrong arguments"));
            return;
        }
        Path path = Paths.get(command.getArgs()[0]);
        long size = Long.parseLong(command.getArgs()[1]);
        File file = new File("./data/" + path.getFileName());
        if (true) { // TODO free space check
            String[] response = {
                    Factory.getFileJobService().add(file, ctx.channel()),
                    command.getArgs()[0]
            };
            ctx.writeAndFlush(new Command(CommandCode.UPLOAD_REQUEST, response));
        } else {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "no free space"));
        }
    }

    @Override
    public CommandCode getCommand() {
        return CommandCode.UPLOAD_REQUEST;
    }

}
