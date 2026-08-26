import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Stephen {
    private static final String SEPARATOR = "\n________________________________________________";
    private static final Path DATA_FILE = Path.of("data", "stephen.txt");

    /**
     * Runs the chatbot and stores tasks entered during the current session.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Task> tasks = new ArrayList<>();
        Storage storage = new Storage(DATA_FILE);

        System.out.println("Hello! I'm Stephen.");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);

        try {
            tasks.addAll(storage.load());
        } catch (IOException e) {
            System.out.println("Oops! I couldn't load your tasks. Starting with an empty list.");
            System.out.println(SEPARATOR);
        }

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();

            if (input.equals("bye")) {
                System.out.println(SEPARATOR);
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(SEPARATOR);
                break;
            }

            System.out.println(SEPARATOR);
            try {
                if (input.equals("list")) {
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i + 1) + "." + tasks.get(i));
                    }
                } else if (input.equals("schedule") || input.startsWith("schedule ")) {
                    LocalDate date = parseScheduleDate(input.substring(8).trim());
                    printSchedule(tasks, date);
                } else if (input.equals("mark") || input.startsWith("mark ")) {
                    int taskIndex = parseTaskIndex(input, "mark", tasks.size());
                    tasks.get(taskIndex).markAsDone();
                    saveTasks(storage, tasks);
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println(tasks.get(taskIndex));
                } else if (input.equals("unmark") || input.startsWith("unmark ")) {
                    int taskIndex = parseTaskIndex(input, "unmark", tasks.size());
                    tasks.get(taskIndex).markAsNotDone();
                    saveTasks(storage, tasks);
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println(tasks.get(taskIndex));
                } else if (input.equals("delete") || input.startsWith("delete ")) {
                    int taskIndex = parseTaskIndex(input, "delete", tasks.size());
                    Task deletedTask = tasks.remove(taskIndex);
                    saveTasks(storage, tasks);
                    System.out.println("Noted. I've removed this task:");
                    System.out.println("  " + deletedTask);
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                } else if (input.equals("todo") || input.startsWith("todo ")) {
                    String description = input.substring(4).trim();
                    if (description.isEmpty()) {
                        throw new ChatbotException("A todo needs a description. Try: todo borrow book");
                    }
                    Task todo = new Todo(description);
                    tasks.add(todo);
                    saveTasks(storage, tasks);
                    printAddedTask(todo, tasks.size());
                } else if (input.equals("deadline") || input.startsWith("deadline ")) {
                    Deadline deadline = parseDeadline(input.substring(8).trim());
                    tasks.add(deadline);
                    saveTasks(storage, tasks);
                    printAddedTask(deadline, tasks.size());
                } else if (input.equals("event") || input.startsWith("event ")) {
                    Event event = parseEvent(input.substring(5).trim());
                    tasks.add(event);
                    saveTasks(storage, tasks);
                    printAddedTask(event, tasks.size());
                } else {
                    throw new ChatbotException("I don't recognise that command.");
                }
            } catch (ChatbotException e) {
                System.out.println("Oops! " + e.getMessage());
            }
            System.out.println(SEPARATOR);
        }
    }

    /** Saves the task list and converts file-system failures into user-facing errors. */
    private static void saveTasks(Storage storage, List<Task> tasks) throws ChatbotException {
        try {
            storage.save(tasks);
        } catch (IOException e) {
            throw new ChatbotException("I couldn't save your tasks. Please check the data folder.");
        }
    }

    /** Parses and validates a one-based task number supplied to a task command. */
    private static int parseTaskIndex(String input, String command, int taskCount)
            throws ChatbotException {
        String numberText = input.substring(command.length()).trim();
        if (numberText.isEmpty()) {
            throw new ChatbotException("Please provide a task number. Try: " + command + " 2");
        }

        final int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            throw new ChatbotException("The task number must be a number. Try: " + command + " 2");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new ChatbotException("That task number does not exist.");
        }
        return taskNumber - 1;
    }

    /** Parses the date supplied to the schedule command. */
    private static LocalDate parseScheduleDate(String dateText) throws ChatbotException {
        if (dateText.isEmpty()) {
            throw new ChatbotException(
                    "Please provide a schedule date. Try: schedule 2019-12-02");
        }
        try {
            return LocalDate.parse(dateText, Task.INPUT_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new ChatbotException("Invalid schedule date. Please use yyyy-MM-dd, "
                    + "for example 2019-12-02.");
        }
    }

    /** Displays dated tasks occurring on the requested date. */
    private static void printSchedule(List<Task> tasks, LocalDate date) {
        boolean hasMatches = false;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).occursOn(date)) {
                if (!hasMatches) {
                    System.out.println("Here are the tasks occurring on "
                            + date.format(Task.DISPLAY_DATE_FORMAT) + ":");
                    hasMatches = true;
                }
                System.out.println((i + 1) + "." + tasks.get(i));
            }
        }
        if (!hasMatches) {
            System.out.println("There are no deadlines or events on "
                    + date.format(Task.DISPLAY_DATE_FORMAT) + ".");
        }
    }

    /** Parses a deadline while checking its description and {@code /by} value. */
    private static Deadline parseDeadline(String details) throws ChatbotException {
        if (details.isEmpty()) {
            throw new ChatbotException(
                    "A deadline needs a description and '/by'. "
                            + "Try: deadline return book /by 2019-12-02");
        }
        if (details.equals("/by") || details.startsWith("/by ")) {
            throw new ChatbotException("A deadline needs a description before '/by'.");
        }
        int byIndex = findMarker(details, "/by", 0);
        if (byIndex < 0) {
            throw new ChatbotException(
                    "A deadline needs '/by'. Try: deadline return book /by 2019-12-02");
        }
        String description = details.substring(0, byIndex).trim();
        String by = details.substring(byIndex + 4).trim();
        if (description.isEmpty()) {
            throw new ChatbotException("A deadline needs a description before '/by'.");
        }
        if (by.isEmpty()) {
            throw new ChatbotException("A deadline needs a date after '/by'. Try: /by 2019-12-02");
        }
        try {
            return new Deadline(description, LocalDate.parse(by, Task.INPUT_DATE_FORMAT));
        } catch (DateTimeParseException e) {
            throw new ChatbotException("Invalid deadline date. Please use yyyy-MM-dd, "
                    + "for example 2019-12-02.");
        }
    }

    /** Parses an event while checking its description, start, and end values. */
    private static Event parseEvent(String details) throws ChatbotException {
        if (details.isEmpty()) {
            throw new ChatbotException(
                    "An event needs a description, '/from', and '/to'. "
                            + "Try: event meeting /from 2019-12-02 /to 2019-12-03");
        }
        if (details.equals("/from") || details.startsWith("/from ")) {
            throw new ChatbotException("An event needs a description before '/from'.");
        }
        int fromIndex = findMarker(details, "/from", 0);
        if (fromIndex < 0) {
            throw new ChatbotException(
                    "An event needs '/from'. "
                            + "Try: event meeting /from 2019-12-02 /to 2019-12-03");
        }
        int toIndex = findMarker(details, "/to", fromIndex + 6);
        if (toIndex < 0) {
            throw new ChatbotException(
                    "An event needs '/to'. "
                            + "Try: event meeting /from 2019-12-02 /to 2019-12-03");
        }
        String description = details.substring(0, fromIndex).trim();
        String from = details.substring(fromIndex + 6, toIndex).trim();
        String to = details.substring(toIndex + 4).trim();
        if (description.isEmpty()) {
            throw new ChatbotException("An event needs a description before '/from'.");
        }
        if (from.isEmpty()) {
            throw new ChatbotException("An event needs a start date after '/from'.");
        }
        if (to.isEmpty()) {
            throw new ChatbotException("An event needs an end date after '/to'.");
        }
        try {
            return new Event(description,
                    LocalDate.parse(from, Task.INPUT_DATE_FORMAT),
                    LocalDate.parse(to, Task.INPUT_DATE_FORMAT));
        } catch (DateTimeParseException e) {
            throw new ChatbotException("Invalid event date. Please use yyyy-MM-dd for both dates, "
                    + "for example /from 2019-12-02 /to 2019-12-03.");
        }
    }

    /**
     * Finds a command marker that is either followed by whitespace or ends the input.
     */
    private static int findMarker(String details, String marker, int fromIndex) {
        String markerWithLeadingSpace = " " + marker;
        int index = details.indexOf(markerWithLeadingSpace, fromIndex);
        while (index >= 0) {
            int markerEnd = index + markerWithLeadingSpace.length();
            if (markerEnd == details.length() || Character.isWhitespace(details.charAt(markerEnd))) {
                return index;
            }
            index = details.indexOf(markerWithLeadingSpace, index + 1);
        }
        return -1;
    }

    /**
     * Prints the standard confirmation after adding a task.
     *
     * @param task task that was added
     * @param taskCount number of tasks currently stored
     */
    private static void printAddedTask(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }
}
