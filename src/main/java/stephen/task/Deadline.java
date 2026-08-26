package stephen.task;

import java.time.LocalDate;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private final LocalDate by;

    /**
     * Creates an incomplete deadline.
     *
     * @param description description of the task
     * @param by deadline date
     */
    public Deadline(String description, LocalDate by) {
        super(TaskType.DEADLINE, description);
        this.by = by;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return by.equals(date);
    }

    @Override
    public String toDataString() {
        return super.toDataString() + " | " + by.format(INPUT_DATE_FORMAT);
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
