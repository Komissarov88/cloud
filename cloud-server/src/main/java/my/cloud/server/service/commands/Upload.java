package my.cloud.server.service.commands;

import command.Command;
import command.CommandCode;
import handler.FileReadHandler;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.server.factory.Factory;
import command.CommandService;
import my.cloud.server.service.files.FileJob;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Upload implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {

        if (command.getArgs() == null
                || command.getArgs().length != 1) {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "wrong arguments"));
            return;
        }

        FileJob job = Factory.getFileJobService().remove(command.getArgs()[0]);
        if (job != null) {

            try {
                Path path = Paths.get(job.file.getPath());
                ctx.pipeline().replace(
                        "ObjectDecoder", "Reader", new FileReadHandler(path));
                ctx.writeAndFlush(new Command(CommandCode.OK, "transfer ready")).sync();
                ctx.pipeline().removeLast();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }
        ctx.writeAndFlush(new Command(CommandCode.FAIL, "authentication fails"));
    }

    @Override
    public CommandCode getCommand() {
        return CommandCode.UPLOAD;
    }

}
