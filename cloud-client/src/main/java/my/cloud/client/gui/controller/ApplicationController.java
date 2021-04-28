package my.cloud.client.gui.controller;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import my.cloud.client.factory.Factory;
import my.cloud.client.gui.elements.ListItem;
import my.cloud.client.gui.helper.PaneCrossfade;
import my.cloud.client.service.NetworkService;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class ApplicationController implements Initializable {

    public TextField commandTextField;
    public TextArea commandResultTextArea;

    public NetworkService networkService;
    public ProgressBar progressBar;
    public AnimationTimer timer;
    public VBox fileBrowser;
    public VBox authForm;
    public HBox clientPathHBox;
    public HBox ServerPathHBox;
    public ListView<ListItem> clientListView;
    private PaneCrossfade cf;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        networkService = Factory.getNetworkService();
        cf = new PaneCrossfade(authForm, fileBrowser, 15);
        clientListView.getItems().add(0, new ListItem());

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                float progress = Factory.getUploadProgressService().progress(Paths.get(".").normalize());
                progressBar.setProgress(progress);
                if (progress >= 1) {
                    progressBar.setProgress(0);
                    stop();
                }
            }
            @Override
            public void start() {
                progressBar.setProgress(0);
                super.start();
            }
        };
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
        networkService.downloadFile(Paths.get(commandTextField.getText().trim()));
        commandTextField.clear();
        commandTextField.requestFocus();
    }

    /**
     * Upload file from ./ to ./data dir
     */
    public void upload(ActionEvent actionEvent) {
        timer.start();
        networkService.uploadFile(new File(commandTextField.getText().trim()));
        commandTextField.clear();
        commandTextField.requestFocus();
    }

    public void transition(ActionEvent actionEvent) {
        cf.start();
    }

    public void login(ActionEvent actionEvent) {
    }

    public void register(ActionEvent actionEvent) {
    }
}
