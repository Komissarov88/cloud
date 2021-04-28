package my.cloud.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import my.cloud.client.gui.controller.ApplicationController;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/mainWindowView.fxml"));
        Parent parent = loader.load();
        primaryStage.setScene(new Scene(parent));
        primaryStage.setTitle("My cloud client");
        primaryStage.setResizable(true);

        ApplicationController controller = loader.getController();
        primaryStage.setOnCloseRequest((event) -> controller.shutdown());
        primaryStage.show();
    }


}
