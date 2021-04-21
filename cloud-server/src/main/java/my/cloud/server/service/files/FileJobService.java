package my.cloud.server.service.files;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class FileJobService {

    private static FileJobService fileJobService;
    private Set<FileJob> jobs;

    private FileJobService() {
        jobs = new HashSet<>();
    }

    public static FileJobService getInstance() {
        if (fileJobService == null) {
            fileJobService = new FileJobService();
        }
        return fileJobService;
    }

    public String add(Path path, String login) {
        FileJob job = new FileJob(path, login);
        if (!jobs.add(job)) {
            throw new IllegalArgumentException("job already exists");
        }
        return job.getAuthenticateKey();
    }

    public void remove(Path path, String login) {
        for (FileJob job : jobs) {
            if (job.equals(path, login)) {
                jobs.remove(job);
                return;
            }
        }
    }
}
