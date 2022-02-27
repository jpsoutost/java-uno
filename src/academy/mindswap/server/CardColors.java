package academy.mindswap.server;

/**
 * A ENUM class that represents card colors.
 */
public enum CardColors {
    BLUE("blue", ConsoleColors.BLUE),
    RED("red", ConsoleColors.RED),
    YELLOW("yellow", ConsoleColors.YELLOW),
    GREEN("green", ConsoleColors.GREEN);

    private String description;
    private String consoleColors;

    CardColors(String description, String consoleColors) {
        this.description = description;
        this.consoleColors = consoleColors;
    }

    public String getDescription() {
        return description;
    }

    public String getConsoleColors() {
        return consoleColors;
    }
}
