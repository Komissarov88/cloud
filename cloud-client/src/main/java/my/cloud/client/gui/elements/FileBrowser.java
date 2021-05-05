package my.cloud.client.gui.elements;

import files.service.FileTransferProgressService;

import java.nio.file.Path;
import java.util.List;

public interface FileBrowser {

    /**
     * Updates current directory
     * @param args ignored (need for callback compatibility)
     */
    void refreshView(String... args);

    /**
     * Switch to another directory
     * @param path directory to switch, if file - does nothing
     */
    void changeDirectory(Path path);

    /**
     * Rename all lines in list view to arg
     * @param files formatted array of all files to show in view
     */
    void updateListView(String[] files);

    /**
     * @return path to current directory
     */
    Path getCurrentDirectory();

    /**
     * @return list of all selected items paths, except ".." parent dir
     */
    List<Path> getSelectedFilePaths();

    /**
     * @return list of all items paths, except ".." parent dir
     */
    List<Path> getCurrentFilePaths();

    /**
     * Set service to retrieve information for progress bars
     * @param progressService download or upload progress service
     */
    void setProgressService(FileTransferProgressService progressService);

    /**
     * Start animation timer for total progress bar
     * @param args
     */
    void startProgressAnimation(String... args);

    /**
     * Updates animation of items progress bars
     * @param now timestamp of current render frame
     * @return true if any transfer task completes
     */
    boolean handleAnimation(long now);

    /**
     * Reset and hides all progress bars
     */
    void clearAllProgressBars();
}
