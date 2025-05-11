package Frontend;

import Backend.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.*;

public class RegistrationController {
    @FXML private TextField phoneField;
    @FXML private TextField nameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    @FXML
    private void handleRegister() {
        String phone = phoneField.getText().trim();
        String username = nameField.getText().trim();
        String password = passwordField.getText().trim();

        // Validation
        if (!phone.matches("^\\+?[0-9]{10,15}$")) {
            statusLabel.setText("Invalid phone format! Use +1234567890");
            return;
        }

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("All fields are required!");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Check for existing user
            String checkSql = "SELECT id FROM users WHERE phone = ? OR username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, phone);
            checkStmt.setString(2, username);

            if (checkStmt.executeQuery().next()) {
                statusLabel.setText("Phone/username already exists!");
                return;
            }

            // Register new user
            String insertSql = "INSERT INTO users (phone, username, password) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, phone);
                insertStmt.setString(2, username);
                insertStmt.setString(3, password);
                insertStmt.executeUpdate();

                // After successful registration, go directly to HomePage.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Frontend/HomePage.fxml"));
                Parent root = loader.load();

                HomeController controller = loader.getController();
                controller.initData(username, phone); // Send user info to Home

                Stage stage = (Stage) phoneField.getScene().getWindow();
                stage.setScene(new Scene(root, 800, 600));
                stage.setTitle("WhatsApp Clone - Home");
            }
        } catch (Exception e) {
            statusLabel.setText("Error: Please try again.");
            e.printStackTrace();
        }
    }

    @FXML
    private void goToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Frontend/LoginPage.fxml"));
            Stage stage = (Stage) phoneField.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 500));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
