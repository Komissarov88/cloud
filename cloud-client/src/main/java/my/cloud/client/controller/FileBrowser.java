package my.cloud.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import my.cloud.client.factory.Factory;
import my.cloud.client.service.NetworkService;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class FileBrowser implements Initializable {

    public TextField commandTextField;
    public TextArea commandResultTextArea;

    public NetworkService networkService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        networkService = Factory.getNetworkService();
    }

    /**
     * Temporary authentication
     */
    public void sendCommand(ActionEvent actionEvent) {
        networkService.connect("user", commandTextField.getText().trim());
        commandTextField.clear();
        commandTextField.requestFocus();
    }

    public void shutdown() {
        networkService.closeConnection();
    }

    /**
     * Download file from ./data dir to ./
     */
    public void download(ActionEvent actionEvent) {
        networkService.downloadFile(Paths.get("./data", commandTextField.getText().trim()));
        commandTextField.clear();
        commandTextField.requestFocus();
    }

    /**
     * Upload file from ./ to ./data dir
     */
    public void upload(ActionEvent actionEvent) {
        networkService.uploadFile(new File(commandTextField.getText().trim()));
        commandTextField.clear();
        commandTextField.requestFocus();
    }
}
