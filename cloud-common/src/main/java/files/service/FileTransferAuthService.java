package files.service;

import io.netty.channel.Channel;

import java.nio.file.Path;

public interface FileTransferAuthService {

    String add(Path path, Channel channel);
    Path getPathIfValid(String key);
    void clean();
}
