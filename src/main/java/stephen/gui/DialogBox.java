package stephen.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/** Displays one wrapped chat message with speaker-specific alignment and styling. */
public class DialogBox extends HBox {
    private static final double MAX_BUBBLE_WIDTH = 430;

    private DialogBox(String text, boolean isUser) {
        Label message = new Label(text);
        message.setWrapText(true);
        message.setMaxWidth(MAX_BUBBLE_WIDTH);
        message.setMinHeight(Region.USE_PREF_SIZE);
        message.getStyleClass().add(isUser ? "user-bubble" : "stephen-bubble");

        setFillHeight(true);
        setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        getStyleClass().add("dialog-row");
        getChildren().add(message);
        HBox.setHgrow(message, Priority.SOMETIMES);
    }

    /** Returns a right-aligned dialog for a user command. */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, true);
    }

    /** Returns a left-aligned dialog for Stephen's response. */
    public static DialogBox getStephenDialog(String text) {
        return new DialogBox(text, false);
    }
}
