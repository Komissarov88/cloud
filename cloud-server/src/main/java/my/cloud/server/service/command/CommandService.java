package my.cloud.server.service.command;

public interface CommandService {

    String processCommand(String command);
    String getCommand();
}
