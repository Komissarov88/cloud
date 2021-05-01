package files.service.impl;

import files.service.FileTransferProgressService;
import utils.Logger;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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
    }

    public FileTransferProgressServiceImpl() {
        progressMap = new ConcurrentHashMap<>();
    }

    @Override
    public void add(Path path, long size) {
        progressMap.putIfAbsent(path, new Progress(size));
    }

    @Override
    public void increment(Path path, int transferred) {
        Progress p = progressMap.get(path);
        if (p != null) {
            p.transferred += transferred;
        } else {
            Logger.warning("no such transfer: " + new String(path.toString().getBytes(StandardCharsets.UTF_8)));
        }
    }

    @Override
    public float progress(Path path) {
        Progress progress = progressMap.get(path);
        if (progress == null) {
            return -1;
        }
        return ((float) progress.transferred) / ((float) progress.size);
    }

    @Override
    public float totalProgress() {
        if (progressMap.isEmpty()) {
            return -1;
        }

        AtomicLong transferred = new AtomicLong(0);
        AtomicLong size = new AtomicLong(0);
        progressMap.forEach((key, val) -> {
            transferred.addAndGet(val.transferred);
            size.addAndGet(val.size);
        });

        return transferred.floatValue() / size.floatValue();
    }
}
