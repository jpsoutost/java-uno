package academy.mindswap.server.commands.gameCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.messages.GameMessages;

/**
 * A class that represents the command to finish the turn of a player.
 */
public class FinishHandler implements GameCommandHandler {

    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) throws Exception {
        if (game.getHasToChooseAColor()) {
            throw new Exception(GameMessages.CHOOSE_COLOR);
        }

        if (clientConnectionHandler.getDeck().size() == 1 && !game.getSaidUNO()) {
            game.getPlayerToPlay().send(GameMessages.UNO_MISSED);
            game.getServer().roomBroadcast(game, clientConnectionHandler.getName(),
                    GameMessages.UNO_MISSED_ROOM);
            game.drawCard();
            game.drawCard();
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
