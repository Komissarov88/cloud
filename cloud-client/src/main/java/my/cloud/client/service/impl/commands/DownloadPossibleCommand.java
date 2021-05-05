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
import java.util.function.Consumer;

/**
 * Called when server sends key to authenticate download channel
 */
public class DownloadPossibleCommand implements CommandService {

    private Consumer<String[]> consumer;

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {

        if (command.getArgs() == null
                || command.getArgs().length < 6) {
            Logger.warning("wrong arguments");
            return;
        }

        long totalSize = Long.parseLong(command.getArgs()[0]);
        int filesNumber = Integer.parseInt(command.getArgs()[1]);
        Path targetPath = Paths.get(command.getArgs()[2]);
        Path origin = Paths.get(command.getArgs()[3]);

        // free space check
        if (totalSize > targetPath.toFile().getFreeSpace()) {
            Logger.warning("not enough free space");
            String[] args = new String[filesNumber];
            int j = 4;
            for (int i = 0; i < filesNumber; i++) {
                String authKey = command.getArgs()[j];
                args[i] = authKey;
                j += 2;
            }
            Factory.getNetworkService().sendCommand(new Command(CommandCode.OFFER_REFUSED, args));
            return;
        }

        // add info to track transfer progress
        Factory.getDownloadProgressService().add(origin, totalSize);

        // submit transfer channels
        int j = 4;
        for (int i = 0; i < filesNumber; i++) {
            String authKey = command.getArgs()[j++];
            Path serverPath = Paths.get(command.getArgs()[j++]);
            Path fileName = targetPath.resolve(serverPath);

            String jobKey = Factory.getFileTransferAuthService().add(origin, fileName, ctx.channel());
            Command initialCommand = new Command(CommandCode.DOWNLOAD, authKey, jobKey);
            Factory.getNetworkService().submitConnection(new CloudConnection(initialCommand, null));
        }

        // callback to notify when download task added
        if (consumer != null) {
            consumer.accept(command.getArgs());
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.DOWNLOAD_POSSIBLE;
    }

    @Override
    public void setListener(Consumer<String[]> consumer) {
        this.consumer = consumer;
    }
}
