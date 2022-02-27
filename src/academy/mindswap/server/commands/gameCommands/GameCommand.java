package academy.mindswap.server.commands.gameCommands;

public enum GameCommand {
    FINISH_TURN("f", new FinishHandler()),
    DRAW("d", new DrawHandler()),
    CHANGE_TO_BLUE("b", new ColorChangeHandler()),
    CHANGE_TO_YELLOW("y", new ColorChangeHandler()),
    CHANGE_TO_RED("r", new ColorChangeHandler()),
    CHANGE_TO_GREEN("g", new ColorChangeHandler()),
    NOT_LEGAL("NotLegal", new NotLegalHandler()),
    PLAY("play", new PlayHandler()),
    SEE_PLAYER_DECK("h", new SeeHandHandler()),
    SEE_CARD_ON_THE_TABLE("c", new CardOnTheTableHandler()),
    QUIT("q", new QuitGameHandler()),
    UNO("uno", new UnoCommandHandler());


    private final String description;
    private final GameCommandHandler gameHandler;

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