package my.cloud.server.service.commands;

import command.Command;
import command.CommandCode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import my.cloud.server.factory.Factory;
import command.CommandService;
import my.cloud.server.service.files.FileJob;

import java.io.File;
import java.io.IOException;

public class Download implements CommandService {

    private ChunkedFile getChunkedFile(File file) {
        try {
            return new ChunkedFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {

        if (command.getArgs() == null
                || command.getArgs().length != 1) {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "wrong arguments"));
            return;
        }

        FileJob job = Factory.getFileJobService().remove(command.getArgs()[0]);
        if (job != null) {
            ChunkedFile cf;
            if ((cf = getChunkedFile(job.file)) == null) {
                ctx.writeAndFlush(new Command(CommandCode.FAIL, "cant read file"));
                ctx.close();
                return;
            }

            try {
                ctx.writeAndFlush(new Command(CommandCode.OK, "transfer ready")).sync();
                ctx.pipeline().replace("ObjectEncoder", "Writer", new ChunkedWriteHandler());
                ctx.pipeline().removeLast();
                ctx.writeAndFlush(cf).sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                ctx.close();
            }
            return;
        }
        ctx.writeAndFlush(new Command(CommandCode.FAIL, "authentication fails"));
    }

    @Override
    public CommandCode getCommand() {
        return CommandCode.DOWNLOAD;
    }

}
