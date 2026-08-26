/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private final String by;

    /**
     * Creates an incomplete deadline.
     *
     * @param description description of the task
     * @param by deadline text, stored without date parsing
     */
    public Deadline(String description, String by) {
        super(TaskType.DEADLINE, description);
        this.by = by;
    }

    @Override
    public String toDataString() {
        return super.toDataString() + " | " + escape(by);
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by + ")";
    }
}
