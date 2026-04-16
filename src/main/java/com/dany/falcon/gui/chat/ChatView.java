package com.dany.falcon.gui.chat;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ChatView {
    private VBox root;
    private VBox chatBox;
    private TextField input;
    private Button sendBtn;

    public ChatView() {
        chatBox = new VBox();
        input = new TextField();
        sendBtn = new Button("Enviar");

        root = new VBox(chatBox, input, sendBtn);
    }

    public VBox getRoot() {
        return root;
    }

    public Button getSendBtn() {
        return sendBtn;
    }

    public TextField getInput() {
        return input;
    }

    public void addMessage(String msg) {
        chatBox.getChildren().add(new Label(msg));
    }
}
