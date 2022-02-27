package academy.mindswap.server.commands.gameCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.messages.Messages;

public class UnoCommandHandler implements GameCommandHandler {
    Server server;
    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) {
        if (game.getPlayerToPlay().getDeck().size() == 1) {
            game.setCanPlayLastCard(true);
            game.canPlayACard();
        }
    }
}
