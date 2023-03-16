package my.cloud.server.service.impl.commands;

import command.domain.CommandCode;
import command.service.CommandService;
import io.netty.channel.ChannelHandlerContext;
import utils.PathUtils;

import java.io.File;

import static my.cloud.server.service.impl.commands.util.ServerCommandUtil.*;

/**
 * Return files list in server directory
 */
public class GetFormattedFileListCommand implements CommandService {

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
        String[] response = PathUtils.lsDirectory(requestFile.toPath(), getUserRootPath(ctx));
        if (response.length > 0) {
            sendResponse(ctx, CommandCode.LS, response);
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.LS;
    }
}
