package my.cloud.client.gui.elements.impl;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class ListItem extends VBox {

    private Label name;
    private Label size;
    ProgressBar progressBar;

    private Pane pane;
    private void loadFXML() {
        try {
            pane = FXMLLoader.load(
                    Objects.requireNonNull(
                            ListItem.class.getResource("/view/listItemView.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ListItem(String name, String size) {
        loadFXML();
        getChildren().add(pane);

        this.name = (Label) pane.lookup("#name");
        this.size = (Label) pane.lookup("#size");
        progressBar = (ProgressBar) pane.lookup("#progress");

        this.size.setText(size);
        this.name.setText(name);

    }

    public Path getPath() {
        return Paths.get(name.getText());
    }
}
