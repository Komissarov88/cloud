package my.cloud.client.service.impl.commands;

import command.domain.CommandCode;
import my.cloud.client.service.impl.commands.base.BaseClientCommand;

/**
 * Called on successful authentication.
 * Does nothing, except calling consumer in super class.
 */
public class AuthenticationSuccessCommand extends BaseClientCommand {

    public AuthenticationSuccessCommand() {
        expectedArgumentsCountCheck = i -> true;
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.SUCCESS;
    }

}
