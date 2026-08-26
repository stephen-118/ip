package stephen.command;

import stephen.exception.ChatbotException;
import stephen.storage.Storage;
import stephen.task.Task;
import stephen.task.TaskList;
import stephen.ui.Ui;

/** Marks one task as done. */
public class MarkCommand extends Command {
    private final int taskIndex;

    /** Creates a command that marks the task at the given zero-based index. */
    public MarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChatbotException {
        Task markedTask = tasks.mark(taskIndex);
        saveTasks(tasks, storage);
        ui.showTaskMarked(markedTask, true);
    }
}
