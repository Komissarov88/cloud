package my.cloud.client.gui.helper;

import my.cloud.client.gui.elements.impl.FileItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Pool of file items, needed to avoid loading of fxml resource on every FileItem instance creation
 */
public class FileItemPool {

    private final List<FileItem> activeObjects = new ArrayList<>();
    private final List<FileItem> freeObjects = new ArrayList<>();

    public FileItem obtain(String path, String size) {
        FileItem object;
        if (freeObjects.isEmpty()) {
            object = new FileItem(path, size);
        } else {
            object = freeObjects.remove(freeObjects.size() - 1);
            object.set(path, size);
        }
        activeObjects.add(object);
        return object;
    }

    public void freeAll() {
        freeObjects.addAll(activeObjects);
        activeObjects.clear();
    }
}
