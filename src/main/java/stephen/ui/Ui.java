package stephen.ui;

import java.time.LocalDate;
import java.util.Scanner;

import stephen.task.Task;
import stephen.task.TaskList;

/** Reads user input and displays all console output for Stephen. */
public class Ui {
    private static final String DIVIDER = "\n________________________________________________";
    private final Scanner scanner;

    /** Creates a console UI that reads from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Returns whether another line of user input is available.
     *
     * @return {@code true} if another input line can be read
     */
    public boolean hasNextInput() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next line of user input.
     *
     * @return next command without surrounding whitespace
     */
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

    /**
     * Displays an error using the chatbot's standard prefix.
     *
     * @param message error message without the standard prefix
     */
    public void showError(String message) {
        showLine("Oops! " + message);
    }

    /**
     * Displays all tasks with their one-based list numbers.
     *
     * @param tasks task list to display
     */
    public void showTaskList(TaskList tasks) {
        showLine("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            showLine((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays the standard confirmation for a newly added task.
     *
     * @param task task that was added
     * @param taskCount number of tasks after the addition
     */
    public void showTaskAdded(Task task, int taskCount) {
        showLine("Got it. I've added this task:");
        showLine("  " + task);
        showLine("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays the standard confirmation for a deleted task.
     *
     * @param task task that was deleted
     * @param taskCount number of tasks after the deletion
     */
    public void showTaskDeleted(Task task, int taskCount) {
        showLine("Noted. I've removed this task:");
        showLine("  " + task);
        showLine("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays a task whose completion status was changed.
     *
     * @param task task whose status changed
     * @param isDone {@code true} if the task was marked done
     */
    public void showTaskMarked(Task task, boolean isDone) {
        if (isDone) {
            showLine("Nice! I've marked this task as done:");
        } else {
            showLine("OK, I've marked this task as not done yet:");
        }
        showLine(task.toString());
    }

    /**
     * Displays dated tasks occurring on the requested date.
     *
     * @param tasks task list to search
     * @param date date whose tasks should be displayed
     */
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

    /**
     * Displays one line of normal output.
     *
     * @param message text to display
     */
    public void showLine(String message) {
        System.out.println(message);
    }

    /** Displays the standard divider, including its leading blank line. */
    public void showDivider() {
        showLine(DIVIDER);
    }
}
