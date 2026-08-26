package stephen.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import stephen.task.Deadline;
import stephen.task.Event;
import stephen.task.Task;
import stephen.task.Todo;

/** JUnit tests for repeatable task persistence and malformed-record recovery. */
class StorageJUnitTest {
    @TempDir
    Path tempDirectory;

    @Test
    void load_missingFile_returnsEmptyListWithoutCreatingFile() throws IOException {
        Path dataFile = tempDirectory.resolve("missing").resolve("tasks.txt");

        assertEquals(List.of(), new Storage(dataFile).load());
        assertFalse(Files.exists(dataFile));
        assertFalse(Files.exists(dataFile.getParent()));
    }

    @Test
    void saveAndLoad_allTaskTypesStatusEscapesAndDuplicates_roundTrip() throws IOException {
        Path dataFile = tempDirectory.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        Todo special = new Todo("line one\nline two | path\\notes");
        Deadline deadline = new Deadline("submit", LocalDate.of(2024, 2, 29));
        Event event = new Event("conference",
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 3));
        deadline.markAsDone();

        storage.save(List.of(special, deadline, event, special));

        assertEquals(List.of(
                "T | 0 | line one\\nline two \\| path\\\\notes",
                "D | 1 | submit | 2024-02-29",
                "E | 0 | conference | 2024-03-01 | 2024-03-03",
                "T | 0 | line one\\nline two \\| path\\\\notes"),
                serialized(storage.load()));
    }

    @Test
    void save_emptyList_replacesExistingContents() throws IOException {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        storage.save(List.of(new Todo("old task")));

        storage.save(List.of());

        assertEquals(List.of(), storage.load());
        assertEquals(List.of(), Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    void load_blankAndMalformedRecords_skipsOnlyBadLines() throws IOException {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Files.write(dataFile, List.of(
                "",
                "T | 0 | first",
                "T | 0 | first",
                "unknown",
                "T | 2 | invalid status",
                "T | 0 | broken escape\\q",
                "D | 1 | bad date | 2023-02-29",
                "E | 0 | missing end | 2024-01-01",
                "E | 1 | valid | 2024-01-01 | 2024-01-02"), StandardCharsets.UTF_8);

        assertEquals(List.of(
                "T | 0 | first",
                "T | 0 | first",
                "E | 1 | valid | 2024-01-01 | 2024-01-02"),
                serialized(new Storage(dataFile).load()));
    }

    /** Converts tasks to their stable persisted representation for exact comparison. */
    private static List<String> serialized(List<Task> tasks) {
        return tasks.stream().map(Task::toDataString).toList();
    }
}
