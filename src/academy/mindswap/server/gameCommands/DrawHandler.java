package academy.mindswap.server.gameCommands;

import academy.mindswap.server.Card;
import academy.mindswap.server.Game;
import academy.mindswap.server.Server;

public class DrawHandler implements GameCommandHandler {
    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) {
            if (!game.isPlayerPlayedAlreadyOneCard()) {
                Card newCard = game.getDeck().poll();
                game.getPlayerToPlay().getDeck().add(newCard);
                //game.getPlayerToPlay().send("You draw a " + newCard);
                clientConnectionHandler.send("You have to play or draw a card first.");
                game.getServer().roomBroadcast(game, clientConnectionHandler.getName(),
                        clientConnectionHandler.getName() + " draw a card.");

                game.setCanFinishTurn(true);
            } else {
                clientConnectionHandler.send("You can't draw more than one card each turn nor if you have already played a card.");
            }
    }
}
