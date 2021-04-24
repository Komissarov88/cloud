package files.service;

import io.netty.channel.Channel;

import java.io.File;

public interface FileJobService {

    String add(File file, Channel channel);
    File remove(String key);
    void clean();
}
