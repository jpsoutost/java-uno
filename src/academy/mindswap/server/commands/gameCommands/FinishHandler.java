package academy.mindswap.server.commands.gameCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.messages.GameMessages;

/**
 * A class that represents the finish handler.
 */
public class FinishHandler implements GameCommandHandler {

    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) throws Exception {
        if (game.getHasToChooseAColor()) {
            throw new Exception(GameMessages.CHOOSE_COLOR);
        }

        if (clientConnectionHandler.getDeck().size() == 1 && !game.getCanPlayLastCard()) {
            game.setCardsToDraw(2);
            game.getPlayerToPlay().send("You did not say UNO!");
            return;
        }

        if (game.canFinishTurn()) {
            clientConnectionHandler.send(GameMessages.END_TURN);

            game.setNextPlayerToPlay();
            game.resetBooleansAndAccumulators();


        } else {
            throw new Exception(GameMessages.NOT_PLAYED);
        }
    }



}
