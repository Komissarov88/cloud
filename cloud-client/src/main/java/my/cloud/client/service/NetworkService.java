package my.cloud.client.service;

import command.Command;

public interface NetworkService {

    void sendCommand(Command command);
    Command readCommandResult();
    void closeConnection();
    void connect();
}
