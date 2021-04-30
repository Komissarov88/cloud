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
import my.cloud.client.gui.elements.FileBrowser;
import my.cloud.client.gui.helper.PaneCrossfade;
import my.cloud.client.service.NetworkService;
import utils.Logger;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

public class ApplicationController implements Initializable {

    public NetworkService networkService;
    public AnimationTimer timer;
    public VBox authForm;
    public FileBrowser clientListView;
    public FileBrowser serverListView;
    public TextField loginTextField;
    public PasswordField passwordTextField;
    public Pane serverListPane;
    private PaneCrossfade authViewToServerViewTransition;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        networkService = Factory.getNetworkService();
        networkService.setCommandCodeListener(CommandCode.SUCCESS, this::onAuthenticationSuccess);
        networkService.setCommandCodeListener(CommandCode.LS, serverListView::updateListView);

        setupGUI();
    }

    private void setupGUI() {
        authViewToServerViewTransition = new PaneCrossfade(authForm, serverListPane, 15);
        clientListView.changeDirectory(Paths.get("."));
    }

    public void shutdown() {
        Logger.info("shutdown");
        networkService.closeConnection();
    }

    public void download(ActionEvent actionEvent) {
        if (networkService.isConnected()) {
            List<Path> downloadFiles = serverListView.getSelectedFilePaths();
            for (Path downloadFile : downloadFiles) {
                networkService.downloadFile(downloadFile, clientListView.getCurrentDirectory());
            }
        }
    }

    public void upload(ActionEvent actionEvent) {
        if (networkService.isConnected()) {
            List<Path> uploadFiles = clientListView.getSelectedFilePaths();
            for (Path uploadFile : uploadFiles) {
                networkService.uploadFile(uploadFile.toFile(), serverListView.getCurrentDirectory());
            }
        }
    }

    public void login(ActionEvent actionEvent) {
        if (!networkService.isConnected()) {
            networkService.connect(loginTextField.getText().trim(), passwordTextField.getText().trim());
            passwordTextField.setText("");
        }
    }

    public void register(ActionEvent actionEvent) {
        if (!networkService.isConnected()) {
            networkService.requestRegistration(loginTextField.getText().trim(), passwordTextField.getText().trim());
            passwordTextField.setText("");
        }
    }

    public void onAuthenticationSuccess(String[] args) {
        authViewToServerViewTransition.start();
        networkService.requestFileList("/");
    }

    public void logout(ActionEvent actionEvent) {
        if (networkService.isConnected()) {
            authViewToServerViewTransition.start();
            shutdown();
        }
    }

    public void quit(ActionEvent actionEvent) {
        shutdown();
        Platform.exit();
    }


}
