package academy.mindswap.server.commands.gameCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.messages.GameMessages;

/**
 * A class that represents the game command to execute when the player has a not legal play.
 */
public class NotLegalHandler implements GameCommandHandler{
    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) {
        clientConnectionHandler.send(GameMessages.NOT_LEGAL);
    }
}
