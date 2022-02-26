package academy.mindswap.server;

public enum CardColors {
    BLUE("blue", Colors.BLUE),
    RED("red", Colors.RED),
    YELLOW("yellow", Colors.YELLOW),
    GREEN("green", Colors.GREEN);

    private String description;
    private String consoleColors;

    CardColors(String description, String consoleColors) {
        this.description = description;
        this.consoleColors = consoleColors;
    }

    public String getDescription() {
        return description;
    }

    public String getConsoleColers() {
        return consoleColors;
    }
}
