package stephen.command;

import stephen.exception.ChatbotException;
import stephen.storage.Storage;
import stephen.task.Task;
import stephen.task.TaskList;
import stephen.ui.Ui;

/** Marks one task as not done. */
public class UnmarkCommand extends Command {
    private final int taskIndex;

    /** Creates a command that unmarks the task at the given zero-based index. */
    public UnmarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChatbotException {
        Task unmarkedTask = tasks.unmark(taskIndex);
        saveTasks(tasks, storage);
        ui.showTaskMarked(unmarkedTask, false);
    }
}
