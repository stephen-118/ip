package stephen.command;

import stephen.storage.Storage;
import stephen.task.TaskList;
import stephen.ui.Ui;

/** Displays every task in the task list. */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
