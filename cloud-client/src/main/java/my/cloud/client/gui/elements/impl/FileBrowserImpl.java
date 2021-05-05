package my.cloud.client.gui.elements.impl;

import files.domain.FileTransferStatus;
import files.service.FileTransferProgressService;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import my.cloud.client.gui.elements.FileBrowser;
import my.cloud.client.gui.helper.FileItemPool;
import org.apache.commons.io.FileUtils;
import utils.Logger;
import utils.PathUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Local file system file browser.
 * Base class for server file browser.
 */
public class FileBrowserImpl extends AnchorPane implements FileBrowser {

    protected Path currentPath;
    protected Path root;
    protected Label pathLabel;
    protected FileItemListView listView;
    private Pane pane;
    private final AnimatedProgressBar totalProgress;
    private final FileItemPool fileItemPool;
    protected FileTransferProgressService progressService;
    protected DeleteAlert alert;
    protected Button rootBtn;
    protected ContextMenu contextMenu;
    protected MenuItem delete;
    protected MenuItem open;

    private void loadFXML() {
        try {
            pane = FXMLLoader.load(
                    Objects.requireNonNull(
                            FileItem.class.getResource("/view/fileBrowserView.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileBrowserImpl() {
        loadFXML();
        getChildren().add(pane);

        // fxml lookup
        pathLabel = (Label) pane.lookup("#currentPath");
        listView = (FileItemListView) pane.lookup("#listView");
        Button homeBtn = (Button) pane.lookup("#home");
        rootBtn = (Button) pane.lookup("#root");
        totalProgress = (AnimatedProgressBar) pane.lookup("#totalProgress");

        root = Paths.get(".").toAbsolutePath();
        fileItemPool = new FileItemPool();

        pathLabel.setText(root.toString());
        currentPath = root;

        setupListView();
        createContextMenu();

        // change directory to home action
        homeBtn.setOnAction(event -> {
            changeDirectory(root);
            rootBtn.setText(root.getRoot().toString());
        });

        // chose root dialog
        rootBtn.setText(root.getRoot().toString());
        rootBtn.setOnAction(event -> {
            File res = new RootChoiceDialog().getRoot();
            if (res != null) {
                rootBtn.setText(res.toString());
                changeDirectory(res.toPath());
            }
        });

        alert = new DeleteAlert();
    }

    /**
     * Rises delete confirm dialog if needed
     * @return true on confirm
     */
    protected boolean deleteConfirm() {
        List<Path> list = getSelectedFilePaths();
        if (list.isEmpty()) {
            return false;
        }
        alert.setText(list);
        Optional<ButtonType> result = alert.showAndWait();
        return result.orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    /**
     * Delete local file system files
     * @param paths paths to delete
     */
    protected void delete(List<Path> paths) {
        for (Path path : paths) {
            if (FileUtils.deleteQuietly(path.toFile())) {
                refreshView();
            }
        }
    }

    /**
     * Setup GUI and callbacks for file browser
     */
    private void setupListView() {
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        listView.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() > 1) {
                FileItem item = listView.getSelectionModel().getSelectedItem();
                if (item != null) {
                    changeDirectory(item.getPath());
                }
            }
        });
        listView.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                FileItem item = listView.getSelectionModel().getSelectedItem();
                if (item != null) {
                    changeDirectory(item.getPath());
                }
            } else if (event.getCode().equals(KeyCode.F5)) {
                refreshView();
            } else if (event.getCode().equals(KeyCode.DELETE)) {
                if (deleteConfirm()) {
                    delete(getSelectedFilePaths());
                }
            } else if (event.getCode().equals(KeyCode.BACK_SPACE)) {
                changeDirectory(listView.getItems().get(0).getPath());
            }
        });
    }

    /**
     * Context menu setup
     */
    private void createContextMenu() {
        contextMenu = new ContextMenu();

        open = new MenuItem("Open");
        delete = new MenuItem("Delete");

        contextMenu.getItems().addAll(open, delete);

        delete.setOnAction((event) -> {
            if (deleteConfirm()) {
                delete(getSelectedFilePaths());
            }
        });
        open.setOnAction(event -> {
            try {
                Desktop.getDesktop().open(listView.getSelectionModel().getSelectedItem().getPath().toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        listView.setContextMenu(contextMenu);
    }

    @Override
    public void refreshView(String... args) {
        changeDirectory(currentPath);
    }

    /**
     * Set current path label from last 3 names of path
     * @param path any path
     */
    protected void setCurrentPathLabel(Path path) {
        if (path == null) {
            pathLabel.setText("");
            return;
        }
        int length = path.getNameCount();
        int fromName = Math.max(length - 3, 0);
        if (length == fromName) {
            pathLabel.setText("");
        } else {
            pathLabel.setText((length > 3 ? "... " : "") + currentPath.subpath(fromName, length));
        }
    }

    @Override
    public void changeDirectory(Path path) {
        Path newPath = currentPath.resolve(path).normalize();
        if (!newPath.toFile().isDirectory()) {
            return;
        }

        String[] files = PathUtils.lsDirectory(newPath, null);
        if (files.length > 0) {
            currentPath = newPath;
            setCurrentPathLabel(currentPath);
            updateListView(files);
        }
    }

    @Override
    public void updateListView(String[] files) {
        Platform.runLater(() -> {
            List<FileItem> items = listView.getItems();
            items.clear();
            fileItemPool.freeAll();
            for (int i = 0; i <= files.length - 2; i += 2) {
                items.add(fileItemPool.obtain(files[i], files[i + 1]));
            }
        });
    }

    @Override
    public List<Path> getSelectedFilePaths() {
        return listView
                .getSelectionModel()
                .getSelectedItems()
                .stream()
                .filter(fileItem -> fileItem.progressBar.getProgress() <= 0)
                .map(FileItem::getPath)
                .filter((p) -> !p.endsWith(".."))
                .collect(Collectors.toList());
    }

    @Override
    public List<Path> getCurrentFilePaths() {
        return listView
                .getItems()
                .stream()
                .skip(1)
                .map(FileItem::getPath)
                .collect(Collectors.toList());
    }

    @Override
    public Path getCurrentDirectory() {
        return currentPath;
    }

    @Override
    public void setProgressService(FileTransferProgressService progressService) {
        this.progressService = progressService;
    }

    @Override
    public void startProgressAnimation(String... args) {
        if (progressService.getTransferList().size() > 0) {
            totalProgress.startAnimation();
        }
    }

    /**
     * Find items that represent current transfer jobs and update it progress bars
     * @return true if any job completed
     */
    private boolean updateProgress() {
        List<Path> jobs = progressService.getTransferList();
        if (jobs.size() == 0) {
            return false;
        }
        boolean anyTransferCompleted = false;
        for (Path job : jobs) {
            float progress = progressService.progress(job);
            if (progress >= FileTransferStatus.DONE.value) {
                anyTransferCompleted = true;
            }
            for (FileItem item : listView.getItems()) {
                if (item.getPath().equals(job)) {
                    if (progress == FileTransferStatus.INTERRUPTED.value) {
                        item.progressBar.reset();
                        progressService.remove(item.getPath());
                        break;
                    }
                    item.setProgress(progress);
                    break;
                }
            }
        }
        totalProgress.setProgress(progressService.totalProgress());
        return anyTransferCompleted;
    }

    @Override
    public boolean handleAnimation(long now) {
        if (progressService == null) {
            Logger.warning("progress service not set");
            return false;
        }
        return updateProgress();
    }

    @Override
    public void clearAllProgressBars() {
        listView.getItems().forEach(fileItem -> fileItem.progressBar.reset());
        totalProgress.reset();
    }
}
