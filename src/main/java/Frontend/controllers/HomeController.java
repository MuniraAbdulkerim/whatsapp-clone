package Frontend.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HomeController {

    @FXML private ListView<String> contactsList;
    @FXML private VBox messagesBox;
    @FXML private Label chatHeaderLabel;
    @FXML private TextField messageField;
    @FXML private ImageView profilePic;
    @FXML private Label sidebarUserLabel;
    @FXML private ImageView chatAvatar;
    @FXML private TextField searchContactsField;

    private String currentUser;
    private String currentPhone;
    private String selectedContact;

    // Initialize user data
    public void initData(String username, String phone) {
        this.currentUser = username;
        this.currentPhone = phone;
        sidebarUserLabel.setText(username);
        setProfileImage(profilePic, "default_profile.png"); // Placeholder
        loadContacts();
        setupContactSelection();
        setupSearchFilter();
    }

    // Load contacts from backend
    private void loadContacts() {
        contactsList.getItems().clear();
        try {
            URL url = new URL("http://localhost:8080/api/auth/users");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            int code = conn.getResponseCode();
            if (code == 200) {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);
                }
                JSONArray users = new JSONArray(sb.toString());
                for (int i = 0; i < users.length(); i++) {
                    JSONObject user = users.getJSONObject(i);
                    String username = user.getString("username");
                    String phone = user.getString("phone");
                    if (!username.equals(currentUser)) {
                        contactsList.getItems().add(username + " (" + phone + ")");
                    }
                }
            }
        } catch (Exception e) {
            showAlert("Connection Error", "Could not load contacts from server");
        }
    }

    // Set up contact selection handler
    private void setupContactSelection() {
        contactsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedContact = newVal.split(" \\(")[0]; // Extract username
                chatHeaderLabel.setText(selectedContact);
                setProfileImage(chatAvatar, "default_profile.png"); // Placeholder
                loadChatHistory();
            }
        });
    }

    private void setupSearchFilter() {
        searchContactsField.textProperty().addListener((obs, oldVal, newVal) -> {
            // Simple filter
            loadContacts();
            if (!newVal.isEmpty()) {
                contactsList.getItems().removeIf(item -> !item.toLowerCase().contains(newVal.toLowerCase()));
            }
        });
    }

    // Load chat history from backend
    private void loadChatHistory() {
        messagesBox.getChildren().clear();
        if (selectedContact == null) return;
        try {
            String user1 = currentUser;
            String user2 = selectedContact;
            String urlStr = String.format("http://localhost:8080/api/chat/history?user1=%s&user2=%s",
                    URLEncoder.encode(user1, "UTF-8"), URLEncoder.encode(user2, "UTF-8"));
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            int code = conn.getResponseCode();
            if (code == 200) {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);
                }
                JSONArray messages = new JSONArray(sb.toString());
                for (int i = 0; i < messages.length(); i++) {
                    JSONObject message = messages.getJSONObject(i);
                    String sender = message.getString("sender");
                    String content = message.getString("content");
                    String timestamp = message.getString("timestamp");
                    String displayTime = "";
                    try {
                        LocalDateTime dt = LocalDateTime.parse(timestamp);
                        displayTime = dt.format(DateTimeFormatter.ofPattern("HH:mm"));
                    } catch (Exception e) {
                        displayTime = timestamp;
                    }
                    addMessageBubble(sender.equals(currentUser), sender, content, displayTime);
                }
            }
        } catch (Exception e) {
            showAlert("Connection Error", "Could not load chat history");
        }
    }

    // Add a message bubble to the chat area
    private void addMessageBubble(boolean isSelf, String sender, String content, String time) {
        HBox bubble = new HBox();
        bubble.setAlignment(isSelf ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        bubble.setSpacing(8);

        Text text = new Text(content + "\n" + time);
        text.setWrappingWidth(320);

        Label messageLabel = new Label();
        messageLabel.setGraphic(text);
        messageLabel.setStyle("-fx-padding: 10 16; -fx-background-radius: 12; -fx-font-size: 14;" +
                (isSelf ? "-fx-background-color: #DCF8C6;" : "-fx-background-color: #fff;") +
                "-fx-border-color: #ece5dd; -fx-border-width: 1;");
        messageLabel.setMaxWidth(340);
        bubble.getChildren().add(messageLabel);

        messagesBox.getChildren().add(bubble);
    }

    // Handle sending messages to backend
    @FXML
    private void handleSendMessage() {
        if (selectedContact == null) {
            showAlert("No Contact Selected", "Please select a contact to message");
            return;
        }
        String messageText = messageField.getText().trim();
        if (messageText.isEmpty()) return;
        try {
            URL url = new URL("http://localhost:8080/api/chat/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            JSONObject json = new JSONObject();
            json.put("sender", currentUser);
            json.put("receiver", selectedContact);
            json.put("content", messageText);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.toString().getBytes());
            }
            int code = conn.getResponseCode();
            if (code == 200) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                addMessageBubble(true, currentUser, messageText, timestamp);
                messageField.clear();
            } else {
                showAlert("Error", "Could not send message");
            }
        } catch (Exception e) {
            showAlert("Error", "Could not send message");
        }
    }

    // Handle logout (UI navigation)
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Frontend/views/LoginPage.fxml"));
            Stage stage = (Stage) sidebarUserLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 400, 500));
        } catch (Exception e) {
            showAlert("Logout Error", "Could not logout properly");
            e.printStackTrace();
        }
    }

    // Utility: set profile images
    private void setProfileImage(ImageView view, String resourceName) {
        try {
            Image img = new Image(getClass().getResourceAsStream("/Frontend/assets/" + resourceName));
            view.setImage(img);
            Rectangle clip = new Rectangle(view.getFitWidth(), view.getFitHeight());
            clip.setArcWidth(view.getFitWidth());
            clip.setArcHeight(view.getFitHeight());
            view.setClip(clip);
        } catch (Exception e) {
            // Ignore if image not found
        }
    }

    // Utility method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}