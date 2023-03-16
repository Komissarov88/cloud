package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class PathUtils {

    private PathUtils() {
    }

    /**
     * Get list of all files in path
     *
     * @param path to look
     * @return files
     */
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

    /**
     * @param files to sum length
     * @return total size
     */
    public static long getSize(List<File> files) {
        return files.stream()
                .mapToLong(File::length)
                .sum();
    }

    /**
     * @param root  path
     * @param child path
     * @return true if root is actually root
     */
    public static boolean isPathsParentAndChild(Path root, Path child) {
        return child.normalize().startsWith(root);
    }

    private static Path relativize(Path path, Path root) {
        if (root == null) {
            return path;
        }
        return root.relativize(path);
    }

    /**
     * Formatted list of directory content
     *
     * @param path to look
     * @param root path to relativize from
     * @return array of name and dir prefix or file size
     */
    public static String[] lsDirectory(Path path, Path root) {
        final String DIR_PREFIX = "D";
        File directory = path.toFile();

        if (!directory.exists() || directory.isFile()) {
            return new String[0];
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return new String[0];
        }

        List<String> dirs = new LinkedList<>();
        List<String> nestedFiles = new LinkedList<>();

        dirs.add(relativize(path.normalize().resolve(".."), root).toString());
        dirs.add(DIR_PREFIX);

        for (File f : files) {
            if (f.isDirectory()) {
                dirs.add(relativize(f.toPath(), root).normalize().toString());
                dirs.add(DIR_PREFIX);
            } else {
                nestedFiles.add(relativize(f.toPath(), root).normalize().toString());
                nestedFiles.add(getFileSize(f));
            }
        }
        dirs.addAll(nestedFiles);
        String[] args = new String[dirs.size()];
        return dirs.toArray(args);
    }

    /**
     * String representation of file size
     *
     * @param file to check
     * @return formatted size
     */
    private static String getFileSize(File file) {
        long size = file.length();
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
