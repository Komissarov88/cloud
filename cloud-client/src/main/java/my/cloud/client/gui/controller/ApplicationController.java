package my.cloud.client.gui.controller;

import command.domain.CommandCode;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import my.cloud.client.factory.Factory;
import my.cloud.client.gui.elements.ListItem;
import my.cloud.client.gui.helper.PaneCrossfade;
import my.cloud.client.service.NetworkService;
import utils.Logger;
import utils.PathUtils;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ResourceBundle;

public class ApplicationController implements Initializable {

    public TextField commandTextField;

    public NetworkService networkService;
    public ProgressBar progressBar;
    public AnimationTimer timer;
    public VBox authForm;
    public Label clientPath;
    public Label ServerPath;
    public ListView<ListItem> clientListView;
    public ListView<ListItem> serverListView;
    public TextField loginTextField;
    public PasswordField passwordTextField;
    public Pane serverListPane;
    private PaneCrossfade authToServerViewTransition;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        networkService = Factory.getNetworkService();
        networkService.setCommandCodeListener(CommandCode.SUCCESS, this::authenticationListener);
        networkService.setCommandCodeListener(CommandCode.LS, this::updateServerListView);

        setupGUI();

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

    private void setupGUI() {
        authToServerViewTransition = new PaneCrossfade(authForm, serverListPane, 15);

        clientListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        serverListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        serverListView.getItems().add(0, new ListItem("name", "D"));
        serverListView.getItems().add(0, new ListItem("eman", "D"));

        String[] files = PathUtils.lsDirectory(networkService.getCurrentPath());
        for (int i = 0; i <= files.length - 2; i += 2) {
            clientListView.getItems().add(new ListItem(files[i], files[i+1]));
        }
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
        Logger.info("shutdown");
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

    public void login(ActionEvent actionEvent) {
        if (!networkService.isConnected()) {
            networkService.connect(loginTextField.getText().trim(), passwordTextField.getText().trim());
        }
    }

    public void authenticationListener(String[] args) {
        authToServerViewTransition.start();
        networkService.requestFileList();
    }

    public void updateServerListView(String[] files) {
        Platform.runLater(()->{
            serverListView.getItems().clear();
            for (int i = 0; i <= files.length - 2; i += 2) {
                serverListView.getItems().add(new ListItem(files[i], files[i+1]));
            }
        });
    }

    public void infoListener(String[] args) {

    }

    public void register(ActionEvent actionEvent) {
    }

    public void logout(ActionEvent actionEvent) {
        if (networkService.isConnected()) {
            authToServerViewTransition.start();
            shutdown();
        }
    }

    public void quit(ActionEvent actionEvent) {
        shutdown();
        Platform.exit();
    }
}
