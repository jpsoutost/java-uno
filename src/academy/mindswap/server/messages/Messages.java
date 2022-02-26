package academy.mindswap.server.messages;

public class Messages {
    public static final String DEFAULT_NAME = "PLAYER-";
    public static final String PLAYER_ENTERED_LOBBY = " entered in the lobby.";
    public static final String NO_SUCH_COMMAND = "⚠️ Invalid command!";
    public static final String COMMANDS_LIST = """
            List of available commands:
            /list player -> gets you the list of connected players
            /list server -> gets yout the list of already created servers
            /ready -> to start playing UNO
            /play <card name> <card color> -> play that exact card in your hand
            /quit -> exits the server""";
    public static final String PLAYER_DISCONNECTED = " left the lobby.";
    public static final String WHISPER_INSTRUCTIONS = "Invalid whisper use. Correct use: '/whisper <username> <message>";
    public static final String PLAY_CARD_INSTRUCTIONS = "⚠️ Invalid use of play command: '/play <card name> <card color>";
    public static final String NO_SUCH_CLIENT = "The client you want to whisper to doesn't exists.";
    public static final String WHISPER = "(whisper)";
    public static final String PLAYER_ERROR = "Something went wrong with this player's connection. Error: ";
    public static final String PLAYER_QUIT_ROOM = " left the room and you became unready.";
    public static final String WELCOME =

            "\n" +
            "░██╗░░░░░░░██╗███████╗██╗░░░░░░█████╗░░█████╗░███╗░░░███╗███████╗░\n" +
            "░██║░░██╗░░██║██╔════╝██║░░░░░██╔══██╗██╔══██╗████╗░████║██╔════╝░\n" +
            "░╚██╗████╗██╔╝█████╗░░██║░░░░░██║░░╚═╝██║░░██║██╔████╔██║█████╗░░░\n" +
            "░░████╔═████║░██╔══╝░░██║░░░░░██║░░██╗██║░░██║██║╚██╔╝██║██╔══╝░░░\n" +
            "░░╚██╔╝░╚██╔╝░███████╗███████╗╚█████╔╝╚█████╔╝██║░╚═╝░██║███████╗░\n" +
            "░░░╚═╝░░░╚═╝░░╚══════╝╚══════╝░╚════╝░░╚════╝░╚═╝░░░░░╚═╝╚══════╝░";

}
