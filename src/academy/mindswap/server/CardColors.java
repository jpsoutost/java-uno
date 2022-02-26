package academy.mindswap.server;

public enum CardColors {
    BLUE("blue"),
    RED("red"),
    YELLOW("yellow"),
    GREEN("green");

    private final String description;

    CardColors(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
