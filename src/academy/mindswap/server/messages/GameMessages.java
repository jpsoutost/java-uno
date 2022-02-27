package academy.mindswap.server.messages;

import academy.mindswap.server.ConsoleColors;

public class GameMessages {

    public static final String END_TURN = ConsoleColors.PURPLE + "End of Turn";
    public static final String CANT_CHANGE_COLOR = ConsoleColors.PURPLE + "You can´t change the color";
    public static final String NOT_PLAYED = ConsoleColors.PURPLE + "You have to play or draw a card first.";
    public static final String JUST_ONE_CARD = ConsoleColors.PURPLE + "You can't draw more than one card " +
            "each turn nor if you have already played a card.";
    public static final String COLOR_CHANGED = "Color changed to:  ";
    public static final String NOT_LEGAL = ConsoleColors.PURPLE + "Play not legal.";
    public static final String NOT_ALLOWED = ConsoleColors.PURPLE + "Play not allowed";
    public static final String YOU_THE_WINNER = ConsoleColors.PURPLE + "You finished your deck and you are the winner.";
    public static final String PLAYER_THE_WINNER = ConsoleColors.PURPLE + " finished his deck and he is the winner.";
    public static final String YOU_DRAW = ConsoleColors.PURPLE + "You draw a ";
    public static final String PLAYER_DRAW = ConsoleColors.PURPLE + " draw a card.";
    public static final String CHOOSE_COLOR = ConsoleColors.PURPLE + "Choose color: b-blue, y-yellow, r-red, g-green";
    public static final String PLAYER_QUIT_ROOM = ConsoleColors.PURPLE + " left the room and you became unready.";
    public static final String PLAYER_DISCONNECTED = ConsoleColors.PURPLE + " left the lobby.";
    public static final String PLAY = "play";
    public static final String NOTLEGAL = "NotLegal";

    public static final String UNO = ConsoleColors.PURPLE +

            "\n" +
                    "░██╗░░░██╗███╗░░██╗░█████╗░██╗░\n" +
                    "░██║░░░██║████╗░██║██╔══██╗██║░\n" +
                    "░██║░░░██║██╔██╗██║██║░░██║██║░\n" +
                    "░██║░░░██║██║╚████║██║░░██║╚═╝░\n" +
                    "░██████╔╝██║░╚███║╚█████╔╝░██╗░\n" +
                    "░╚═════╝░╚═╝░░╚══╝░╚════╝░░╚═╝░";

    public static final String CARD1 = " _______ ";
    public static final String CARD2 = "|*     *|";
    public static final String CARD3 = " ------- ";

    public static final String CARD_ON_TABLE1 = "\n _________ \n" +
            "|*       *| \n" +
            "|         |";
    public static final String CARD_ON_TABLE2 = "|         | \n" +
            "|*       *| \n" +
            " --------- ";

    public static final String END_OF_GAME = ConsoleColors.PURPLE + """
            
            ███████╗███╗░░██╗██████╗░  ░█████╗░███████╗  ░██████╗░░█████╗░███╗░░░███╗███████╗
            ██╔════╝████╗░██║██╔══██╗  ██╔══██╗██╔════╝  ██╔════╝░██╔══██╗████╗░████║██╔════╝
            █████╗░░██╔██╗██║██║░░██║  ██║░░██║█████╗░░  ██║░░██╗░███████║██╔████╔██║█████╗░░
            ██╔══╝░░██║╚████║██║░░██║  ██║░░██║██╔══╝░░  ██║░░╚██╗██╔══██║██║╚██╔╝██║██╔══╝░░
            ███████╗██║░╚███║██████╔╝  ╚█████╔╝██║░░░░░  ╚██████╔╝██║░░██║██║░╚═╝░██║███████╗
            ╚══════╝╚═╝░░╚══╝╚═════╝░  ░╚════╝░╚═╝░░░░░  ░╚═════╝░╚═╝░░╚═╝╚═╝░░░░░╚═╝╚══════╝
            """;
}
