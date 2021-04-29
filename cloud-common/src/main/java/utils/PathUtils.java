package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class PathUtils {

    private PathUtils() {}

    public static List<File> getFilesListRecursively(Path path) {
        List<File> files = null;
        try {
            files = Files.walk(path)
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .filter(File::canRead)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

    public static long getSize(List<File> files) {
        return files.stream()
                .mapToLong(File::length)
                .sum();
    }

    public static boolean isPathsParentAndChild(Path root, Path child) {
        return root.resolve(child).normalize().startsWith(root);
    }

    public static String[] lsDirectory(Path path) {
        File directory = path.toFile();

        if ( !directory.exists()) {
            return new String[0];
        }

        if (directory.isFile()) {
            return new String[]{directory.toString(), String.valueOf(directory.length())};
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return new String[0];
        }

        List<String> dirs = new LinkedList<>();
        List<String> nestedFiles = new LinkedList<>();

        final String directoryPrefix = "D";
        dirs.add("..");
        dirs.add(directoryPrefix);

        for (File f : files) {
            if (f.isDirectory()) {
                dirs.add(directory.toPath().relativize(f.toPath()).toString());
                dirs.add(directoryPrefix);
            } else {
                nestedFiles.add(directory.toPath().relativize(f.toPath()).toString());
                nestedFiles.add(getFileSize(f));
            }
        }
        dirs.addAll(nestedFiles);
        String[] args = new String[dirs.size()];
        return dirs.toArray(args);
    }

    private static String getFileSize(File childFile) {
        long size = childFile.length();
        final long KILOBYTE = 1024;
        final long MEGABYTE = 1_048_576;
        final long GIGABYTE = 1_073_741_824;

        if (size >= GIGABYTE / 10) {
            return String.format("%.2f Gb", (float) size / GIGABYTE);
        }
        if (size >= MEGABYTE / 10) {
            return String.format("%.2f Mb", (float) size / MEGABYTE);
        }
        if (size >= KILOBYTE / 10) {
            return String.format("%.2f Kb", (float) size / KILOBYTE);
        }

        return String.format("%d b", size);
    }
}
