package stephen;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import stephen.command.Command;
import stephen.exception.ChatbotException;
import stephen.parser.Parser;
import stephen.storage.Storage;
import stephen.task.Task;
import stephen.task.TaskList;
import stephen.ui.Ui;

public class Stephen {
    private static final Path DATA_FILE = Path.of("data", "stephen.txt");

    /**
     * Runs the chatbot and stores tasks entered during the current session.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage(DATA_FILE);
        Parser parser = new Parser();

        ui.showWelcome();

        List<Task> loadedTasks = List.of();
        try {
            loadedTasks = storage.load();
        } catch (IOException e) {
            ui.showError("I couldn't load your tasks. Starting with an empty list.");
            ui.showDivider();
        }
        TaskList tasks = new TaskList(loadedTasks);

        while (ui.hasNextInput()) {
            String input = ui.readCommand();
            Command command;
            try {
                command = parser.parse(input, tasks);
            } catch (ChatbotException e) {
                ui.showDivider();
                ui.showError(e.getMessage());
                ui.showDivider();
                continue;
            }

            if (!command.isExit()) {
                ui.showDivider();
            }
            try {
                command.execute(tasks, ui, storage);
            } catch (ChatbotException e) {
                ui.showError(e.getMessage());
            }
            if (command.isExit()) {
                break;
            }
            ui.showDivider();
        }
    }
}
