package my.cloud.client.gui.elements.impl;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import utils.Logger;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class FileRewriteAlert extends Alert {

    private ButtonType rewrite;
    private ButtonType skip;
    private ButtonType cancel;
    private List<Path> thisSideList;
    private boolean isDownloadAlert;
    private final TextArea textArea;

    public FileRewriteAlert() {
        super(Alert.AlertType.CONFIRMATION);

        setTitle("Cloud");
        setHeaderText("Files already exist");
        setContentText("Files:");

        rewrite = new ButtonType("Rewrite");
        skip = new ButtonType("Skip");
        cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        getButtonTypes().setAll(rewrite, skip, cancel);

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

    public void reset( List<Path> files, boolean isDownloadAlert) {
        thisSideList = files;
        this.isDownloadAlert = isDownloadAlert;
    }

    public List<Path> getTransferList(List<Path> otherSideList) {
        if (thisSideList == null) {
            Logger.info("selected items not set");
            return Collections.emptyList();
        }

        textArea.clear();

        List<Path> overlapping = new LinkedList<>();
        for (Path thisSidePath : thisSideList) {
            for (Path other : otherSideList) {
                if (isPathsSame(thisSidePath, other)) {
                    overlapping.add(thisSidePath);
                    textArea.appendText(thisSidePath + System.lineSeparator());
                    break;
                }
            }
        }

        if (overlapping.isEmpty()) {
            return thisSideList;
        }

        Optional<ButtonType> result = showAndWait();
        System.out.println(result.get());
        if (result.get() == cancel) {
            return Collections.emptyList();
        } else if (result.get() == skip) {
            thisSideList.removeAll(overlapping);
        }

        List<Path> transferList = new LinkedList<>(thisSideList);
        thisSideList = null;
        return transferList;
    }

    private boolean isPathsSame(Path thisSide, Path otherSide) {
        if (isDownloadAlert) {
            return otherSide.endsWith(thisSide);
        } else {
            return thisSide.endsWith(otherSide);
        }
    }
}
