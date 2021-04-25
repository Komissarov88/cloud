package files.service.impl;

import files.service.FileJobService;
import io.netty.channel.Channel;
import utils.HashOperator;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages FileJobs, used to authenticate upload/download channels
 */
public class FileJobServiceImpl implements FileJobService {

    private static FileJobServiceImpl fileJobService;
    private ConcurrentHashMap<String, FileJob> jobs;

    private FileJobServiceImpl() {
        jobs = new ConcurrentHashMap<>();
    }

    public static FileJobServiceImpl getInstance() {
        if (fileJobService == null) {
            fileJobService = new FileJobServiceImpl();
        }
        return fileJobService;
    }

    private static class FileJob {

        public final Channel channel;
        public final File file;

        public FileJob(Channel channel, File file) {
            this.channel = channel;
            this.file = file;
        }
    }

    @Override
    public String add(File file, Channel channel) {
        String hash = HashOperator.apply(channel.toString() + file);
        jobs.putIfAbsent(hash, new FileJob(channel, file));
        return hash;
    }

    @Override
    public File remove(String key) {
        FileJob fj = jobs.remove(key);
        return fj == null ? null : fj.file;
    }

    @Override
    public void clean() {
        for (Map.Entry<String, FileJob> stringFileJobEntry : jobs.entrySet()) {
            if (!stringFileJobEntry.getValue().channel.isOpen()) {
                jobs.remove(stringFileJobEntry.getKey());
            }
        }
    }
}
