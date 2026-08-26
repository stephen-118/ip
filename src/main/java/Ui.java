import java.time.LocalDate;
import java.util.Scanner;

/** Reads user input and displays all console output for Stephen. */
public class Ui {
    private static final String DIVIDER = "\n________________________________________________";
    private final Scanner scanner;

    /** Creates a console UI that reads from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Returns whether another line of user input is available. */
    public boolean hasNextInput() {
        return scanner.hasNextLine();
    }

    /** Reads and trims the next line of user input. */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Displays the startup greeting and its trailing divider. */
    public void showWelcome() {
        showLine("Hello! I'm Stephen.");
        showLine("What can I do for you?");
        showDivider();
    }

    /** Displays the farewell between divider lines. */
    public void showGoodbye() {
        showDivider();
        showLine("Bye. Hope to see you again soon!");
        showDivider();
    }

    /** Displays an error using the chatbot's standard prefix. */
    public void showError(String message) {
        showLine("Oops! " + message);
    }

    /** Displays all tasks with their one-based list numbers. */
    public void showTaskList(TaskList tasks) {
        showLine("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            showLine((i + 1) + "." + tasks.get(i));
        }
    }

    /** Displays the standard confirmation for a newly added task. */
    public void showTaskAdded(Task task, int taskCount) {
        showLine("Got it. I've added this task:");
        showLine("  " + task);
        showLine("Now you have " + taskCount + " tasks in the list.");
    }

    /** Displays the standard confirmation for a deleted task. */
    public void showTaskDeleted(Task task, int taskCount) {
        showLine("Noted. I've removed this task:");
        showLine("  " + task);
        showLine("Now you have " + taskCount + " tasks in the list.");
    }

    /** Displays a task whose completion status was changed. */
    public void showTaskMarked(Task task, boolean isDone) {
        if (isDone) {
            showLine("Nice! I've marked this task as done:");
        } else {
            showLine("OK, I've marked this task as not done yet:");
        }
        showLine(task.toString());
    }

    /** Displays dated tasks occurring on the requested date. */
    public void showSchedule(TaskList tasks, LocalDate date) {
        boolean hasMatches = false;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).occursOn(date)) {
                if (!hasMatches) {
                    showLine("Here are the tasks occurring on "
                            + date.format(Task.DISPLAY_DATE_FORMAT) + ":");
                    hasMatches = true;
                }
                showLine((i + 1) + "." + tasks.get(i));
            }
        }
        if (!hasMatches) {
            showLine("There are no deadlines or events on "
                    + date.format(Task.DISPLAY_DATE_FORMAT) + ".");
        }
    }

    /** Displays one line of normal output. */
    public void showLine(String message) {
        System.out.println(message);
    }

    /** Displays the standard divider, including its leading blank line. */
    public void showDivider() {
        showLine(DIVIDER);
    }
}
