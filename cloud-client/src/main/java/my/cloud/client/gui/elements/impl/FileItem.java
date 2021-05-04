package my.cloud.client.gui.elements.impl;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class FileItem extends VBox {

    private Pane pane;
    private final Label name;
    private final Label size;
    private final Tooltip tooltip;
    AnimatedProgressBar progressBar;

    private Path path;

    private void loadFXML() {
        try {
            pane = FXMLLoader.load(
                    Objects.requireNonNull(
                            FileItem.class.getResource("/view/listItemView.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileItem(String path, String size) {
        loadFXML();
        getChildren().add(pane);

        this.name = (Label) pane.lookup("#name");
        this.size = (Label) pane.lookup("#size");
        progressBar = (AnimatedProgressBar) pane.lookup("#progress");

        tooltip = new Tooltip();
        name.setTooltip(tooltip);

        setPath(path);
        this.size.setText(size);
    }

    public void setPath(String path) {
        this.path = Paths.get(path);
        this.name.setText(this.path.getFileName().toString());
        tooltip.setText(path);
    }

    public Path getPath() {
        return path;
    }

    public void set(String path, String size) {
        setPath(path);
        progressBar.reset();
        this.size.setText(size);
    }

    public void setProgress(double progress) {
        progressBar.setProgress(progress);
        progressBar.startAnimation();
    }
}
