package my.cloud.client.gui.elements.impl;

import files.service.FileTransferProgressService;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import my.cloud.client.gui.elements.FileBrowser;
import org.apache.commons.io.FileUtils;
import utils.Logger;
import utils.PathUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
        pathLabel = (Label) pane.lookup("#currentPath");
        listView = (FileItemListView) pane.lookup("#listView");
        Button homeBtn = (Button) pane.lookup("#home");
        rootBtn = (Button) pane.lookup("#root");
        totalProgress = (AnimatedProgressBar) pane.lookup("#totalProgress");

        root = Paths.get(".").toAbsolutePath();
        fileItemPool = new FileItemPool();

        pathLabel.setText(root.toString());
        currentPath = root;
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
        homeBtn.setOnAction(event -> {
            changeDirectory(root);
            rootBtn.setText(root.getRoot().toString());
        });

        rootBtn.setText(root.getRoot().toString());
        rootBtn.setOnAction(event -> {
            File res = new RootChoiceDialog().getRoot();
            if (res != null) {
                rootBtn.setText(res.toString());
                changeDirectory(res.toPath());
            }
        });

        createContextMenu();

        alert = new DeleteAlert();
    }

    protected boolean deleteConfirm() {
        alert.setText(getSelectedFilePaths());
        Optional<ButtonType> result = alert.showAndWait();
        return result.orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    protected void delete(List<Path> paths) {
        for (Path path : paths) {
            if (FileUtils.deleteQuietly(path.toFile())) {
                refreshView();
            }
        }
    }

    private void createContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem delete = new MenuItem("Delete");
        MenuItem rename = new MenuItem("Rename");

        contextMenu.getItems().add(delete);
        contextMenu.getItems().add(rename);

        delete.setOnAction((event) -> {
            if (deleteConfirm()) {
                delete(getSelectedFilePaths());
            }
        });

        listView.setContextMenu(contextMenu);
    }

    @Override
    public void refreshView(String... args) {
        changeDirectory(currentPath);
    }

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

    private boolean updateProgress() {
        List<Path> jobs = progressService.getTransferList();
        if (jobs.size() == 0) {
            return false;
        }
        boolean anyTransferCompleted = false;
        for (Path job : jobs) {
            float progress = progressService.progress(job);
            if (progress >= 1) {
                anyTransferCompleted = true;
            }
            for (FileItem item : listView.getItems()) {
                if (item.getPath().equals(job)) {
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
}
