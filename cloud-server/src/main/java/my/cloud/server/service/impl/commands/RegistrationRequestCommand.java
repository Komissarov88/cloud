package my.cloud.server.service.impl.commands;

import command.domain.CommandCode;
import my.cloud.server.factory.Factory;
import my.cloud.server.service.impl.commands.base.BaseServerCommand;
import utils.PropertiesReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Called when client want to register
 */
public class RegistrationRequestCommand extends BaseServerCommand {

    public RegistrationRequestCommand() {
        disconnectOnFail = true;
        expectedArgumentsCountCheck = i -> i == 2;
    }

    @Override
    protected void processArguments(String[] args) {

        String login = args[0];
        String password = args[1];

        if (login.length() == 0 || password.length() == 0) {
            sendFailMessage("login/password expected not empty");
            ctx.close();
            return;
        }

        if (Factory.getDbService().addUser(login, password)) {
            Factory.getServerService().subscribeUser(login, ctx.channel());
            createUserFolder(login);
            sendResponse(CommandCode.SUCCESS, "registered");
        } else {
            sendFailMessage("user already exists");
            ctx.close();
        }
    }

    private void createUserFolder(String login) {
        try {
            Files.createDirectories(Paths.get(PropertiesReader.getProperty("server.data.root.path"), login));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.REGISTER_REQUEST;
    }

}
