package my.cloud.server.service.impl.commands;

import command.domain.CommandCode;
import command.service.CommandService;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.server.factory.Factory;
import utils.PathUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static my.cloud.server.service.impl.commands.util.ServerCommandUtil.*;

/**
 * Called when client want to upload file
 */
public class FilesOfferCommand implements CommandService {

    private boolean notCorrectCommand(ChannelHandlerContext ctx, String[] args) {
        return disconnectIfUnknown(ctx) || wrongArgumentsLength(ctx, args, i -> i < 3);
    }

    @Override
    public void processCommand(ChannelHandlerContext ctx, String[] args) {
        if (notCorrectCommand(ctx, args)) {
            return;
        }

        long totalSize = Long.parseLong(args[0]);
        List<String> keys = parseEachSecond(args, 1);

        if (!haveFreeSpace(ctx, totalSize)) {
            sendFailMessage(ctx, "not enough free space");
            sendResponse(ctx, CommandCode.OFFER_REFUSED, keys.toArray(new String[0]));
            return;
        }

        String userRoot = getUserRootPath(ctx).toString();
        List<Path> files = parseEachSecond(args, 2)
                .stream()
                .map(s -> Paths.get(userRoot, s))
                .collect(Collectors.toList());

        for (int i = 0; i < files.size(); i++) {
            if (!PathUtils.isPathsParentAndChild(getUserRootPath(ctx), files.get(i))) {
                sendFailMessage(ctx, "access violation");
                sendResponse(ctx, CommandCode.OFFER_REFUSED, keys.get(i));
                continue;
            }
            String uploadChannelAuthKey = Factory.getFileTransferAuthService().add(null, files.get(i), ctx.channel());
            sendResponse(ctx, CommandCode.UPLOAD_POSSIBLE, uploadChannelAuthKey, keys.get(i));
        }
    }

    private List<String> parseEachSecond(String[] args, int from) {

        List<String> strings = new ArrayList<>();
        for (int i = from; i < args.length; i += 2) {
            strings.add(args[i]);
        }
        return strings;
    }

    private boolean haveFreeSpace(ChannelHandlerContext ctx, long size) {
        return size <= Factory.getServerService().getUserFreeSpace(ctx.channel());
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.FILES_OFFER;
    }
}
