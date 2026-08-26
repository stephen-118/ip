package stephen.command;

import stephen.exception.ChatbotException;
import stephen.storage.Storage;
import stephen.task.Task;
import stephen.task.TaskList;
import stephen.ui.Ui;

/** Adds one previously parsed task to the task list. */
public class AddCommand extends Command {
    private final Task task;

    /** Creates a command that adds the given task. */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChatbotException {
        tasks.add(task);
        saveTasks(tasks, storage);
        ui.showTaskAdded(task, tasks.size());
    }
}
