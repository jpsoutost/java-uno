package academy.mindswap.server.messages;

import academy.mindswap.server.ConsoleColors;

/**
 * A class that represents the server messages to send to the client.
 */
public class ServerMessages {
    public static final String ROOM_FULL = ConsoleColors.WHITE + "Room is full.";
    public static final String NO_OPEN_ROOMS = ConsoleColors.WHITE + "There is no open rooms";
    public static final String WHILE_PLAYING_COMMAND = ConsoleColors.WHITE + "You can't use a server command " +
            "inside a game.";
    public static final String JOIN_WRONG = ConsoleColors.WHITE + "Wrong way to join a Room.";
    public static final String NO_SUCH_ROOM = ConsoleColors.WHITE + "No such room.";
    public static final String JOINED_ROOM = ConsoleColors.WHITE + "You joined room ";
    public static final String PLAYER_ENTERED_ROOM = ConsoleColors.WHITE + " entered the room.";
    public static final String ALREADY_CREATED_ROOM = ConsoleColors.WHITE + "Room with that name already created.";
    public static final String ROOM_CREATED = ConsoleColors.WHITE + "You created room ";
    public static final String PLAYER_LEFT = ConsoleColors.WHITE + "Someone left the room and you became unready.";
    public static final String PLAYER_OUT = ConsoleColors.WHITE + "You can't be ready if you are not in a room.";
    public static final String PLAYER_ALONE = ConsoleColors.WHITE + "You can't play alone.";
    public static final String PLAYER_READY = ConsoleColors.WHITE + "You are ready.";
    public static final String PLAYER_NOT_READY = ConsoleColors.WHITE + "You are not ready.";
    public static final String I_AM_READY = ConsoleColors.WHITE + "I am ready.";
    public static final String I_AM_NOT_READY = ConsoleColors.WHITE + "I am not ready.";
    public static final String PLAYER_ERROR = ConsoleColors.WHITE + "Something went wrong with this player's " +
            "connection. Error: ";
    public static final String CHOOSE_USERNAME = ConsoleColors.WHITE + "Choose username: ";
    public static final String USERNAME_INVALID = ConsoleColors.WHITE + "Username Already in use.";
    public static final String NO_SUCH_COMMAND = ConsoleColors.WHITE + "⚠️ Invalid command!";
    public static final String PLAYER_ENTERED_LOBBY = ConsoleColors.WHITE + " entered in the lobby.";
    public static final String REDIRECTED_LOBBY = ConsoleColors.WHITE + "You were redirected to the lobby.";
    public static final String PLAYER_NOT_IN_ROOM = ConsoleColors.WHITE + "You are not in a room";
    public static final String COMMANDS_LIST = ConsoleColors.WHITE + """
            List of available commands:
            /players -> gets you a list of all the players connected to the server;
            /openRooms-> gets you a list of all open room that you can join, and information about it;
            /createRoom roomName -> creates a room and puts you inside the room;
            /joinRoom roomName -> you join an open room;
            /ready -> you get ready to play;
            /quitRoom -> you leave the room in which you are in;
            /quit -> you leave the server.""";
    public static final String WELCOME = ConsoleColors.WHITE +

            "\n" +
                    "░██╗░░░░░░░██╗███████╗██╗░░░░░░█████╗░░█████╗░███╗░░░███╗███████╗░\n" +
                    "░██║░░██╗░░██║██╔════╝██║░░░░░██╔══██╗██╔══██╗████╗░████║██╔════╝░\n" +
                    "░╚██╗████╗██╔╝█████╗░░██║░░░░░██║░░╚═╝██║░░██║██╔████╔██║█████╗░░░\n" +
                    "░░████╔═████║░██╔══╝░░██║░░░░░██║░░██╗██║░░██║██║╚██╔╝██║██╔══╝░░░\n" +
                    "░░╚██╔╝░╚██╔╝░███████╗███████╗╚█████╔╝╚█████╔╝██║░╚═╝░██║███████╗░\n" +
                    "░░░╚═╝░░░╚═╝░░╚══════╝╚══════╝░╚════╝░░╚════╝░╚═╝░░░░░╚═╝╚══════╝░\n";

}
