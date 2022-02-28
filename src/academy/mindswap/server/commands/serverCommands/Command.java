package academy.mindswap.server.commands.serverCommands;

/**
 * A ENUM that sets the game commands.
 */
public enum Command {
    LIST_PLAYERS("/players", new PlayerListHandler()),
    LIST_ROOMS("/openRooms", new RoomListHandler()),
    HELP("/help", new HelpHandler()),
    NEW_ROOM("/createRoom", new NewRoomHandler()),
    JOIN_ROOM("/joinRoom", new JoinRoomHandler()),
    READY("/ready", new ReadyHandler()),
    QUIT_ROOM("/quitRoom", new QuitRoomHandler()),
    QUIT("/quit", new QuitHandler());

    private final String description;
    private final CommandHandler handler;

    /**
     * Method that initializes the ENUM with two parameters.
     * @param description The name of the command.
     * @param handler     The class executed by the respective command.
     */
    Command(String description, CommandHandler handler) {
        this.description = description;
        this.handler = handler;
    }

    /**
     * Method that get command from description.
     * @param description The name of the command.
     * @return Respective command or null.
     */
    public static Command getCommandFromDescription(String description) {
        for (Command command : values()) {
            if (description.equals(command.description)) {
                return command;
            }
        }
        return null;
    }

    //GETTER
    public CommandHandler getHandler() {
        return handler;
    }
}