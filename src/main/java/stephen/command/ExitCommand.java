package stephen.command;

import stephen.storage.Storage;
import stephen.task.TaskList;
import stephen.ui.Ui;

/** Displays the farewell and signals that the application should stop. */
public class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
