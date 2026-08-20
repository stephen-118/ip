import java.util.Scanner;

public class Stephen {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

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
            System.out.println(input);
            System.out.println(line);
        }

    }
}
