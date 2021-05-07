package my.cloud.client.service.impl.commands;

import command.domain.CommandCode;
import my.cloud.client.service.impl.commands.base.BaseClientCommand;

/**
 * Called when something goes wrong
 * Does nothing, except calling consumer in super class.
 */
public class FailResponseCommand extends BaseClientCommand {

    public FailResponseCommand() {
        expectedArgumentsCountCheck = integer -> true;
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.FAIL;
    }
}
