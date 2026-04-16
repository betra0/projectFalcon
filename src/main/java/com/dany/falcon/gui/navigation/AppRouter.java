package com.dany.falcon.gui.navigation;

import com.dany.falcon.chat.ChatService;
import com.dany.falcon.gui.chat.ChatController;
import com.dany.falcon.gui.chat.ChatView;
import com.dany.falcon.gui.layout.MainLayout;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppRouter {
    private MainLayout layout;

    public AppRouter(Stage stage) {
        layout = new MainLayout();
        Scene scene = new Scene(layout.getRoot(), 1000, 500);
        scene.getStylesheets().add(
                getClass().getResource("/styles/dark.css").toExternalForm()
        );
        stage.setScene(scene);

    }
    public void showHome(ChatService chat){

    }
    public void showChat(ChatService chat){
        ChatView view = new ChatView();
        ChatController controller = new ChatController(view, chat, this);

        layout.setContent(view.getRoot());



    }


}
