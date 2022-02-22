package academy.mindswap.server.commands;

public enum Command {
    LIST_PLAYERS("/players", new PlayerListHandler()),
    LIST_ROOMS("/openRooms", new RoomListHandler()),
    HELP("/help", new HelpHandler()),
    WHISPER("/whisper", new WhisperHandler()),
    MUTE("/mute", new QuitHandler()),
    NEW_ROOM("/createRoom", new NewRoomHandler()),
    JOIN_ROOM("/JoinRoom", new JoinRoomHandler()),
    READY("/Ready", new ReadyHandler());

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
