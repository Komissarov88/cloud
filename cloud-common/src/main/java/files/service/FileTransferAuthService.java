package files.service;

import files.domain.TransferId;
import io.netty.channel.Channel;

import java.nio.file.Path;

public interface FileTransferAuthService {

    /**
     * Add new TransferId to authenticate transfer channels
     * @param origin file or folder user clicked on
     * @param destination folder on other side
     * @param channel that transferring data
     * @return hash key
     */
    String add(Path origin, Path destination, Channel channel);

    /**
     * Authenticate transfer
     * @param key hash given to main user channel
     * @return and remove TransferId from
     */
    TransferId getTransferIfValid(String key);

    /**
     * Removes key
     * @param key hash given to main user channel
     */
    void remove(String key);

    /**
     * Remove all keys associated with dead channels
     */
    void clean();
}
