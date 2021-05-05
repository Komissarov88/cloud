package my.cloud.client.service;

import command.domain.Command;
import command.domain.CommandCode;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public interface NetworkService {

    /**
     * Upload list of files to server
     * @param serverUploadDirectory absolute path from server side user root to target dir
     * @param files list of absolute paths of local file system
     */
    void uploadFile(Path serverUploadDirectory, List<Path> files);

    /**
     * Remove remote files
     * @param path paths to target files
     */
    void removeFile(List<Path> path);

    /**
     * Download files from server
     * @param clientDownloadDirectory absolute path to client target dir
     * @param files absolute path from server side user root to target file
     */
    void downloadFile(Path clientDownloadDirectory, List<Path> files);

    /**
     * Close main connection
     */
    void closeConnection();

    /**
     * @return true if main connection alive
     */
    boolean isConnected();

    /**
     * Submit new connection with AUTH command
     * @param login login
     * @param password password
     */
    void login(String login, String password);

    /**
     * Add new main or transfer connection to executor service.
     * @param connection CloudConnection
     */
    void submitConnection(Runnable connection);

    /**
     * Set consumer for CommandCode args array
     * @param code code to subscribe
     * @param listener consumer
     */
    void setCommandCodeListener(CommandCode code, Consumer<String[]> listener);

    /**
     * Disconnect callback set
     * @param onChannelInactive callback
     */
    void setOnChannelInactive(Runnable onChannelInactive);

    /**
     * Send LS command to server
     * @param path target path
     */
    void requestFileList(String path);

    /**
     * Try to register. Login on success
     * @param login login
     * @param password password
     */
    void requestRegistration(String login, String password);

    /**
     * @param command send any
     */
    void sendCommand(Command command);
}