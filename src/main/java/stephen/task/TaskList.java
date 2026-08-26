package stephen.task;

import java.util.ArrayList;
import java.util.List;

/** Stores tasks and provides the operations that update or retrieve them. */
public class TaskList {
    private final List<Task> tasks;

    /** Creates a task list containing the tasks loaded from storage. */
    public TaskList(List<Task> initialTasks) {
        tasks = new ArrayList<>(initialTasks);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /** Removes and returns the task at the given zero-based index. */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /** Returns the task at the given zero-based index. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Marks the task at the given zero-based index as done. */
    public Task mark(int index) {
        Task task = get(index);
        task.markAsDone();
        return task;
    }

    /** Marks the task at the given zero-based index as not done. */
    public Task unmark(int index) {
        Task task = get(index);
        task.markAsNotDone();
        return task;
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Returns a read-only snapshot suitable for display or persistence. */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }
}
