package Frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/Frontend/views/LoginPage.fxml"));
        primaryStage.setTitle("WhatsApp Clone");
        primaryStage.setScene(new Scene(root, 400, 500));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}