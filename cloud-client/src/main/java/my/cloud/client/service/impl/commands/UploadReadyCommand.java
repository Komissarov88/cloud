package my.cloud.client.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import command.service.CommandService;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import utils.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Called when server ready to receive ChunkedWriteHandler data
 */
public class UploadReadyCommand implements CommandService {

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
                || command.getArgs().length != 1) {
            Logger.warning("wrong arguments");
            return;
        }

        ChunkedFile cf;
        if ((cf = getChunkedFile(new File(command.getArgs()[0]))) == null) {
            Logger.warning("cant read file");
            ctx.close();
            return;
        }

        ctx.pipeline().replace("ObjectEncoder", "Writer", new ChunkedWriteHandler());
        ctx.pipeline().removeLast();
        ctx.writeAndFlush(cf).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Logger.info("upload complete");
                ctx.close();
            }
        });

    }

    @Override
    public CommandCode getCommand() {
        return CommandCode.UPLOAD_READY;
    }

}
