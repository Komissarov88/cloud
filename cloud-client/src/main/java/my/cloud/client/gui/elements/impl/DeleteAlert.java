package my.cloud.client.gui.elements.impl;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

import java.nio.file.Path;
import java.util.List;

/**
 * Request delete confirmation from user
 */
public class DeleteAlert extends Alert {

    private final TextArea textArea;

    public DeleteAlert() {
        super(Alert.AlertType.CONFIRMATION);

        setTitle("Cloud");
        setHeaderText("Confirm delete");
        setContentText("Files:");

        textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 1);

        getDialogPane().expandedProperty().set(true);
        getDialogPane().setExpandableContent(expContent);
    }

    public void setText(List<Path> text) {
        textArea.clear();
        for (Path s : text) {
            textArea.appendText(s.toString() + System.lineSeparator());
        }
    }
}
