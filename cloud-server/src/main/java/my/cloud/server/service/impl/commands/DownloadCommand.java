package my.cloud.server.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import my.cloud.server.factory.Factory;
import command.service.CommandService;
import utils.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Called from download channel with authenticate key
 */
public class DownloadCommand implements CommandService {

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
        Logger.info(command.toString());

        if (command.getArgs() == null
                || command.getArgs().length != 2) {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "wrong arguments"));
            ctx.close();
            return;
        }

        String key = command.getArgs()[0];
        String clientPath = command.getArgs()[1];

        File job = Factory.getFileJobService().remove(key);
        if (job != null) {
            ChunkedFile cf;
            if ((cf = getChunkedFile(job)) == null) {
                ctx.writeAndFlush(new Command(CommandCode.FAIL, "cant read file"));
                ctx.close();
                return;
            }

            try {
                ctx.writeAndFlush(new Command(CommandCode.DOWNLOAD_READY, clientPath)).sync();
                ctx.pipeline().replace("ObjectEncoder", "Writer", new ChunkedWriteHandler());
                ctx.pipeline().removeLast();
                ctx.writeAndFlush(cf).addListener((ChannelFutureListener) future -> {
                    Logger.info("download complete");
                    ctx.close();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "authentication fails"));
        }
    }

    @Override
    public CommandCode getCommand() {
        return CommandCode.DOWNLOAD;
    }

}
