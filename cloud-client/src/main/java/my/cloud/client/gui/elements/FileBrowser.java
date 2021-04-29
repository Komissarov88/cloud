package my.cloud.client.gui.elements;

import java.nio.file.Path;

public interface FileBrowser {

    void changeDirectory(Path path);
    void updateListView(String[] files);
}
