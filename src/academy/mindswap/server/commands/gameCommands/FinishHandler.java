package academy.mindswap.server.commands.gameCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.messages.GameMessages;

/**
 * A class that represents the finish handler.
 */
public class FinishHandler implements GameCommandHandler {

    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) {
        if (game.getHasToChooseAColor()) {
            clientConnectionHandler.send(GameMessages.CHOOSE_COLOR);
            return;
        }

        if (game.canFinishTurn()) {
            clientConnectionHandler.send(GameMessages.END_TURN);

            game.setNextPlayerToPlay();
            game.resetBooleansAndAccumulators();

        } else {
            clientConnectionHandler.send(GameMessages.NOT_PLAYED);
        }
    }
}
