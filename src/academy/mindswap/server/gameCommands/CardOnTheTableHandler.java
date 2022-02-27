package academy.mindswap.server.gameCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;

/**
 * A class that represents the card on the table.
 */
public class CardOnTheTableHandler implements GameCommandHandler{
    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) {
        clientConnectionHandler.send(game.getLastCardPlayed().toString());
    }
}
