package my.cloud.client.gui.elements;

import java.nio.file.Path;
import java.util.List;

public interface FileBrowser {

    void changeDirectory(Path path);
    void updateListView(String[] files);
    Path getCurrentDirectory();
    List<Path> getSelectedFilePaths();
}
