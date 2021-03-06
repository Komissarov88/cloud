package my.cloud.client.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import command.service.CommandService;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.client.factory.Factory;
import my.cloud.client.service.impl.CloudConnection;
import utils.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static my.cloud.client.service.impl.commands.util.ClientCommandUtil.*;

/**
 * Called when server sends key to authenticate download channel
 */
public class DownloadPossibleCommand implements CommandService {

    private Consumer<String[]> consumer;

    @Override
    public void processCommand(ChannelHandlerContext ctx, String[] args) {
        if (wrongArgumentsLength(args, i -> i < 6 || i % 2 != 0)) {
            return;
        }

        final int COMMON_ARGS_COUNT = 4;

        long totalSize = Long.parseLong(args[0]);
        int filesNumber = Integer.parseInt(args[1]);
        Path targetPath = Paths.get(args[2]);
        Path origin = Paths.get(args[3]);

        List<String> keys = parseEachSecond(args, COMMON_ARGS_COUNT);

        if (totalSize > targetPath.toFile().getFreeSpace()) {
            sendRefuseResponse(keys);
            return;
        }

        List<Path> paths = parseEachSecond(args, COMMON_ARGS_COUNT + 1)
                .stream()
                .map(Paths::get).collect(Collectors.toList());

        if (filesNumber != paths.size()) {
            sendFailMessage("files count mismatch");
            sendRefuseResponse(keys);
            return;
        }

        Factory.getDownloadProgressService().add(origin, totalSize);
        initiateDownload(ctx, paths, keys, targetPath, origin);

        if (consumer != null) {
            consumer.accept(args);
        }
    }

    private void initiateDownload(ChannelHandlerContext ctx,
                                  List<Path> serverPath, List<String> keys,
                                  Path targetPath, Path origin) {

        for (int i = 0; i < serverPath.size(); i++) {
            Path fileName = targetPath.resolve(serverPath.get(i));

            String jobKey = Factory.getFileTransferAuthService().add(origin, fileName, ctx.channel());
            Command initialCommand = new Command(CommandCode.DOWNLOAD, keys.get(i), jobKey);
            Factory.getNetworkService().submitConnection(new CloudConnection(initialCommand, null));
        }
    }

    private void sendRefuseResponse(List<String> keys) {
        Logger.warning("not enough free space");
        Factory.getNetworkService().sendCommand(new Command(CommandCode.OFFER_REFUSED, keys.toArray(new String[0])));
    }

    private List<String> parseEachSecond(String[] args, int from) {
        List<String> strings = new ArrayList<>();
        for (int i = from; i < args.length; i += 2) {
            strings.add(args[i]);
        }
        return strings;
    }

    @Override
    public void setListener(Consumer<String[]> consumer) {
        this.consumer = consumer;
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.DOWNLOAD_POSSIBLE;
    }
}
