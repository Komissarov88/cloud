package my.cloud.server.service.impl.commands;

import command.domain.CommandCode;
import my.cloud.server.factory.Factory;
import my.cloud.server.service.impl.commands.base.BaseServerCommand;
import utils.PropertiesReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Called when client want to login
 */
public class AuthenticateUserCommand extends BaseServerCommand {

    public AuthenticateUserCommand() {
        expectedArgumentsCountCheck = i -> i == 2;
        disconnectOnFail = true;
    }

    private void createUserFolders(String login) {
        try {
            Files.createDirectories(Paths.get(PropertiesReader.getProperty("server.data.root.path"), login));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void processArguments(String[] args) {
        String login = args[0];
        String password = args[1];

        if (Factory.getDbService().login(login, password)) {
            Factory.getServerService().subscribeUser(login, ctx.channel());
            sendResponse(CommandCode.SUCCESS, "authenticated");
            createUserFolders(login);
        } else {
            sendFailMessage("authentication fails");
            ctx.close();
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.AUTH;
    }

}
