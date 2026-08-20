import java.util.Scanner;

public class Stephen {
    private static final int MAX_TASKS = 100;
    private static final String SEPARATOR = "\n________________________________________________";

    /**
     * Runs the chatbot and stores tasks entered during the current session.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        System.out.println("Hello! I'm Stephen.");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);

        while (true) {
            String input = scanner.nextLine();

            if (input.equals("bye")) {
                System.out.println(SEPARATOR);
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(SEPARATOR);
                break;
            }

            System.out.println(SEPARATOR);
            if (input.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + "." + tasks[i]);
                }
            } else if (input.startsWith("mark ")) {
                int taskIndex = Integer.parseInt(input.substring(5)) - 1;
                tasks[taskIndex].markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println(tasks[taskIndex]);
            } else if (input.startsWith("unmark ")) {
                int taskIndex = Integer.parseInt(input.substring(7)) - 1;
                tasks[taskIndex].markAsNotDone();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println(tasks[taskIndex]);
            } else if (input.startsWith("todo ")) {
                tasks[taskCount] = new Todo(input.substring(5));
                taskCount++;
                printAddedTask(tasks[taskCount - 1], taskCount);
            } else if (input.startsWith("deadline ")) {
                String details = input.substring(9);
                int byIndex = details.indexOf(" /by ");
                String description = details.substring(0, byIndex);
                String by = details.substring(byIndex + 5);
                tasks[taskCount] = new Deadline(description, by);
                taskCount++;
                printAddedTask(tasks[taskCount - 1], taskCount);
            } else if (input.startsWith("event ")) {
                String details = input.substring(6);
                int fromIndex = details.indexOf(" /from ");
                int toIndex = details.indexOf(" /to ", fromIndex + 7);
                String description = details.substring(0, fromIndex);
                String from = details.substring(fromIndex + 7, toIndex);
                String to = details.substring(toIndex + 5);
                tasks[taskCount] = new Event(description, from, to);
                taskCount++;
                printAddedTask(tasks[taskCount - 1], taskCount);
            }
            System.out.println(SEPARATOR);
        }
    }

    /**
     * Prints the standard confirmation after adding a task.
     *
     * @param task task that was added
     * @param taskCount number of tasks currently stored
     */
    private static void printAddedTask(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }
}
