package stephen.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task-list searches across descriptions. */
class TaskListTest {
    @Test
    void find_oneMatchingTask_returnsMatch() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Todo("buy groceries")));

        assertEquals(List.of("read book"), descriptions(tasks.find("read")));
    }

    @Test
    void find_multipleMatches_preservesOriginalOrder() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Deadline("return book", LocalDate.of(2024, 6, 15)),
                new Todo("buy groceries")));

        assertEquals(List.of("read book", "return book"), descriptions(tasks.find("book")));
    }

    @Test
    void find_noMatchingTask_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertEquals(List.of(), tasks.find("groceries"));
    }

    @Test
    void find_differentCapitalization_matchesDescription() {
        TaskList tasks = new TaskList(List.of(new Todo("Read Book")));

        assertEquals(List.of("Read Book"), descriptions(tasks.find("rEaD bOoK")));
    }

    @Test
    void find_multiWordPhrase_matchesCompletePhrase() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read project book"),
                new Todo("read book tonight")));

        assertEquals(List.of("read book tonight"), descriptions(tasks.find("read book")));
    }

    @Test
    void find_repeatedKeywordInDescription_returnsTaskOnce() {
        TaskList tasks = new TaskList(List.of(new Todo("book book book")));

        assertEquals(List.of("book book book"), descriptions(tasks.find("book")));
    }

    /** Converts matching tasks to their descriptions for concise assertions. */
    private static List<String> descriptions(List<Task> tasks) {
        return tasks.stream().map(task -> task.description).toList();
    }
}
