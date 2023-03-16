package my.cloud.server.service.impl.commands;

import command.domain.CommandCode;
import command.service.CommandService;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.io.FileUtils;

import java.io.File;

import static my.cloud.server.service.impl.commands.util.ServerCommandUtil.*;

/**
 * Called on delete request
 */
public class RemoveFileCommand implements CommandService {

    private boolean notCorrectCommand(ChannelHandlerContext ctx, String[] args) {
        return disconnectIfUnknown(ctx) || wrongArgumentsLength(ctx, args, i -> i != 1);
    }

    @Override
    public void processCommand(ChannelHandlerContext ctx, String[] args) {
        if (notCorrectCommand(ctx, args)) {
            return;
        }

        String request = args[0];
        File requestFile = getFileFromClientRequest(ctx, request);

        if (requestFile == null) {
            sendFailMessage(ctx, "access violation");
            return;
        }
        if (FileUtils.deleteQuietly(requestFile)) {
            sendResponse(ctx, CommandCode.REFRESH_VIEW);
        } else {
            sendFailMessage(ctx, "cant delete fail");
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.REMOVE_FILE;
    }

}
