import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Saves the current task list to disk. */
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
}
