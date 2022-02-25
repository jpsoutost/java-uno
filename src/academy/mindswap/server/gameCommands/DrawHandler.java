package academy.mindswap.server.gameCommands;

import academy.mindswap.server.Card;
import academy.mindswap.server.Game;
import academy.mindswap.server.Server;

public class DrawHandler implements GameCommandHandler {
    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) {

        if (!game.isPlayerPlayedAlreadyOneCard()) {
            Card newCard = game.getDeck().poll();
            clientConnectionHandler.getDeck().add(newCard);
            clientConnectionHandler.send("You draw a " + newCard);
            game.getServer().roomBroadcast(game, clientConnectionHandler.getName(),
                    clientConnectionHandler.getName() + " draw a card.");
            game.setCanFinishTurn(true);
        } else if (game.getHasToChooseAColor()) {
            clientConnectionHandler.send("You have to choose a color. b-blue, y-yellow, g-green, r-red");
        }else{
            clientConnectionHandler.send("You can't draw more than one card each turn nor if you have already played a card.");
        }
    }
}
