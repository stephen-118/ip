import java.time.LocalDate;

/** Displays tasks occurring on one date. */
public class ScheduleCommand extends Command {
    private final LocalDate date;

    /** Creates a command that displays the schedule for the given date. */
    public ScheduleCommand(LocalDate date) {
        this.date = date;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showSchedule(tasks, date);
    }
}
