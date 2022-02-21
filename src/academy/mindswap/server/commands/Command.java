package academy.mindswap.server.commands;

public enum Command {
    LIST_PLAYER("/players", new PlayerListHandler()),
    LIST_SERVER("/rooms", new RoomListHandler()),
    HELP("/help", new HelpHandler()),
    WHISPER("/whisper", new WhisperHandler()),
    QUIT("/quit", new QuitHandler());
    //SHOUT("/shout", new ShoutHandler());

    private String description;
    private CommandHandler handler;

    Command(String description, CommandHandler handler) {
        this.description = description;
        this.handler = handler;
    }

    public static Command getCommandFromDescription(String description) {
        for (Command command : values()) {
            if (description.equals(command.description)) {
                return command;
            }
        }
        return null;
    }

    public CommandHandler getHandler() {
        return handler;
    }
}
