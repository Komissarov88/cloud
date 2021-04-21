package my.cloud.server.service.files;

import io.netty.channel.Channel;
import utils.Hash;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileJobService {

    private static FileJobService fileJobService;
    private ConcurrentHashMap<String, FileJob> jobs;

    private FileJobService() {
        jobs = new ConcurrentHashMap<>();
    }

    public static FileJobService getInstance() {
        if (fileJobService == null) {
            fileJobService = new FileJobService();
        }
        return fileJobService;
    }

    public String add(File file, Channel channel) {
        String hash = Hash.get(channel.toString() + file);
        jobs.putIfAbsent(hash, new FileJob(channel, file));
        return hash;
    }

    public FileJob remove(String key) {
        return jobs.remove(key);
    }

    public void clean() {
        for (Map.Entry<String, FileJob> stringFileJobEntry : jobs.entrySet()) {
            if (!stringFileJobEntry.getValue().userChannel.isOpen()) {
                jobs.remove(stringFileJobEntry.getKey());
            }
        }
    }
}
