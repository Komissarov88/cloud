package files.service;

import java.nio.file.Path;

public interface FileTransferProgressService {

    void add(Path path, long size);
    void increment(Path path, int transferred);
    float progress(Path path);
    void remove(Path path);
}
