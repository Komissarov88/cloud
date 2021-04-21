package my.cloud.server.service.commands;

import command.Command;
import command.CommandCode;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.server.factory.Factory;
import command.CommandService;

import java.io.File;

public class UploadRequest implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {

        if (command.getArgs() == null
                || command.getArgs().length != 1
                || !Factory.getServerService().isUserLoggedIn(ctx.channel())) {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "wrong arguments"));
            return;
        }
        File file = new File(command.getArgs()[0]);
        if (true) { // TODO free space check
            String response = Factory.getFileJobService().add(file, ctx.channel());
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
