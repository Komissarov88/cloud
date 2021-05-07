package my.cloud.client.service.impl.commands;

import command.domain.CommandCode;
import my.cloud.client.service.impl.commands.base.BaseClientCommand;

/**
 * Called on transfer completes or file delete.
 * Does nothing, except calling consumer in super class.
 */
public class RefreshViewCommand extends BaseClientCommand {

    public RefreshViewCommand() {
        expectedArgumentsCountCheck = integer -> true;
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.REFRESH_VIEW;
    }

}
