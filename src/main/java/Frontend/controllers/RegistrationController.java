package Frontend.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.http.*;
import java.net.URI;

public class RegistrationController {
    @FXML private TextField phoneField;
    @FXML private TextField nameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    private void handleRegister() {
        String phone = phoneField.getText().trim();
        String username = nameField.getText().trim();
        String password = passwordField.getText().trim();

        if (phone.isEmpty() || username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("All fields required");
            return;
        }

        String json = String.format("{\"phone\":\"%s\",\"username\":\"%s\",\"password\":\"%s\"}", phone, username, password);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/api/auth/register"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                // Registration successful - go to home
                loadHomeScreen(username);
            } else {
                statusLabel.setText("Registration failed: " + response.body());
            }
        } catch (Exception e) {
            statusLabel.setText("Registration error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Frontend/views/LoginPage.fxml"));
            Stage stage = (Stage) phoneField.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 500));
        } catch (Exception e) {
            statusLabel.setText("Error: Cannot load login screen");
            e.printStackTrace();
        }
    }

    private void loadHomeScreen(String username) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Frontend/views/HomePage.fxml"));
            Stage stage = (Stage) phoneField.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("WhatsApp Clone - Home");
        } catch (Exception e) {
            statusLabel.setText("Error: Cannot load home screen");
            e.printStackTrace();
        }
    }
}