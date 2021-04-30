package my.cloud.client.gui.elements.impl;

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
    protected ItemListView listView;
    private Pane pane;
    private ListItemPool listItemPool;

    private void loadFXML() {
        try {
            pane = FXMLLoader.load(
                    Objects.requireNonNull(
                            ListItem.class.getResource("/view/fileBrowserView.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileBrowserImpl() {
        loadFXML();
        getChildren().add(pane);
        pathLabel = (Label) pane.lookup("#currentPath");
        listView = (ItemListView) pane.lookup("#listView");
        Button home = (Button) pane.lookup("#home");

        root = Paths.get(".").toAbsolutePath();
        listItemPool = new ListItemPool();

        pathLabel.setText(root.toString());
        currentPath = root;
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        listView.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() > 1) {
                ListItem item = listView.getSelectionModel().getSelectedItem();
                if (item != null) {
                    changeDirectory(item.getPath());
                }
            }
        });
        listView.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                ListItem item = listView.getSelectionModel().getSelectedItem();
                if (item != null) {
                    changeDirectory(item.getPath());
                }
            }
        });
        home.setOnAction(event -> changeDirectory(root));
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
            List<ListItem> items = listView.getItems();
            items.clear();
            listItemPool.freeAll();
            for (int i = 0; i <= files.length - 2; i += 2) {
                items.add(listItemPool.obtain(files[i], files[i + 1]));
            }
        });
    }

    @Override
    public List<Path> getSelectedFilePaths() {
        List<Path> list = listView.getSelectionModel().getSelectedItems()
                .stream()
                .map(ListItem::getPath)
                .filter((p) -> !p.endsWith(".."))
                .collect(Collectors.toList());

        return list;
    }

    @Override
    public Path getCurrentDirectory() {
        return currentPath;
    }
}
