package my.cloud.client.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import command.service.CommandService;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.client.factory.Factory;
import my.cloud.client.service.impl.CloudConnection;
import utils.Logger;

import java.nio.file.Path;

/**
 * Called when server sends key to authenticate download channel
 */
public class DownloadPossibleCommand implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {
        Logger.info(command.toString());

        if (command.getArgs() == null
                || command.getArgs().length < 3) {
            Logger.warning("wrong arguments");
            return;
        }

        Path currentPath = Factory.getNetworkService().getCurrentPath();
        long totalSize = Long.parseLong(command.getArgs()[0]);
        if (totalSize > currentPath.toFile().getFreeSpace()) {
            Logger.warning("not enough free space");
            return;
        }

        //TODO callback on progressbar

        for (int i = 1; i <= command.getArgs().length - 2; i+=2) {
            String authKey = command.getArgs()[i];
            Path fileName = currentPath.resolve(command.getArgs()[i+1]);
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
