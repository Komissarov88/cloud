package files.service.impl;

import files.service.FileTransferProgressService;
import utils.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class FileTransferProgressServiceImpl implements FileTransferProgressService {

    private final ConcurrentHashMap<Path, Progress> progressMap;

    private static class Progress {
        long size;
        AtomicLong transferred;

        public Progress(long size) {
            this.size = size;
            this.transferred = new AtomicLong(0);
        }
    }

    public FileTransferProgressServiceImpl() {
        progressMap = new ConcurrentHashMap<>();
    }

    @Override
    public void add(Path path, long size) {
        if (progressMap.putIfAbsent(path, new Progress(size)) != null) {
            Logger.warning("transfer already exists: " + path);
        }
    }

    @Override
    public void increment(Path path, int transferred) {
        Progress p = progressMap.get(path);
        if (p != null) {
            p.transferred.addAndGet(transferred);
        } else {
            Logger.warning("no such transfer: " + path);
            Logger.info("current jobs are: " + getTransferList());
        }
    }

    @Override
    public float progress(Path path) {
        Progress progress = progressMap.get(path);
        if (progress == null) {
            return -1;
        }
        if (progress.transferred.get() >= progress.size) {
            progressMap.remove(path);
            return 1;
        }
        return (progress.transferred.floatValue()) / (float) (progress.size);
    }

    @Override
    public List<Path> getTransferList() {
        return new ArrayList<>(progressMap.keySet());
    }

    @Override
    public float totalProgress() {
        if (progressMap.isEmpty()) {
            return -1;
        }

        AtomicLong transferred = new AtomicLong(0);
        AtomicLong size = new AtomicLong(0);
        progressMap.forEach((key, val) -> {
            transferred.addAndGet(val.transferred.get());
            size.addAndGet(val.size);
        });

        return transferred.floatValue() / size.floatValue();
    }

    @Override
    public void remove(Path path) {
        Progress progress = progressMap.get(path);
        if (progress != null) {
            if (progress.transferred.get() != progress.size) {
                Logger.warning("removing incomplete transfer: " + path);
            }
            progressMap.remove(path);
        } else {
            Logger.warning("no such transfer: " + path);
        }
    }
}
