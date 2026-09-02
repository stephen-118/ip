package stephen;

import javafx.application.Application;

/** Launches the JavaFX application without extending {@link Application}. */
public class Launcher {
    /**
     * Starts Stephen's graphical interface.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String... args) {
        Application.launch(Main.class, args);
    }
}
