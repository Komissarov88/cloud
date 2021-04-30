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

/**
 * Called when server sends key to authenticate download channel
 */
public class DownloadPossibleCommand implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {

        if (command.getArgs() == null
                || command.getArgs().length < 3) {
            Logger.warning("wrong arguments");
            return;
        }

        Path currentPath = Paths.get(".");
        long totalSize = Long.parseLong(command.getArgs()[0]);
        long filesNumber = Integer.parseInt(command.getArgs()[1]);

        if (totalSize > currentPath.toFile().getFreeSpace()) {
            Logger.warning("not enough free space");
            return;
        }

        int j = 2;
        for (int i = 0; i < filesNumber; i++) {
            String authKey = command.getArgs()[j++];
            Path fileName = currentPath.resolve(command.getArgs()[j++]);
            long fileSize = Long.parseLong(command.getArgs()[j++]);

            Factory.getDownloadProgressService().add(fileName, fileSize);
            String jobKey = Factory.getFileTransferAuthService().add(fileName, ctx.channel());
            Command initialCommand = new Command(CommandCode.DOWNLOAD, authKey, jobKey);
            Factory.getNetworkService().submitConnection(new CloudConnection(initialCommand));
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.DOWNLOAD_POSSIBLE;
    }

}
