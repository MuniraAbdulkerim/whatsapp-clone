package Frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.net.URL;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        try {
            // Load registration page with safety checks
            URL fxmlUrl = getClass().getResource("/Frontend/Registration.fxml");
            if (fxmlUrl == null) {
                throw new RuntimeException("FXML file not found at /Frontend/Registration.fxml");
            }
            
            loadRegistrationPage();
            testDBConnection();
            
        } catch (Exception e) {
            showErrorAlert("Startup Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Updated with null checks
    public static void loadRegistrationPage() throws Exception {
        URL fxmlUrl = MainApp.class.getResource("/Frontend/Registration.fxml");
        URL cssUrl = MainApp.class.getResource("/Frontend/whatsapp-style.css");
        
        if (fxmlUrl == null) throw new RuntimeException("Registration.fxml not found");
        
        Parent root = FXMLLoader.load(fxmlUrl);
        Scene scene = new Scene(root, 400, 500);
        
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
        
        primaryStage.setTitle("WhatsApp Clone - Register");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }

    // Updated with null checks
    public static void loadLoginPage() throws Exception {
        URL fxmlUrl = MainApp.class.getResource("/Frontend/LoginPage.fxml");
        URL cssUrl = MainApp.class.getResource("/Frontend/whatsapp-style.css");
        
        if (fxmlUrl == null) throw new RuntimeException("LoginPage.fxml not found");
        
        Parent root = FXMLLoader.load(fxmlUrl);
        Scene scene = new Scene(root, 400, 500);
        
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
        
        primaryStage.setTitle("WhatsApp Clone - Login");
        primaryStage.setScene(scene);
    }

    private void testDBConnection() {
        try {
            if (Backend.DBConnection.getConnection() == null) {
                showErrorAlert("Database connection failed. Check:\n1. MySQL is running\n2. DB credentials are correct");
            }
        } catch (Exception e) {
            showErrorAlert("Database error: " + e.getMessage());
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}