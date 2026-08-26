import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Automated checks for command parsing, execution, and exit signaling. */
public class CommandTest {
    /** Runs the command checks without requiring an external test library. */
    public static void main(String[] args) throws Exception {
        Path testRoot = Path.of("out", "command-test");
        Path dataFile = testRoot.resolve("tasks.txt");
        Files.createDirectories(testRoot);
        Files.deleteIfExists(dataFile);

        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of());
        Storage storage = new Storage(dataFile);
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
        try {
            Ui ui = new Ui();

            executeAndCheckType(parser, "todo read book", tasks, ui, storage, AddCommand.class);
            executeAndCheckType(parser, "deadline submit report /by 2019-12-02",
                    tasks, ui, storage, AddCommand.class);
            executeAndCheckType(parser,
                    "event project meeting /from 2019-12-02 /to 2019-12-03",
                    tasks, ui, storage, AddCommand.class);
            assertTaskData(tasks, List.of(
                    "T | 0 | read book",
                    "D | 0 | submit report | 2019-12-02",
                    "E | 0 | project meeting | 2019-12-02 | 2019-12-03"));

            executeAndCheckType(parser, "mark 1", tasks, ui, storage, MarkCommand.class);
            assertTaskData(tasks, List.of(
                    "T | 1 | read book",
                    "D | 0 | submit report | 2019-12-02",
                    "E | 0 | project meeting | 2019-12-02 | 2019-12-03"));
            executeAndCheckType(parser, "unmark 1", tasks, ui, storage, UnmarkCommand.class);
            executeAndCheckType(parser, "delete 2", tasks, ui, storage, DeleteCommand.class);
            assertTaskData(tasks, List.of(
                    "T | 0 | read book",
                    "E | 0 | project meeting | 2019-12-02 | 2019-12-03"));

            assertCommandType(parser.parse("list", tasks), ListCommand.class);
            assertCommandType(parser.parse("schedule 2019-12-02", tasks), ScheduleCommand.class);
            Command exit = parser.parse("bye", tasks);
            assertCommandType(exit, ExitCommand.class);
            if (!exit.isExit()) {
                throw new AssertionError("ExitCommand must signal application exit");
            }
            if (parser.parse("list", tasks).isExit()) {
                throw new AssertionError("Non-exit commands must not signal application exit");
            }

            assertParseError(parser, tasks, "mark 99", "That task number does not exist.");
            assertParseError(parser, tasks, "unknown", "I don't recognise that command.");

            List<String> savedLines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);
            List<String> expectedLines = tasks.getTasks().stream().map(Task::toDataString).toList();
            if (!savedLines.equals(expectedLines)) {
                throw new AssertionError("Expected saved tasks " + expectedLines
                        + " but was " + savedLines);
            }
        } finally {
            System.setOut(originalOutput);
        }

        System.out.println("CommandTest: all checks passed");
    }

    /** Parses, type-checks, and executes one command. */
    private static void executeAndCheckType(Parser parser, String input, TaskList tasks,
            Ui ui, Storage storage, Class<? extends Command> expectedType) throws Exception {
        Command command = parser.parse(input, tasks);
        assertCommandType(command, expectedType);
        command.execute(tasks, ui, storage);
    }

    /** Checks that a parsed command has the expected concrete type. */
    private static void assertCommandType(Command command,
            Class<? extends Command> expectedType) {
        if (!expectedType.isInstance(command)) {
            throw new AssertionError("Expected " + expectedType.getSimpleName()
                    + " but was " + command.getClass().getSimpleName());
        }
    }

    /** Checks the exact error produced by invalid input. */
    private static void assertParseError(Parser parser, TaskList tasks, String input,
            String expectedMessage) throws Exception {
        try {
            parser.parse(input, tasks);
            throw new AssertionError("Expected parsing to fail for: " + input);
        } catch (ChatbotException e) {
            if (!e.getMessage().equals(expectedMessage)) {
                throw new AssertionError("Expected error '" + expectedMessage
                        + "' but was '" + e.getMessage() + "'");
            }
        }
    }

    /** Checks tasks through their canonical storage representation. */
    private static void assertTaskData(TaskList tasks, List<String> expected) {
        List<String> actual = tasks.getTasks().stream().map(Task::toDataString).toList();
        if (!actual.equals(expected)) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }
}
