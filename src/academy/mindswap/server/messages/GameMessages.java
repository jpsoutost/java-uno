package academy.mindswap.server.messages;

import academy.mindswap.server.ConsoleColors;

public class GameMessages {
    public static final String CHOOSE_USERNAME = "Choose username: ";
    public static final String USERNAME_INVALID = "Username Already in use.";
    public static final String END_TURN = "End of Turn";
    public static final String CANT_CHANGE = "You can´t change the color";
    public static final String NO_OPEN_ROOM = "There is no open rooms";
    public static final String NOT_PLAYED = "You have to play or draw a card first.";
    public static final String JUST_ONE_CARD = "You can't draw more than one card each turn nor if you have already played a card.";
    public static final String COLOR_CHANGED = "Color changed to:  ";
    public static final String NOT_LEGAL = "Play not legal.";
    public static final String NOT_ALLOWED = "Play not allowed";
    public static final String THE_WINNER = "You finished your deck and you are the winner.";
    public static final String YOU_DRAW = "You draw a ";
    public static final String PLAYER_DRAW = " draw a card.";
    public static final String CHOOSE_COLOR = "Choose color: b-blue, y-yellow, r-red, g-green";
    public static final String PLAYER_QUIT_ROOM = " left the room and you became unready.";
    public static final String PLAYER_DISCONNECTED = " left the lobby.";
    public static final String NO_SUCH_COMMAND = "⚠️ Invalid command!";
    public static final String PLAYER_ENTERED_LOBBY = " entered in the lobby.";
    public static final String PLAY = "play";

    public static final String COMMANDS_LIST = """
            List of available commands:
            /list player -> gets you the list of connected players
            /list server -> gets yout the list of already created servers
            /ready -> to start playing UNO
            /play <card name> <card color> -> play that exact card in your hand
            /quit -> exits the server""";

    public static final String WELCOME =

            "\n" +
                    "░██╗░░░░░░░██╗███████╗██╗░░░░░░█████╗░░█████╗░███╗░░░███╗███████╗░\n" +
                    "░██║░░██╗░░██║██╔════╝██║░░░░░██╔══██╗██╔══██╗████╗░████║██╔════╝░\n" +
                    "░╚██╗████╗██╔╝█████╗░░██║░░░░░██║░░╚═╝██║░░██║██╔████╔██║█████╗░░░\n" +
                    "░░████╔═████║░██╔══╝░░██║░░░░░██║░░██╗██║░░██║██║╚██╔╝██║██╔══╝░░░\n" +
                    "░░╚██╔╝░╚██╔╝░███████╗███████╗╚█████╔╝╚█████╔╝██║░╚═╝░██║███████╗░\n" +
                    "░░░╚═╝░░░╚═╝░░╚══════╝╚══════╝░╚════╝░░╚════╝░╚═╝░░░░░╚═╝╚══════╝░";


    public static final String UNO = ConsoleColors.PURPLE +

            "\n" +
                    "░██╗░░░██╗███╗░░██╗░█████╗░██╗░\n" +
                    "░██║░░░██║████╗░██║██╔══██╗██║░\n" +
                    "░██║░░░██║██╔██╗██║██║░░██║██║░\n" +
                    "░██║░░░██║██║╚████║██║░░██║╚═╝░\n" +
                    "░██████╔╝██║░╚███║╚█████╔╝░██╗░\n" +
                    "░╚═════╝░╚═╝░░╚══╝░╚════╝░░╚═╝░";

    public static final String CARD1 = " _____ ";
    public static final String CARD2 = "|*   *|";
    public static final String CARD3 = " ----- ";

    public static final String CARD_ON_TABLE1 = "\n _________ \n" +
            "|*       *| \n" +
            "|         |";
    public static final String CARD_ON_TABLE2 = "|         | \n" +
            "|*       *| \n" +
            " --------- ";
}
