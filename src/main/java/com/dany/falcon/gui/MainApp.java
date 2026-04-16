package com.dany.falcon.gui;

import com.dany.falcon.chat.ChatService;
import com.dany.falcon.gui.chat.ChatController;
import com.dany.falcon.gui.chat.ChatView;
import com.dany.falcon.gui.navigation.AppRouter;
import com.dany.falcon.ia.AIProvider;
import com.dany.falcon.ia.AIService;
import com.dany.falcon.ia.AIServiceFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    private ChatService chat;

    @Override
    public void start(Stage stage) {
        this.initStart();
        this.initUI(stage);
    }

    private void initStart(){
        AIService ai = AIServiceFactory.create(AIProvider.GEMINI);
        this.chat = new ChatService(ai);
    }

    private void initUI(Stage stage) {

        AppRouter router = new AppRouter(stage);
        router.showChat(chat);
        stage.show();

    }


}