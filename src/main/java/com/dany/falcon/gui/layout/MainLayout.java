package com.dany.falcon.gui.layout;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainLayout {
    private BorderPane root;
    private VBox header;
    private StackPane content;

    public MainLayout() {
        root = new BorderPane();

        header = new VBox();
        header.getChildren().add(new Label("Falcon AI"));

        content = new StackPane();

        root.setTop(header);
        root.setCenter(content);
    }

    public void setContent(Parent view) {
        content.getChildren().clear();
        content.getChildren().add(view);
    }

    public Parent getRoot() {
        return root;
    }
}
