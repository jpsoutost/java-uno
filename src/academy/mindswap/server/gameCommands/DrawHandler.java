package academy.mindswap.server.gameCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;

public class DrawHandler implements GameCommandHandler {
    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) {

        if (game.hasCardsToDraw()) {
            game.goFishingCards();
            return;
        }

        if (game.isFirstCardOfTurn()) {
            game.drawCard();
        } else if (game.getHasToChooseAColor()) {
            clientConnectionHandler.send("You have to choose a color. b-blue, y-yellow, g-green, r-red");
        }else{
            clientConnectionHandler.send("You can't draw more than one card each turn nor if you have already played a card.");
        }
    }
}
