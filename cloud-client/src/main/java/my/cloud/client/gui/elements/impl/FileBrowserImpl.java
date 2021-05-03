package my.cloud.client.gui.elements.impl;

import files.service.FileTransferProgressService;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import my.cloud.client.gui.elements.FileBrowser;
import my.cloud.client.gui.helper.AnimatedProgressBar;
import utils.Logger;
import utils.PathUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
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
        Button home = (Button) pane.lookup("#home");
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

            }
        });
        home.setOnAction(event -> changeDirectory(root));
    }

    @Override
    public void refreshView() {
        changeDirectory(currentPath);
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
            pathLabel.setText(currentPath.getFileName() == null ? "" : currentPath.getFileName().toString());
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
        totalProgress.startAnimation();
    }

    protected boolean updateProgress() {
        List<Path> jobs = progressService.getTransferList();
        boolean anyTransferCompleted = false;
        for (Path job : jobs) {
            float progress = progressService.progress(job);
            if (progress >= 1) {
                anyTransferCompleted = true;
            }
            for (FileItem item : listView.getItems()) {
                if (job.equals(item.getPath())) {
                    item.setProgress(progress);
                    break;
                }
            }
        }
        return anyTransferCompleted;
    }

    @Override
    public boolean handleAnimation(long now) {
        if (progressService == null) {
            Logger.warning("progress service not set");
            return false;
        }
        float total = progressService.totalProgress();
        totalProgress.setProgress(total);
        return updateProgress();
    }
}
