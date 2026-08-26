package stephen.task;

import java.time.LocalDate;

/**
 * Represents a task that occurs between specified start and end times.
 */
public class Event extends Task {
    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an incomplete event.
     *
     * @param description description of the event
     * @param from start date
     * @param to end date
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(TaskType.EVENT, description);
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from) && !date.isAfter(to);
    }

    @Override
    public String toDataString() {
        return super.toDataString() + " | " + from.format(INPUT_DATE_FORMAT)
                + " | " + to.format(INPUT_DATE_FORMAT);
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from.format(DISPLAY_DATE_FORMAT)
                + " to: " + to.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
