/** Deletes one task from the task list. */
public class DeleteCommand extends Command {
    private final int taskIndex;

    /** Creates a command that deletes the task at the given zero-based index. */
    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChatbotException {
        Task deletedTask = tasks.delete(taskIndex);
        saveTasks(tasks, storage);
        ui.showTaskDeleted(deletedTask, tasks.size());
    }
}
