import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

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
            String command = parser.getCommand(input);
            String arguments = parser.getArguments(input);

            if (command.equals("bye") && arguments.isEmpty()) {
                ui.showGoodbye();
                break;
            }

            ui.showDivider();
            try {
                if (command.equals("list") && arguments.isEmpty()) {
                    ui.showTaskList(tasks);
                } else if (command.equals("schedule")) {
                    LocalDate date = parser.parseScheduleDate(arguments);
                    ui.showSchedule(tasks, date);
                } else if (command.equals("mark")) {
                    int taskIndex = parser.parseTaskIndex(arguments, command, tasks.size());
                    Task markedTask = tasks.mark(taskIndex);
                    saveTasks(storage, tasks);
                    ui.showTaskMarked(markedTask, true);
                } else if (command.equals("unmark")) {
                    int taskIndex = parser.parseTaskIndex(arguments, command, tasks.size());
                    Task unmarkedTask = tasks.unmark(taskIndex);
                    saveTasks(storage, tasks);
                    ui.showTaskMarked(unmarkedTask, false);
                } else if (command.equals("delete")) {
                    int taskIndex = parser.parseTaskIndex(arguments, command, tasks.size());
                    Task deletedTask = tasks.delete(taskIndex);
                    saveTasks(storage, tasks);
                    ui.showTaskDeleted(deletedTask, tasks.size());
                } else if (command.equals("todo")) {
                    Task todo = parser.parseTodo(arguments);
                    tasks.add(todo);
                    saveTasks(storage, tasks);
                    ui.showTaskAdded(todo, tasks.size());
                } else if (command.equals("deadline")) {
                    Deadline deadline = parser.parseDeadline(arguments);
                    tasks.add(deadline);
                    saveTasks(storage, tasks);
                    ui.showTaskAdded(deadline, tasks.size());
                } else if (command.equals("event")) {
                    Event event = parser.parseEvent(arguments);
                    tasks.add(event);
                    saveTasks(storage, tasks);
                    ui.showTaskAdded(event, tasks.size());
                } else {
                    throw new ChatbotException("I don't recognise that command.");
                }
            } catch (ChatbotException e) {
                ui.showError(e.getMessage());
            }
            ui.showDivider();
        }
    }

    /** Saves the task list and converts file-system failures into user-facing errors. */
    private static void saveTasks(Storage storage, TaskList tasks) throws ChatbotException {
        try {
            storage.save(tasks.getTasks());
        } catch (IOException e) {
            throw new ChatbotException("I couldn't save your tasks. Please check the data folder.");
        }
    }

}
