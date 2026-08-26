import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Loads and saves the task list on disk. */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage that writes to the given path.
     *
     * @param filePath location of the task data file
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads every valid task from the data file.
     * Missing files, empty files, blank lines, and individual malformed lines are
     * treated as recoverable conditions.
     *
     * @return valid tasks in file order
     * @throws IOException if an existing file cannot be read
     */
    public List<Task> load() throws IOException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseTask(line));
            } catch (IllegalArgumentException e) {
                // Skip only the corrupted record so other saved tasks can still load.
            }
        }
        return tasks;
    }

    /**
     * Replaces the data file with the current serialized task list.
     * The parent directory is created on the first save when necessary.
     *
     * @param tasks tasks to save in list order
     * @throws IOException if the directory or file cannot be written
     */
    public void save(List<Task> tasks) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        List<String> lines = tasks.stream().map(Task::toDataString).toList();
        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }

    /** Converts one validated save-file line back into its concrete task type. */
    private Task parseTask(String line) {
        List<String> fields = splitEscapedFields(line);
        if (fields.size() < 3) {
            throw new IllegalArgumentException("Too few task fields");
        }

        String type = fields.get(0);
        String status = fields.get(1);
        String description = fields.get(2);
        if (description.isEmpty() || (!status.equals("0") && !status.equals("1"))) {
            throw new IllegalArgumentException("Invalid task description or status");
        }

        Task task;
        if (type.equals("T") && fields.size() == 3) {
            task = new Todo(description);
        } else if (type.equals("D") && fields.size() == 4 && !fields.get(3).isEmpty()) {
            task = new Deadline(description, fields.get(3));
        } else if (type.equals("E") && fields.size() == 5
                && !fields.get(3).isEmpty() && !fields.get(4).isEmpty()) {
            task = new Event(description, fields.get(3), fields.get(4));
        } else {
            throw new IllegalArgumentException("Invalid task type or field count");
        }

        if (status.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /** Splits pipe-delimited fields while decoding the save format's escapes. */
    private List<String> splitEscapedFields(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean isEscaped = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (isEscaped) {
                if (character == 'n') {
                    field.append('\n');
                } else if (character == 'r') {
                    field.append('\r');
                } else if (character == '|' || character == '\\') {
                    field.append(character);
                } else {
                    throw new IllegalArgumentException("Unknown escape sequence");
                }
                isEscaped = false;
            } else if (character == '\\') {
                isEscaped = true;
            } else if (character == '|') {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }
        if (isEscaped) {
            throw new IllegalArgumentException("Incomplete escape sequence");
        }
        fields.add(field.toString().trim());
        return fields;
    }
}
