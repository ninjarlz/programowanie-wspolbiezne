package pl.tul;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.tul.controller.FXMLController;
import pl.tul.service.FileClient;

import java.io.IOException;

public class Main {

    private static final String SCENE_VIEW_PATH = "view/scene.fxml";
    private static final String WINDOW_TITLE = "Programowanie Wspolbiezne";

    public static void main(String... args) {
        FileApplication.run(args);
    }

    public static class FileApplication extends Application {

        @Override
        public void start(Stage stage) throws Exception {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(SCENE_VIEW_PATH));
            Parent root = fxmlLoader.load();
            //FXMLController fxmlController = fxmlLoader.getController();
            //initData(fxmlController);
            Scene scene = new Scene(root);
            stage.setTitle(WINDOW_TITLE);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setOnCloseRequest((e) -> {
                Platform.exit();
                System.exit(0);
            });
            stage.show();
        }

        public static void run(String... args) {
            Platform.setImplicitExit(false);
            launch(args);
        }

        private void initData(FXMLController fxmlController) throws IOException {
            FileClient fileClient1 = fxmlController.createEmptyClient();
            fxmlController.createFile(45000L, fileClient1);
            fxmlController.createFile(60000L, fileClient1);
            fxmlController.createFile(45000L, fileClient1);
            fxmlController.createFile(45000L, fileClient1);
            fxmlController.createFile(45000L, fileClient1);
            fxmlController.createFile(45000L, fileClient1);
            fxmlController.createFile(45000L, fileClient1);
        }
    }
}
