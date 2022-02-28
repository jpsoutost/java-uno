package academy.mindswap.server.commands.gameCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;

/**
 * A command that shows the card on the table.
 */
public class CardOnTheTableHandler implements GameCommandHandler {
    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) {
        clientConnectionHandler.send(game.getLastCardPlayed().toString());
    }
}
