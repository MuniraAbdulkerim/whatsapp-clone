package Frontend;

import Backend.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HomeController {

    @FXML private Label welcomeLabel;
    @FXML private ListView<String> contactsList;

    private String currentUser;
    private String currentPhone;

    public void initData(String username, String phone) {
        this.currentUser = username;
        this.currentPhone = phone;
        welcomeLabel.setText("Welcome, " + username + " (" + phone + ")");
        loadContactsFromDB();
    }

    private void loadContactsFromDB() {
        ObservableList<String> contacts = FXCollections.observableArrayList();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT username, phone FROM users WHERE username != ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, currentUser);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                contacts.add(rs.getString("username") + " (" + rs.getString("phone") + ")");
            }
            contactsList.setItems(contacts);
        } catch (SQLException e) {
            contactsList.getItems().addAll(
                    "Example Contact (+1234567890)",
                    "Test User (+9876543210)");
            e.printStackTrace();
        }

        // Set up custom cell rendering here
        contactsList.setCellFactory(lv -> new ListCell<String>() {
            private final HBox content;
            private final ImageView imageView;
            private final Label label;

            {
                imageView = new ImageView(new Image(getClass().getResource("/assets/default-profile.png").toString()));
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);
                label = new Label();
                content = new HBox(10, imageView, label);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    label.setText(item);
                    setGraphic(content);
                }
            }
        });
    }

    @FXML
    private void handleLogout() {
        try {
            // Update last seen
            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE users SET last_seen = NOW() WHERE username = ?");
                stmt.setString(1, currentUser);
                stmt.executeUpdate();
            }

            // Return to login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Frontend/LoginPage.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 400, 500));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
