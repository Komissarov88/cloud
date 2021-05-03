package files.service;

import files.domain.Transfer;
import io.netty.channel.Channel;

import java.nio.file.Path;

public interface FileTransferAuthService {

    String add(Path origin, Path destination, Channel channel);
    Transfer getTransferIfValid(String key);
    void remove(String key);
}
