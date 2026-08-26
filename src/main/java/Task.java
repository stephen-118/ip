/**
 * Represents a task with a description and completion status.
 */
public class Task {
    protected final TaskType type;
    protected String description;
    protected boolean isDone;

    /**
     * Creates a task that is initially not done.
     *
     * @param type fixed type of the task
     * @param description description of the task
     */
    public Task(TaskType type, String description) {
        this.type = type;
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon used to display the task's completion status.
     *
     * @return {@code X} if the task is done, or a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns this task in the pipe-delimited format used in the save file.
     * Backslashes, pipes, and line breaks are escaped so each task stays on one line.
     *
     * @return serialized task data
     */
    public String toDataString() {
        return type.getSymbol() + " | " + (isDone ? "1" : "0") + " | "
                + escape(description);
    }

    /** Escapes characters that have structural meaning in the save file. */
    protected static String escape(String value) {
        return value.replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    /**
     * Returns the task in the format used by the chatbot.
     *
     * @return status icon followed by the task description
     */
    @Override
    public String toString() {
        return "[" + type.getSymbol() + "][" + getStatusIcon() + "] " + description;
    }
}
