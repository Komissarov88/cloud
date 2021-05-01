package files.service.impl;

import files.service.FileTransferAuthService;
import io.netty.channel.Channel;
import utils.HashOperator;
import utils.Logger;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages FileJobs, used to authenticate upload/download channels
 */
public class FileTransferAuthServiceImpl implements FileTransferAuthService {

    private static FileTransferAuthServiceImpl instance;
    private ConcurrentHashMap<String, Transfer> jobs;

    private FileTransferAuthServiceImpl() {
        jobs = new ConcurrentHashMap<>();
    }

    public static FileTransferAuthServiceImpl getInstance() {
        if (instance == null) {
            instance = new FileTransferAuthServiceImpl();
        }
        return instance;
    }

    private static class Transfer {

        public final Channel channel;
        public final Path path;

        public Transfer(Channel channel, Path path) {
            this.channel = channel;
            this.path = path;
        }
    }

    @Override
    public String add(Path path, Channel channel) {
        String hash = HashOperator.apply(channel.toString() + path);
        if (jobs.putIfAbsent(hash, new Transfer(channel, path)) != null) {
            Logger.warning("job already present");
        }
        return hash;
    }

    @Override
    public Path getPathIfValid(String key) {
        Transfer fj = jobs.remove(key);
        return fj == null ? null : fj.path;
    }

    @Override
    public void remove(String key) {
        jobs.remove(key);
    }

    public void clean() {
        for (Map.Entry<String, Transfer> stringFileJobEntry : jobs.entrySet()) {
            if (!stringFileJobEntry.getValue().channel.isOpen()) {
                jobs.remove(stringFileJobEntry.getKey());
            }
        }
    }
}
