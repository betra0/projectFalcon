package com.dany.falcon.gui.chat;

import com.dany.falcon.chat.ChatService;
import com.dany.falcon.gui.navigation.AppRouter;
import javafx.application.Platform;

public class ChatController {

    private ChatView view;
    private ChatService chatService;
    private AppRouter router;

    public ChatController(ChatView view, ChatService chatService, AppRouter router) {
        this.view = view;
        this.chatService = chatService;

        init();
    }

    private void init() {
        view.getSendBtn().setOnAction(e -> handleSend());
    }

    private void handleSend() {
        String texto = view.getInput().getText();

        view.addMessage("Tú: " + texto);

        new Thread(() -> {
            String res = chatService.sendMessage(texto);

            Platform.runLater(() -> {
                view.addMessage("IA: " + res);
            });
        }).start();

        view.getInput().clear();
    }
}
