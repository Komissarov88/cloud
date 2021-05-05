package my.cloud.client.gui.elements.impl;

import javafx.scene.control.ChoiceDialog;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

/**
 * Choose root i.e. hard drive, give multiple options only on widows
 */
public class RootChoiceDialog extends ChoiceDialog<File> {

    public RootChoiceDialog() {
        super(null, Arrays.asList(File.listRoots()));

        setTitle("Cloud");
        setHeaderText("Root select");
        setContentText("Choose your root");
    }

    public File getRoot() {
        Optional<File> result = showAndWait();
        return result.orElse(null);
    }
}
