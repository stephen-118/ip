/**
 * Represents the fixed types of tasks supported by the chatbot.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String symbol;

    /**
     * Creates a task type with the symbol used in task displays.
     *
     * @param symbol short symbol shown before a task
     */
    TaskType(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the symbol used to display this task type.
     *
     * @return task type symbol
     */
    public String getSymbol() {
        return symbol;
    }
}
