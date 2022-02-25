package academy.mindswap.server.gameCommands;

public enum GameCommand {
    FINISH_TURN("f", new FinishHandler()),
    DRAW("d", new DrawHandler()),
    CHANGE_TO_BLUE("blue", new BlueColorHandler()),
    CHANGE_TO_YELLOW("yellow", new YellowColorHandler()),
    CHANGE_TO_RED("red", new RedColorHandler()),
    CHANGE_TO_GREEN("green", new GreenColorHandler());


    private String description;
    private GameCommandHandler gameHandler;

    GameCommand(String description, GameCommandHandler gameHandler) {
        this.description = description;
        this.gameHandler = gameHandler;
    }

    public static GameCommand getGameCommandFromDescription(String description) {
        for (GameCommand command : values()) {
            if (description.equals(command.description)) {
                return command;
            }
        }
        return null;
    }

    public GameCommandHandler getCommandHandler() {
        return gameHandler;
    }
}
