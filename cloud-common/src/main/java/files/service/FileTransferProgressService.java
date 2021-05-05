package files.service;

import java.nio.file.Path;
import java.util.List;

public interface FileTransferProgressService {

    /**
     * Add new transfer data to track progress
     * @param path origin path of transfer
     * @param size total size of folder or file
     */
    void add(Path path, long size);

    /**
     * Update transferred bytes of job
     * @param path id
     * @param transferred bytes to increment
     */
    void increment(Path path, int transferred);

    /**
     * Progress of all added jobs
     * @return value between 0 and 1
     */
    float totalProgress();

    /**
     * Progress of single job
     * @param path id
     * @return value between 0 and 1
     */
    float progress(Path path);

    /**
     * @return list of all jobs
     */
    List<Path> getTransferList();

    /**
     * Remove
     * @param path id
     */
    void remove(Path path);

    /**
     * Clear all jobs regardless of their completeness
     */
    void clear();
}
