package academy.mindswap.server.commands.gameCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;

public class FinishHandler implements GameCommandHandler {

    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) {

        if(game.getHasToChooseAColor()) {
            clientConnectionHandler.send("You have to choose a color first. b-blue, y-yellow, g-green, r-red");
            return;
        }
        if (game.getPlayerToPlay().getDeck().size() == 1 && !game.getCanPlayLastCard()) {
            game.setCardsToDraw(2);
            game.getPlayerToPlay().send("You did not say UNO!");
            return;
        }

        if (game.canFinishTurn()) {
            clientConnectionHandler.send("End of Turn");

            game.setNextPlayerToPlay();
            game.resetBooleansAndAccumulators();


        } else {
            clientConnectionHandler.send("You have to play or draw a card first.");
        }
    }



}
