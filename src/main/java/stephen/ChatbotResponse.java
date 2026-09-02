package stephen;

/** Contains the chatbot's response to one user command and whether the session should end. */
public record ChatbotResponse(String message, boolean isExit) {
}
