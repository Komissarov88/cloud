package my.cloud.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import my.cloud.client.factory.Factory;
import my.cloud.client.service.NetworkService;

import java.net.URL;
import java.util.ResourceBundle;

public class FileBrowser implements Initializable {

    public TextField commandTextField;
    public TextArea commandResultTextArea;

    public NetworkService networkService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        networkService = Factory.getNetworkService();

        createCommandResultHandler();
    }

    private void createCommandResultHandler() {
        new Thread(() -> {
            while (true) {
                String resultCommand = networkService.readCommandResult();
                Platform.runLater(() -> commandResultTextArea.appendText(resultCommand + System.lineSeparator()));
            }
        }).start();
    }

    public void sendCommand(ActionEvent actionEvent) {
        networkService.sendCommand(commandTextField.getText().trim());
        commandTextField.clear();
        commandTextField.requestFocus();
    }

    public void shutdown() {
        networkService.closeConnection();
    }
}
