package stephen.task;

import java.util.ArrayList;
import java.util.List;

/** Stores tasks and provides the operations that update or retrieve them. */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates a task list containing the tasks loaded from storage.
     *
     * @param initialTasks tasks with which to initialize the list
     */
    public TaskList(List<Task> initialTasks) {
        tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param index zero-based index of the task to remove
     * @return removed task
     * @throws IndexOutOfBoundsException if the index is outside the list
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index zero-based index of the task to retrieve
     * @return task at the index
     * @throws IndexOutOfBoundsException if the index is outside the list
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Marks the task at the given zero-based index as done.
     *
     * @param index zero-based index of the task to mark
     * @return marked task
     * @throws IndexOutOfBoundsException if the index is outside the list
     */
    public Task mark(int index) {
        Task task = get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task at the given zero-based index as not done.
     *
     * @param index zero-based index of the task to unmark
     * @return unmarked task
     * @throws IndexOutOfBoundsException if the index is outside the list
     */
    public Task unmark(int index) {
        Task task = get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a read-only snapshot suitable for display or persistence.
     *
     * @return immutable copy of the tasks in list order
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }
}
