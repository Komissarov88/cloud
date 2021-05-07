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
import my.cloud.client.gui.elements.impl.FileRewriteAlert;
import my.cloud.client.gui.elements.impl.TemporaryMessageLabel;
import my.cloud.client.gui.helper.PaneCrossfade;
import my.cloud.client.service.NetworkService;
import utils.Logger;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ApplicationController implements Initializable {

    private NetworkService networkService;
    public VBox authForm;
    public FileBrowser clientListView;
    public FileBrowser serverListView;
    public TextField loginTextField;
    public PasswordField passwordTextField;
    public Pane serverListPane;
    public TemporaryMessageLabel infoLabel;
    private PaneCrossfade authViewToServerViewTransition;
    private AnimationTimer progressAnimation;
    private FileRewriteAlert fileRewriteAlert;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        networkService = Factory.getNetworkService();

        // Set callback on various commandCodes
        networkService.setCommandCodeListener(CommandCode.SUCCESS, this::onAuthenticationSuccess);
        networkService.setCommandCodeListener(CommandCode.LS, serverListView::updateListView);
        networkService.setCommandCodeListener(CommandCode.REFRESH_VIEW, serverListView::refreshView);
        networkService.setCommandCodeListener(CommandCode.DOWNLOAD_POSSIBLE, this::startProgressAnimation);
        networkService.setCommandCodeListener(CommandCode.UPLOAD_POSSIBLE, this::startProgressAnimation);
        networkService.setCommandCodeListener(CommandCode.FAIL, this::setInfoText);

        // Called on logout or force disconnect
        networkService.setOnChannelInactive(() -> {
            if (!authViewToServerViewTransition.onA()) {
                authViewToServerViewTransition.start();
                progressAnimation.stop();
                clientListView.clearAllProgressBars();
                serverListView.clearAllProgressBars();
                Factory.getUploadProgressService().clear();
                Factory.getDownloadProgressService().clear();
                Factory.getFileTransferAuthService().clean();
            }
        });

        setupGUI();
    }

    /**
     * Callback for showing info message on bottom of window
     * @param args hold info message
     */
    private void setInfoText(String[] args) {
        Platform.runLater(() -> infoLabel.updateText(Arrays.toString(args)));
    }

    /**
     * Setup file browsers, animation, and dialogs
     */
    private void setupGUI() {
        clientListView.setProgressService(Factory.getUploadProgressService());
        serverListView.setProgressService(Factory.getDownloadProgressService());

        authViewToServerViewTransition = new PaneCrossfade(authForm, serverListPane, 15);
        clientListView.changeDirectory(Paths.get("."));
        fileRewriteAlert = new FileRewriteAlert();
        progressAnimation = new AnimationTimer() {

            @Override
            public void handle(long now) {
                if (clientListView.handleAnimation(now)) {
                    serverListView.refreshView();
                }
                if (serverListView.handleAnimation(now)) {
                    clientListView.refreshView();
                }

                if (Math.abs(Factory.getUploadProgressService().totalProgress()) >= 1
                        && Math.abs(Factory.getDownloadProgressService().totalProgress()) >= 1) {
                    stop();
                }
            }

            @Override
            public void start() {
                clientListView.startProgressAnimation();
                serverListView.startProgressAnimation();
                super.start();
            }
        };
    }

    /**
     * Called when transfer job appears
     * @param args ignored
     */
    public void startProgressAnimation(String[] args) {
        progressAnimation.start();
    }

    /**
     * Close main connection
     */
    public void shutdown() {
        Logger.info("shutdown");
        networkService.closeConnection();
    }

    /**
     * Get selected files from server browser and initiates download
     * @param actionEvent ignored
     */
    public void download(ActionEvent actionEvent) {
        fileRewriteAlert.reset(serverListView.getSelectedFilePaths(), true);
        List<Path> downloadFiles = fileRewriteAlert.getTransferList(clientListView.getCurrentFilePaths());
        if (downloadFiles.size() > 0) {
            networkService.downloadFiles(clientListView.getCurrentDirectory(), downloadFiles);
        }
    }

    /**
     * Get selected files from server browser and initiates upload
     * @param actionEvent ignored
     */
    public void upload(ActionEvent actionEvent) {
        fileRewriteAlert.reset(clientListView.getSelectedFilePaths(), false);
        List<Path> uploadFiles = fileRewriteAlert.getTransferList(serverListView.getCurrentFilePaths());
        if (uploadFiles.size() > 0) {
            networkService.uploadFiles(serverListView.getCurrentDirectory(), uploadFiles);
        }
    }

    /**
     * Login to server
     * @param actionEvent ignored
     */
    public void login(ActionEvent actionEvent) {
        networkService.login(loginTextField.getText().trim(), passwordTextField.getText().trim());
        passwordTextField.setText("");
    }

    /**
     * Request new user from server
     * @param actionEvent ignored
     */
    public void register(ActionEvent actionEvent) {
        networkService.requestRegistration(loginTextField.getText().trim(), passwordTextField.getText().trim());
        passwordTextField.setText("");
    }

    /**
     * Callback on successful authentication, starts animation of server browser appearance
     * @param args hold success message
     */
    public void onAuthenticationSuccess(String[] args) {
        authViewToServerViewTransition.start();
        setInfoText(args);
        networkService.requestFileList("/");
    }

    /**
     * Starts animation of authentication form appearance
     * @param actionEvent ignored
     */
    public void logout(ActionEvent actionEvent) {
        if (networkService.isConnected()) {
            authViewToServerViewTransition.start();
            shutdown();
        }
    }

    /**
     * Close program
     * @param actionEvent ignored
     */
    public void quit(ActionEvent actionEvent) {
        shutdown();
        Platform.exit();
    }
}
