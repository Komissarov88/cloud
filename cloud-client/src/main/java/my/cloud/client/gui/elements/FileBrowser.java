package my.cloud.client.gui.elements;

import files.service.FileTransferProgressService;

import java.nio.file.Path;
import java.util.List;

public interface FileBrowser {

    void refreshView(String... args);
    void changeDirectory(Path path);
    void updateListView(String[] files);
    Path getCurrentDirectory();
    List<Path> getSelectedFilePaths();
    void setProgressService(FileTransferProgressService progressService);
    void startProgressAnimation(String... args);
    boolean handleAnimation(long now);
}
