package my.cloud.client.gui.elements;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ListItem extends VBox {

    public ListItem() {

        try {
            Pane pane = FXMLLoader.load(getClass().getResource("/view/listItemView.fxml"));
            getChildren().add(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
