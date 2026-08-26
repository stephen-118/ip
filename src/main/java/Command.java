import java.io.IOException;

/** Represents one parsed user command that can be executed by the application. */
public abstract class Command {
    /** Executes this command using the application's components. */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws ChatbotException;

    /** Returns whether this command should end the application. */
    public boolean isExit() {
        return false;
    }

    /** Saves the current tasks while converting file failures into application errors. */
    protected void saveTasks(TaskList tasks, Storage storage) throws ChatbotException {
        try {
            storage.save(tasks.getTasks());
        } catch (IOException e) {
            throw new ChatbotException("I couldn't save your tasks. Please check the data folder.");
        }
    }
}
