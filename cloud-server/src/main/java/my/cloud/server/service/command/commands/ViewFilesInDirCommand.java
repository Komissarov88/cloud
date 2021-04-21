package my.cloud.server.service.command.commands;

import command.Command;
import command.CommandCode;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.server.factory.Factory;
import my.cloud.server.service.command.CommandService;

import java.io.File;
import java.util.Arrays;

public class ViewFilesInDirCommand implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {
        final int requirementCountCommandParts = 1;

        if (command.getArgs().length != requirementCountCommandParts
                || Factory.getServerService().isUserLoggedIn(ctx.channel())) {
            ctx.writeAndFlush(new Command(CommandCode.FAIL));
            return;
        }

        String[] args = {process(command.getArgs()[0])};
        ctx.writeAndFlush(new Command(CommandCode.LS, args));
    }

    private String process(String dirPath) {
        File directory = new File(dirPath);

        if (!directory.exists()) {
            return "Directory is not exists";
        }

        if (directory.isFile()) {
            return dirPath;
        }

        StringBuilder builder = new StringBuilder();
        for (File childFile : directory.listFiles()) {
            String typeFile = getTypeFile(childFile);
            builder.append(childFile.getName()).append(" | ").append(typeFile).append(System.lineSeparator());
        }

        return builder.toString();
    }

    private String getTypeFile(File childFile) {
        return childFile.isDirectory() ? "DIR" : "FILE";
    }

    @Override
    public CommandCode getCommand() {
        return CommandCode.LS;
    }

}
