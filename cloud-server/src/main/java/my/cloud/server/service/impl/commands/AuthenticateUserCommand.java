package my.cloud.server.service.impl.commands;

import command.domain.CommandCode;
import command.service.CommandService;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.server.factory.Factory;
import utils.PropertiesReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static my.cloud.server.service.impl.commands.util.ServerCommandUtil.*;

/**
 * Called when client want to login
 */
public class AuthenticateUserCommand implements CommandService {

    private boolean notCorrectCommand(ChannelHandlerContext ctx, String[] args) {
        return disconnectIfArgsLengthNotEqual(ctx, 2, args);
    }

    private void createUserFolders(String login) {
        try {
            Files.createDirectories(Paths.get(PropertiesReader.getProperty("server.data.root.path"), login));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processCommand(ChannelHandlerContext ctx, String[] args) {

        if (notCorrectCommand(ctx, args)) {
            return;
        }

        String login = args[0];
        String password = args[1];

        if (Factory.getDbService().login(login, password)) {
            Factory.getServerService().subscribeUser(login, ctx.channel());
            sendResponse(ctx, CommandCode.SUCCESS, "authenticated");
            createUserFolders(login);
        } else {
            sendFailMessage(ctx, "authentication fails");
            ctx.close();
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.AUTH;
    }

}
