import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Simple automated checks for task serialization and file saving. */
public class StorageTest {
    /** Runs all storage checks without requiring an external test library. */
    public static void main(String[] args) throws Exception {
        Path testRoot = Path.of("out", "storage-test");
        Path nestedDirectory = testRoot.resolve("nested");
        Path dataFile = nestedDirectory.resolve("tasks.txt");
        Files.deleteIfExists(dataFile);
        Files.deleteIfExists(nestedDirectory);
        Files.deleteIfExists(testRoot);
        Storage storage = new Storage(dataFile);

        Todo todo = new Todo("read | revise\\notes");
        Deadline deadline = new Deadline("submit report", "Friday");
        Event event = new Event("project meeting", "Mon 2pm", "Mon 4pm");
        deadline.markAsDone();

        List<Task> tasks = new ArrayList<>(List.of(todo, deadline, event));
        storage.save(tasks);
        assertLines(dataFile, List.of(
                "T | 0 | read \\| revise\\\\notes",
                "D | 1 | submit report | Friday",
                "E | 0 | project meeting | Mon 2pm | Mon 4pm"));

        todo.markAsDone();
        tasks.remove(deadline);
        storage.save(tasks);
        assertLines(dataFile, List.of(
                "T | 1 | read \\| revise\\\\notes",
                "E | 0 | project meeting | Mon 2pm | Mon 4pm"));

        System.out.println("StorageTest: all checks passed");
    }

    /** Checks exact UTF-8 save-file lines. */
    private static void assertLines(Path file, List<String> expected) throws Exception {
        List<String> actual = Files.readAllLines(file, StandardCharsets.UTF_8);
        if (!actual.equals(expected)) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }
}
