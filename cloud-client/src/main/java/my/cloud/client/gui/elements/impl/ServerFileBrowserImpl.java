package my.cloud.client.gui.elements.impl;

import javafx.application.Platform;
import my.cloud.client.factory.Factory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerFileBrowserImpl extends FileBrowserImpl {

    public ServerFileBrowserImpl() {
        super();
        root = Paths.get("/");
        currentPath = root;
    }

    @Override
    public void changeDirectory(Path path) {
        Factory.getNetworkService().requestFileList(path.toString());
    }

    @Override
    public void updateListView(String[] files) {
        super.updateListView(files);
        currentPath = Paths.get(files[0]).getParent();


        String pathName;
        if (currentPath == null) {
            pathName = "root";
            currentPath = root;
        } else {
            pathName = currentPath.getFileName().toString();
        }
        Platform.runLater(() -> pathLabel.setText(pathName));
    }
}