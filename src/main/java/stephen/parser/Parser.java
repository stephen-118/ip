package stephen.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import stephen.command.AddCommand;
import stephen.command.Command;
import stephen.command.DeleteCommand;
import stephen.command.ExitCommand;
import stephen.command.FindCommand;
import stephen.command.ListCommand;
import stephen.command.MarkCommand;
import stephen.command.ScheduleCommand;
import stephen.command.UnmarkCommand;
import stephen.exception.ChatbotException;
import stephen.task.Deadline;
import stephen.task.Event;
import stephen.task.Task;
import stephen.task.TaskList;
import stephen.task.Todo;

/** Interprets user input and validates command arguments. */
public class Parser {
    /** Creates a parser for Stephen's supported commands. */
    public Parser() {
    }

    /**
     * Interprets one input line and creates the command that should handle it.
     *
     * @param input complete trimmed user input
     * @param tasks current task list, used to validate task numbers
     * @return command representing the input
     * @throws ChatbotException if the command or its arguments are invalid
     */
    public Command parse(String input, TaskList tasks) throws ChatbotException {
        String command = getCommand(input);
        String arguments = getArguments(input);

        switch (command) {
            case "bye":
                if (arguments.isEmpty()) {
                    return new ExitCommand();
                }
                break;
            case "list":
                if (arguments.isEmpty()) {
                    return new ListCommand();
                }
                break;
            case "schedule":
                return new ScheduleCommand(parseScheduleDate(arguments));
            case "find":
                return new FindCommand(parseFindKeyword(arguments));
            case "mark":
                return new MarkCommand(parseTaskIndex(arguments, command, tasks.size()));
            case "unmark":
                return new UnmarkCommand(parseTaskIndex(arguments, command, tasks.size()));
            case "delete":
                return new DeleteCommand(parseTaskIndex(arguments, command, tasks.size()));
            case "todo":
                return new AddCommand(parseTodo(arguments));
            case "deadline":
                return new AddCommand(parseDeadline(arguments));
            case "event":
                return new AddCommand(parseEvent(arguments));
            default:
                break;
        }
        throw new ChatbotException("I don't recognise that command.");
    }

    /**
     * Returns the first word of the input as the command name.
     *
     * @param input complete user input
     * @return command name, or an empty string when the input is empty
     */
    public String getCommand(String input) {
        int firstSpace = input.indexOf(' ');
        return firstSpace < 0 ? input : input.substring(0, firstSpace);
    }

    /**
     * Returns the text following the command name, with surrounding whitespace removed.
     *
     * @param input complete user input
     * @return trimmed command arguments, or an empty string when none are present
     */
    public String getArguments(String input) {
        int firstSpace = input.indexOf(' ');
        return firstSpace < 0 ? "" : input.substring(firstSpace + 1).trim();
    }

    /**
     * Parses a todo description.
     *
     * @param description todo description
     * @return todo containing the supplied description
     * @throws ChatbotException if the description is empty
     */
    public Todo parseTodo(String description) throws ChatbotException {
        if (description.isEmpty()) {
            throw new ChatbotException("A todo needs a description. Try: todo borrow book");
        }
        return new Todo(description);
    }

    /**
     * Parses a deadline while checking its description and {@code /by} value.
     *
     * @param details deadline description and date arguments
     * @return deadline represented by the arguments
     * @throws ChatbotException if required arguments are missing or the date is invalid
     */
    public Deadline parseDeadline(String details) throws ChatbotException {
        if (details.isEmpty()) {
            throw new ChatbotException(
                    "A deadline needs a description and '/by'. "
                            + "Try: deadline return book /by 2019-12-02");
        }
        if (details.equals("/by") || details.startsWith("/by ")) {
            throw new ChatbotException("A deadline needs a description before '/by'.");
        }
        int byIndex = findMarker(details, "/by", 0);
        if (byIndex < 0) {
            throw new ChatbotException(
                    "A deadline needs '/by'. Try: deadline return book /by 2019-12-02");
        }
        String description = details.substring(0, byIndex).trim();
        String by = details.substring(byIndex + 4).trim();
        if (description.isEmpty()) {
            throw new ChatbotException("A deadline needs a description before '/by'.");
        }
        if (by.isEmpty()) {
            throw new ChatbotException("A deadline needs a date after '/by'. Try: /by 2019-12-02");
        }
        try {
            return new Deadline(description, LocalDate.parse(by, Task.INPUT_DATE_FORMAT));
        } catch (DateTimeParseException e) {
            throw new ChatbotException("Invalid deadline date. Please use yyyy-MM-dd, "
                    + "for example 2019-12-02.");
        }
    }

    /**
     * Parses an event while checking its description, start, and end values.
     *
     * @param details event description, start date, and end date arguments
     * @return event represented by the arguments
     * @throws ChatbotException if required arguments are missing or a date is invalid
     */
    public Event parseEvent(String details) throws ChatbotException {
        if (details.isEmpty()) {
            throw new ChatbotException(
                    "An event needs a description, '/from', and '/to'. "
                            + "Try: event meeting /from 2019-12-02 /to 2019-12-03");
        }
        if (details.equals("/from") || details.startsWith("/from ")) {
            throw new ChatbotException("An event needs a description before '/from'.");
        }
        int fromIndex = findMarker(details, "/from", 0);
        if (fromIndex < 0) {
            throw new ChatbotException(
                    "An event needs '/from'. "
                            + "Try: event meeting /from 2019-12-02 /to 2019-12-03");
        }
        int toIndex = findMarker(details, "/to", fromIndex + 6);
        if (toIndex < 0) {
            throw new ChatbotException(
                    "An event needs '/to'. "
                            + "Try: event meeting /from 2019-12-02 /to 2019-12-03");
        }
        String description = details.substring(0, fromIndex).trim();
        String from = details.substring(fromIndex + 6, toIndex).trim();
        String to = details.substring(toIndex + 4).trim();
        if (description.isEmpty()) {
            throw new ChatbotException("An event needs a description before '/from'.");
        }
        if (from.isEmpty()) {
            throw new ChatbotException("An event needs a start date after '/from'.");
        }
        if (to.isEmpty()) {
            throw new ChatbotException("An event needs an end date after '/to'.");
        }
        try {
            return new Event(description,
                    LocalDate.parse(from, Task.INPUT_DATE_FORMAT),
                    LocalDate.parse(to, Task.INPUT_DATE_FORMAT));
        } catch (DateTimeParseException e) {
            throw new ChatbotException("Invalid event date. Please use yyyy-MM-dd for both dates, "
                    + "for example /from 2019-12-02 /to 2019-12-03.");
        }
    }

    /**
     * Parses and validates a one-based task number, returning its zero-based index.
     *
     * @param numberText user-supplied task number
     * @param command command name used in validation guidance
     * @param taskCount number of tasks currently available
     * @return zero-based index corresponding to the task number
     * @throws ChatbotException if the number is missing, non-numeric, or out of range
     */
    public int parseTaskIndex(String numberText, String command, int taskCount)
            throws ChatbotException {
        if (numberText.isEmpty()) {
            throw new ChatbotException("Please provide a task number. Try: " + command + " 2");
        }
        final int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            throw new ChatbotException("The task number must be a number. Try: " + command + " 2");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new ChatbotException("That task number does not exist.");
        }
        return taskNumber - 1;
    }

    /**
     * Parses the date supplied to the schedule command.
     *
     * @param dateText date in {@code yyyy-MM-dd} format
     * @return parsed schedule date
     * @throws ChatbotException if the date is missing or invalid
     */
    public LocalDate parseScheduleDate(String dateText) throws ChatbotException {
        if (dateText.isEmpty()) {
            throw new ChatbotException(
                    "Please provide a schedule date. Try: schedule 2019-12-02");
        }
        try {
            return LocalDate.parse(dateText, Task.INPUT_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new ChatbotException("Invalid schedule date. Please use yyyy-MM-dd, "
                    + "for example 2019-12-02.");
        }
    }

    /**
     * Parses and validates the phrase supplied to the find command.
     *
     * @param keyword search phrase supplied by the user
     * @return validated search phrase
     * @throws ChatbotException if the search phrase is empty
     */
    public String parseFindKeyword(String keyword) throws ChatbotException {
        if (keyword.isEmpty()) {
            throw new ChatbotException("Please provide a search keyword. Try: find book");
        }
        return keyword;
    }

    /**
     * Finds a standalone command marker that is followed by whitespace or ends the input.
     *
     * @param details arguments to search
     * @param marker marker text, such as {@code /by}
     * @param fromIndex index at which to begin searching
     * @return index of the marker's leading space, or {@code -1} if it is absent
     */
    private int findMarker(String details, String marker, int fromIndex) {
        String markerWithLeadingSpace = " " + marker;
        int index = details.indexOf(markerWithLeadingSpace, fromIndex);
        while (index >= 0) {
            int markerEnd = index + markerWithLeadingSpace.length();
            if (markerEnd == details.length() || Character.isWhitespace(details.charAt(markerEnd))) {
                return index;
            }
            index = details.indexOf(markerWithLeadingSpace, index + 1);
        }
        return -1;
    }
}
