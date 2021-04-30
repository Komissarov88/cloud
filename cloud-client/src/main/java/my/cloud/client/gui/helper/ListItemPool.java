package my.cloud.client.gui.helper;

import my.cloud.client.gui.elements.impl.ListItem;

import java.util.ArrayList;
import java.util.List;

public class ListItemPool {

    private final List<ListItem> activeObjects = new ArrayList<>();
    private final List<ListItem> freeObjects = new ArrayList<>();

    public ListItem obtain(String path, String size) {
        ListItem object;
        if (freeObjects.isEmpty()) {
            object = new ListItem(path, size);
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
