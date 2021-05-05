package files.service.impl;

import files.domain.TransferId;
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
    private ConcurrentHashMap<String, TransferId> jobs;

    private FileTransferAuthServiceImpl() {
        jobs = new ConcurrentHashMap<>();
    }

    public static FileTransferAuthServiceImpl getInstance() {
        if (instance == null) {
            instance = new FileTransferAuthServiceImpl();
        }
        return instance;
    }

    @Override
    public String add(Path origin, Path destination, Channel channel) {
        String hash = HashOperator.apply(channel.toString() + destination);
        if (jobs.putIfAbsent(hash, new TransferId(origin, destination, channel)) != null) {
            Logger.warning("job already present");
        }
        return hash;
    }

    @Override
    public TransferId getTransferIfValid(String key) {
        return jobs.remove(key);
    }

    @Override
    public void remove(String key) {
        jobs.remove(key);
    }

    @Override
    public void clean() {
        for (Map.Entry<String, TransferId> stringFileJobEntry : jobs.entrySet()) {
            if (!stringFileJobEntry.getValue().channel.isOpen()) {
                jobs.remove(stringFileJobEntry.getKey());
            }
        }
    }
}
