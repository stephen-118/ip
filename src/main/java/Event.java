/**
 * Represents a task that occurs between specified start and end times.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an incomplete event.
     *
     * @param description description of the event
     * @param from start text, stored without date parsing
     * @param to end text, stored without date parsing
     */
    public Event(String description, String from, String to) {
        super(TaskType.EVENT, description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
