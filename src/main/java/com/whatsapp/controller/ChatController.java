package com.whatsapp.controller;

import com.whatsapp.model.Message;
import com.whatsapp.model.User;
import com.whatsapp.service.MessageService;
import com.whatsapp.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ChatController {

    @FXML private Label contactNameLabel;
    @FXML private TextField searchField;
    @FXML private ListView<User> contactsList;
    @FXML private VBox messagesContainer;
    @FXML private TextArea messageInput;
    @FXML private Button sendButton;
    @FXML private Button backButton;

    @Autowired private MessageService messageService;
    @Autowired private UserService userService;

    private User currentUser;
    private User selectedContact;
    private StompSession stompSession;

    @FXML
    public void initialize() {
        setupWebSocket();
        loadContacts();
    }

    private void setupWebSocket() {
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        try {
            stompSession = stompClient.connect("ws://localhost:8080/ws", new StompSessionHandlerAdapter() {
                @Override
                public void handleException(StompSession session, StompCommand command, 
                                          StompHeaders headers, byte[] payload, Throwable exception) {
                    System.err.println("WebSocket error: " + exception.getMessage());
                }
            }).get();

            // Subscribe to messages
            StompHeaders headers = new StompHeaders();
            headers.setDestination("/topic/public");
            
            stompSession.subscribe(headers, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return Message.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    Message receivedMessage = (Message) payload;
                    if (receivedMessage.getReceiver().getId().equals(currentUser.getId())) {
                        javafx.application.Platform.runLater(() -> displayMessage(receivedMessage));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadContacts() {
        // Load contacts from service
        List<User> contacts = userService.getContacts(currentUser.getId());
        contactsList.getItems().setAll(contacts);
    }

    @FXML
    private void handleContactSelect() {
        selectedContact = contactsList.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            contactNameLabel.setText(selectedContact.getFullName());
            loadMessages();
        }
    }

    private void loadMessages() {
        messagesContainer.getChildren().clear();
        List<Message> messages = messageService.getMessagesBetweenUsers(
            currentUser.getId(), selectedContact.getId());
        
        for (Message message : messages) {
            displayMessage(message);
        }
    }

    private void displayMessage(Message message) {
        HBox messageBox = new HBox(10);
        messageBox.setStyle("-fx-padding: 5;");
        
        Label messageLabel = new Label(message.getContent());
        messageLabel.setWrapText(true);
        
        if (message.getSender().getId().equals(currentUser.getId())) {
            messageBox.setAlignment(Pos.CENTER_RIGHT);
            messageLabel.setStyle("-fx-background-color: #DCF8C6; -fx-padding: 10; -fx-background-radius: 10;");
        } else {
            messageBox.setAlignment(Pos.CENTER_LEFT);
            messageLabel.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 10;");
        }
        
        messageBox.getChildren().add(messageLabel);
        messagesContainer.getChildren().add(messageBox);
    }

    @FXML
    private void handleSendMessage() {
        if (selectedContact != null && !messageInput.getText().trim().isEmpty()) {
            Message message = new Message();
            message.setSender(currentUser);
            message.setReceiver(selectedContact);
            message.setContent(messageInput.getText().trim());
            
            // Send through WebSocket
            stompSession.send("/app/chat.sendMessage", message);
            
            messageInput.clear();
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        List<User> filteredContacts = userService.searchContacts(currentUser.getId(), searchText);
        contactsList.getItems().setAll(filteredContacts);
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
} 