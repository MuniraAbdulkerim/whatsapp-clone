package Frontend;

import java.io.IOException;
import java.sql.*;
import Backend.DBConnection;
import Backend.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        try (Connection conn = DBConnection.getConnection()) {
            // Get both username and phone if login succeeds
            String sql = "SELECT username, phone FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Successful login - load home page
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Frontend/HomePage.fxml"));
                Parent root = loader.load();
                
                HomeController controller = loader.getController();
                controller.initData(rs.getString("username"), rs.getString("phone"));
                
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root, 800, 600));
                stage.setTitle("WhatsApp Clone - Home");
            } else {
                statusLabel.setText("Invalid username or password");
            }
        } catch (SQLException | IOException e) {
            statusLabel.setText("Login error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goToRegister() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Frontend/Registration.fxml"));
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(new Scene(root, 400, 500));
    }
}