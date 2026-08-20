import java.util.Scanner;

public class Stephen {
    private static final int MAX_TASKS = 100;

    /**
     * Runs the chatbot and stores tasks entered during the current session.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] tasks = new String[MAX_TASKS];
        boolean[] isDone = new boolean[MAX_TASKS];
        int taskCount = 0;

        String line = "\n________________________________________________";
        String line1 = "Hello! I'm Stephen.";
        String line2 = "What can I do for you?";
        String line3 = "Bye. Hope to see you again soon!";

        System.out.println(line1);
        System.out.println(line2);
        System.out.println(line);

        while (true) {
            String input = scanner.nextLine();

            if (input.equals("bye")) {
                System.out.println(line);
                System.out.println(line3);
                System.out.println(line);
                break;
            }

            System.out.println(line);
            if (input.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    String statusIcon = isDone[i] ? "X" : " ";
                    System.out.println((i + 1) + ".[" + statusIcon + "] " + tasks[i]);
                }
            } else if (input.startsWith("mark ")) {
                int taskIndex = Integer.parseInt(input.substring(5)) - 1;
                isDone[taskIndex] = true;
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("[X] " + tasks[taskIndex]);
            } else {
                tasks[taskCount] = input;
                taskCount++;
                System.out.println("added: " + input);
            }
            System.out.println(line);
        }
    }
}
