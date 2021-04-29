package my.cloud.client.gui.elements.impl;

import my.cloud.client.factory.Factory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerFileBrowserImpl extends FileBrowserImpl {

    public ServerFileBrowserImpl() {
        super();
        root = Paths.get(Factory.getNetworkService().getLogin());
    }

    @Override
    public void changeDirectory(Path path) {
    }

    @Override
    protected void onMouseDoubleClicked(ListItem item) {

    }
}
