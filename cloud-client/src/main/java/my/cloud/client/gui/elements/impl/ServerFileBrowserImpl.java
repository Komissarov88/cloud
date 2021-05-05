package my.cloud.client.gui.elements.impl;

import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import my.cloud.client.factory.Factory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class ServerFileBrowserImpl extends FileBrowserImpl {

    public ServerFileBrowserImpl() {
        super();
        root = Paths.get("/");
        currentPath = root;
        rootBtn.setDisable(true);
        rootBtn.setManaged(false);
        contextMenu.getItems().remove(open);
    }

    @Override
    protected boolean deleteConfirm() {
        alert.setText(getSelectedFilePaths());
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    @Override
    protected void delete(List<Path> paths) {
        Factory.getNetworkService().removeFile(paths);
    }


    @Override
    public void changeDirectory(Path path) {
        Factory.getNetworkService().requestFileList(path.toString());
    }

    @Override
    public void updateListView(String[] files) {
        super.updateListView(files);
        currentPath = Paths.get(files[0]).getParent();
        Platform.runLater(() -> setCurrentPathLabel(currentPath));
        if (currentPath == null) {
            currentPath = root;
        }
    }

    @Override
    public void clearAllProgressBars() {
        super.clearAllProgressBars();
        Platform.runLater(() -> listView.getItems().clear());
    }
}