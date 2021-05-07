package my.cloud.client.service.impl.commands;

import command.domain.CommandCode;
import my.cloud.client.service.impl.commands.base.BaseClientCommand;

/**
 * Hold list of files in directory, sent with LS command.
 * Does nothing, except calling consumer in super class.
 */
public class UpdateServerFilesListCommand extends BaseClientCommand {

    public UpdateServerFilesListCommand() {
        expectedArgumentsCountCheck = integer -> true;
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.LS;
    }

}
