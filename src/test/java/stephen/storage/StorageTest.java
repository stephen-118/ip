package stephen.storage;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import stephen.task.Deadline;
import stephen.task.Event;
import stephen.task.Task;
import stephen.task.Todo;

/** Automated checks for task serialization, saving, and loading. */
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

        assertTaskData(storage.load(), List.of());
        if (Files.exists(dataFile) || Files.exists(nestedDirectory)) {
            throw new AssertionError("Loading a missing file must not create data paths");
        }

        Files.createDirectories(nestedDirectory);
        Files.writeString(dataFile, "", StandardCharsets.UTF_8);
        assertTaskData(storage.load(), List.of());

        Todo todo = new Todo("read | revise\\notes");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2019, 12, 2));
        Event event = new Event("project meeting",
                LocalDate.of(2019, 12, 2), LocalDate.of(2019, 12, 3));
        deadline.markAsDone();

        List<Task> tasks = new ArrayList<>(List.of(todo, deadline, event));
        storage.save(tasks);
        assertLines(dataFile, List.of(
                "T | 0 | read \\| revise\\\\notes",
                "D | 1 | submit report | 2019-12-02",
                "E | 0 | project meeting | 2019-12-02 | 2019-12-03"));
        assertTaskData(storage.load(), List.of(
                "T | 0 | read \\| revise\\\\notes",
                "D | 1 | submit report | 2019-12-02",
                "E | 0 | project meeting | 2019-12-02 | 2019-12-03"));

        todo.markAsDone();
        tasks.remove(deadline);
        storage.save(tasks);
        assertLines(dataFile, List.of(
                "T | 1 | read \\| revise\\\\notes",
                "E | 0 | project meeting | 2019-12-02 | 2019-12-03"));
        assertTaskData(storage.load(), List.of(
                "T | 1 | read \\| revise\\\\notes",
                "E | 0 | project meeting | 2019-12-02 | 2019-12-03"));

        Files.write(dataFile, List.of(
                "",
                "not a task",
                "T | 0 | valid todo",
                "D | maybe | invalid status | 2019-12-02",
                "D | 1 | invalid deadline | tomorrow",
                "D | 1 | valid deadline | 2020-02-29",
                "E | 0 | missing end | 2019-12-02",
                "E | 1 | invalid event | 2pm | 4pm",
                "E | 1 | valid event | 2019-12-02 | 2019-12-03",
                "T | 0 | broken escape\\q"), StandardCharsets.UTF_8);
        assertTaskData(storage.load(), List.of(
                "T | 0 | valid todo",
                "D | 1 | valid deadline | 2020-02-29",
                "E | 1 | valid event | 2019-12-02 | 2019-12-03"));

        System.out.println("StorageTest: all checks passed");
    }

    /** Checks tasks by serializing the loaded objects back to the canonical format. */
    private static void assertTaskData(List<Task> tasks, List<String> expected) {
        List<String> actual = tasks.stream().map(Task::toDataString).toList();
        if (!actual.equals(expected)) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    /** Checks exact UTF-8 save-file lines. */
    private static void assertLines(Path file, List<String> expected) throws Exception {
        List<String> actual = Files.readAllLines(file, StandardCharsets.UTF_8);
        if (!actual.equals(expected)) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }
}
