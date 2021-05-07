package my.cloud.server.service.impl.commands;

import command.domain.CommandCode;
import my.cloud.server.factory.Factory;
import my.cloud.server.service.impl.commands.base.BaseServerCommand;
import utils.PathUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Called when client want to upload file
 */
public class FilesOfferCommand extends BaseServerCommand {

    public FilesOfferCommand() {
        isAuthNeeded = true;
        expectedArgumentsCountCheck = i -> i >= 3;
    }

    @Override
    protected void processArguments(String[] args) {

        long totalSize = Long.parseLong(args[0]);

        List<String> keys = parseEachSecond(args, 1);

        if (!haveFreeSpace(totalSize)) {
            sendFailMessage("not enough free space");
            sendResponse(CommandCode.OFFER_REFUSED, keys.toArray(new String[0]));
            return;
        }

        String userRoot = getUserRootPath().toString();
        List<Path> files = parseEachSecond(args, 2)
                .stream()
                .map(s -> Paths.get(userRoot, s))
                .collect(Collectors.toList());

        for (int i = 0; i < files.size(); i++) {

            if (!PathUtils.isPathsParentAndChild(getUserRootPath(), files.get(i))) {
                sendFailMessage("access violation");
                sendResponse(CommandCode.OFFER_REFUSED, keys.get(i));
                continue;
            }

            String uploadChannelAuthKey = Factory.getFileTransferAuthService().add(null, files.get(i), ctx.channel());
            sendResponse(CommandCode.UPLOAD_POSSIBLE, uploadChannelAuthKey, keys.get(i));
        }
    }

    private List<String> parseEachSecond(String[] args, int from) {

        List<String> strings = new ArrayList<>();
        for (int i = from; i < args.length; i += 2) {
            strings.add(args[i]);
        }
        return strings;
    }

    private boolean haveFreeSpace(long size) {
        return size <= Factory.getServerService().getUserFreeSpace(ctx.channel());
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.FILES_OFFER;
    }

}
