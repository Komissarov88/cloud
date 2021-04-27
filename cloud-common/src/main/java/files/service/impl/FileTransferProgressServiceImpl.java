package files.service.impl;

import files.service.FileTransferProgressService;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class FileTransferProgressServiceImpl implements FileTransferProgressService {

    private final ConcurrentHashMap<Path, Progress> progressMap;

    private static class Progress {
        long size;
        long transferred;

        public Progress(long size) {
            this.size = size;
            this.transferred = 0;
        }

        public long progress() {
            return size - transferred;
        }
    }

    public FileTransferProgressServiceImpl() {
        progressMap = new ConcurrentHashMap<>();
    }

    @Override
    public void add(Path path, long size) {
        progressMap.putIfAbsent(path, new Progress(size));
    }

    @Override
    public void increment(Path path, long transferred) {
        Progress p = progressMap.get(path);
        if (p != null) {
            p.transferred += transferred;
        }
    }

    @Override
    public float progress(Path path) {
        if (progressMap.isEmpty()) {
            return -1;
        }

        Progress progress = progressMap.get(path);
        if (progress != null) {
            return ((float) progress.transferred) / ((float) progress.size) * 100f;
        }

        AtomicLong overallSize = new AtomicLong(0);
        AtomicLong transferred = new AtomicLong(0);
        List<Path> done = new LinkedList<>();
        progressMap.forEach((key, val) -> {
            if (key.startsWith(path)) {
                if (val.size <= val.transferred) {
                    done.add(key);
                } else {
                    overallSize.addAndGet(val.size);
                    transferred.addAndGet(val.transferred);
                }
            }
        });
        return transferred.floatValue() / overallSize.floatValue() * 100f;
    }

    @Override
    public void remove(Path path) {
        progressMap.remove(path);
    }
}
