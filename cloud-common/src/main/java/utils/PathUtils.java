package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class PathUtils {

    private PathUtils() {}

    public static File[] ls(Path path) {
        return new File[0];
    }

    public static List<File> getFilesList(Path path) {
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
}
