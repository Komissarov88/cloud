package my.cloud.client.gui.elements;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

public class ListItem extends VBox {

    Label name;
    Label size;
    ProgressBar progressBar;

    public ListItem(String name, String size) {
        try {
            Pane pane = FXMLLoader.load(
                    Objects.requireNonNull(
                            getClass().getResource("/view/listItemView.fxml")));
            getChildren().add(pane);

            this.name = (Label) pane.lookup("#name");
            this.size = (Label) pane.lookup("#size");
            progressBar = (ProgressBar) pane.lookup("#progress");

            this.size.setText(size);
            this.name.setText(name);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
