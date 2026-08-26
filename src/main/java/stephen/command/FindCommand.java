package stephen.command;

import java.util.List;

import stephen.storage.Storage;
import stephen.task.Task;
import stephen.task.TaskList;
import stephen.ui.Ui;

/** Finds tasks whose descriptions contain a search phrase. */
public class FindCommand extends Command {
    private final String keyword;

    /** Creates a command that searches task descriptions for the given keyword. */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> matches = tasks.find(keyword);
        ui.showFindResults(matches, keyword);
    }
}
