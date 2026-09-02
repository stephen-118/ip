package stephen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the UI-independent facade used by the JavaFX controller. */
class ChatbotTest {
    @TempDir
    Path tempDirectory;

    /** Verifies stateful command execution and clean response text. */
    @Test
    void getResponseValidCommandsPreservesStateAndOmitsConsoleDividers() {
        Chatbot chatbot = new Chatbot(tempDirectory.resolve("tasks.txt"));

        ChatbotResponse addResponse = chatbot.getResponse("todo read book");
        ChatbotResponse listResponse = chatbot.getResponse("list");

        assertEquals("Got it. I've added this task:\n"
                + "  [T][ ] read book\n"
                + "Now you have 1 tasks in the list.", addResponse.message());
        assertFalse(addResponse.isExit());
        assertEquals("Here are the tasks in your list:\n1.[T][ ] read book",
                listResponse.message());
    }

    /** Verifies that invalid input becomes friendly response text without ending the session. */
    @Test
    void getResponseInvalidCommandReturnsErrorAndContinues() {
        ChatbotResponse response = new Chatbot(tempDirectory.resolve("tasks.txt"))
                .getResponse("unknown");

        assertEquals("Oops! I don't recognise that command.", response.message());
        assertFalse(response.isExit());
    }

    /** Verifies that the existing exit command is surfaced to graphical clients. */
    @Test
    void getResponseByeReturnsFarewellAndExitSignal() {
        ChatbotResponse response = new Chatbot(tempDirectory.resolve("tasks.txt"))
                .getResponse("bye");

        assertEquals("Bye. Hope to see you again soon!", response.message());
        assertTrue(response.isExit());
    }
}
